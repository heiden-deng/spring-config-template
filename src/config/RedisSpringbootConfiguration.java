package com.heiden.dbp.zuul.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.Data;

/**
 * redis客户端配置
 *
 * @author heiden
 */
@Data
@Configuration
@ConfigurationProperties(prefix = RedisSpringbootConfiguration.PREFIX)
public class RedisSpringbootConfiguration {
	public static final String PREFIX = "spring.redis";

	public static final String NODES = "nodes";

	public static final String MASTER = "master";
	
	public static final String SINGLE_REDIS = "0";
	
	public static final String CLUSTER_REDIS="1";
	
	public static final String SENTINEL_REDIS="2";
	private String host;

	private int port;

	private String password;
	
	private String enabledClusterSentinel;
	
	private Map<String, String> sentinel = new HashMap<>();

	private Map<String, String> cluster = new HashMap<>();

	private static StringEncryptor stringEncryptor = new JasyptStringEncryptorConfig().stringEncryptor();

	public String decrypt(String value) {
		if (value.startsWith("{ENC}")) {
			value = value.substring("{ENC}".length());
			value = stringEncryptor.decrypt(value);
		}
		return value;
	}

	
	
	
	/**
	 * redis:手动配置Factory
	 *
	 * @return
	 */

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory cf = null;
		boolean flag = true;
		if (CLUSTER_REDIS.equals(enabledClusterSentinel)&&!cluster.isEmpty()&& StringUtils.isNotEmpty(decrypt(cluster.get(NODES)))) {
			
			String nodes = decrypt(cluster.get(NODES));
			List<String> nodeList = new ArrayList<>();
			nodeList.add(nodes);
			cf = new JedisConnectionFactory(new RedisClusterConfiguration(nodeList));
			cf.setPassword(decrypt(password));

		} else if (SENTINEL_REDIS.equals(enabledClusterSentinel)&&!sentinel.isEmpty() && StringUtils.isNotEmpty(decrypt(sentinel.get(NODES)))) {

			cf = new JedisConnectionFactory(getRedisSentinelConfiguration());
			cf.setPassword(decrypt(password));

		} else {
			cf = new JedisConnectionFactory();
			cf.setHostName(decrypt(host));
			cf.setPort(port);
			cf.setPassword(decrypt(password));
		}

		cf.afterPropertiesSet();
		return cf;
	}

	
	public RedisSentinelConfiguration getRedisSentinelConfiguration() {
		String masterName = decrypt(sentinel.get(MASTER));
		String sentinelNode = decrypt(sentinel.get(NODES));

		if (StringUtils.isEmpty(masterName)) {
			throw new NullPointerException("sentinel 模式  master 不能为空或者 null !");
		}

		Objects.requireNonNull(sentinelNode, "nodes 不能为空或者null !");
		HashSet<String> sentinelNodes = new HashSet<>(Arrays.asList(sentinelNode.split(",")));

		return new RedisSentinelConfiguration(masterName, sentinelNodes);
	}

	/**
	 * 创建一个操作redis的客户端对象
	 *
	 * @param factory
	 * @return
	 */
	//
	@Bean
	public RedisSerializer fastJson2JsonRedisSerializer() {
		return new FastJson2JsonRedisSerializer<Object>(Object.class);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory,
			RedisSerializer fastJson2JsonRedisSerializer) {
		// 使用Jackson2JsonRedisSerialize 替换默认序列化
		StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
		redisTemplate.setConnectionFactory(factory);

		// redis 开启事务
		// redisTemplate.setEnableTransactionSupport(true);
		// hash 使用jdk 的序列化
		redisTemplate.setHashValueSerializer(
				fastJson2JsonRedisSerializer/*
											 * new
											 * JdkSerializationRedisSerializer()
											 */);
		// StringRedisSerializer key 序列化
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		// keySerializer 对key的默认序列化器。默认值是StringSerializer
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		// valueSerializer
		redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	/*
	 * @Bean public <K extends Serializable, V extends Serializable>
	 * RedisTemplate<K, V> redisTemplate( RedisConnectionFactory factory) {
	 * RedisTemplate<K, V> template = new JdkRedisTemplate<K, V>();
	 * template.setConnectionFactory(factory); return template; }
	 * 
	 * private static class JdkRedisTemplate<K, V> extends RedisTemplate<K, V> {
	 * public JdkRedisTemplate() { JdkSerializationRedisSerializer
	 * jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
	 * setKeySerializer(jdkSerializationRedisSerializer);
	 * setValueSerializer(jdkSerializationRedisSerializer);
	 * setHashKeySerializer(jdkSerializationRedisSerializer);
	 * setHashValueSerializer(jdkSerializationRedisSerializer); } }
	 */

}