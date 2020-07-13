package com.heiden.dbp.zuul.config;


import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptStringEncryptorConfig {
    private static final Logger logger = LoggerFactory.getLogger(JasyptStringEncryptorConfig.class);


    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        AESDecryptUtil aesDecryptUtil = new AESDecryptUtil();
        return aesDecryptUtil;

    }

    class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {

        private final AESDecryptUtil aesDecryptUtil;

        public MyEncryptablePropertyResolver(){
              this.aesDecryptUtil = new AESDecryptUtil();
        }

        @Override
        public String resolvePropertyValue(String value) {
            if (value != null && value.startsWith("{ENC}")) {
                return aesDecryptUtil.decrypt(value.substring("{ENC}".length()));
            }
            return value;
        }
    }

    @Bean(name="encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver() {
        return new MyEncryptablePropertyResolver();
    }
}