package com.heiden.dbp.zuul.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.heiden.dbp.zuul.dao.entity.Log;
import org.springframework.stereotype.Repository;

@Repository("logMapper")
public interface LogMapper {

	public void insertLog(Log log);

	public void deleteLogById(String logId);

	public void updateLog(Log log);

	public void updateSensitiveLog(Log log);

	public Log findLogById(String logId);

	public List<Log> findLogList();

	public int deleteLogListByIds(String[] Ids);

	public int insertLogList(List<Log> logs);

	public void updateLogList(List<Log> logs);

	public List<Log> findLogQuery(Log log);

	public List<Log> findLogQueryPage(@Param("obj")Log log,@Param("startIndex") int startIndex,@Param("limit") int limit);

	public int findCountLogQuery(Log log);

    public List<Log> findLogByTime(@Param("obj")Log log,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime,@Param("responseStatus") String responseStatus, @Param("apiId")String[] apiId);
	
	public List<Map> findLogSubsectionStats(@Param("obj")Log log,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime,@Param("responseStatus") String responseStatus, @Param("apiId")String[] apiId,@Param("timeTemplate")String timeTemplate);
	
	public List<Map> findavgResponseTimeLog(@Param("obj")Log log, @Param("beginTime") Long beginTime,@Param("endTime") Long endTime,@Param("responseStatus") String responseStatus, @Param("apiId")String[] apiId,@Param("timeTemplate")String timeTemplate);
}