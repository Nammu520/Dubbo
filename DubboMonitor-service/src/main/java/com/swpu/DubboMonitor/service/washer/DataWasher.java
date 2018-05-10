package com.swpu.DubboMonitor.service.washer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.swpu.DubboMonitor.core.MethodManager;
import com.swpu.DubboMonitor.core.RequestManager;
import com.swpu.DubboMonitor.core.dto.MethodTemp;
import com.swpu.DubboMonitor.core.dto.RequestTemp;
import com.swpu.DubboMonitor.core.util.TransferUtil;
import com.swpu.DubboMonitor.core.dto.RecordInfo;
import com.swpu.DubboMonitor.service.common.WasherGobal;
import com.swpu.DubboMonitor.service.util.RedisUtil;

/**
 * 数据清洗函数类
 * @author: dengyu
 */
public class DataWasher
{
    @Autowired
    MethodManager methodManager;

    @Autowired
    RequestManager requestManager;

    @Autowired
    RedisUtil redis;
    /** 
     * 处理方法打印的日志
     * @param Reocrd(日志)
     */
    private static Logger logger = LoggerFactory.getLogger(DataWasher.class);
    
    public void dealOneRecord(RecordInfo record)
    {
        MethodTemp temp = null;
        boolean flag=false;
        String span = record.getSpan();
        String traceID = record.getTraceID();
        try {
			
	        if (redis.exists(traceID))
	        {
	            temp = redis.hget(traceID, span,new TypeReference<MethodTemp>(){});
	            if (temp != null)
	            {
	                int index = span.lastIndexOf('.');
	                if (index != -1 && span.substring(0, index).indexOf('.') != -1)
	                {
	                    String parentSpan = span.substring(0, index);
	                    MethodTemp tempParen = redis.hget(traceID, parentSpan,new TypeReference<MethodTemp>(){});
	                    if (tempParen != null)
	                    {
	                        temp.setParentId(tempParen.getId());
	                    }
	                    else 
	                    {
	                        flag=true;
	                    }
	                }
	                temp = TransferUtil.transferMethodTemp(temp, record);
	                if(!StringUtils.isEmpty(temp.getClassName()) && temp.getClassName().equals("SQL USE"))
	                {
	                    String tempStr = temp.getMethodName().replaceAll("\\s+", " ");
	                    temp.setMethodName(tempStr);
	                }
	                logger.info("插入调用链日志到数据库:{}",JSON.toJSONString(temp));
	                methodManager.insertOneMethod(temp);
	                if(flag)
	                {
	                    WasherGobal.methodId.add(temp.getId());
	                }
	            }
	            else
	            {
	                redis.hset(traceID, span, TransferUtil.recordToMethodTemp(record));
	            }
	        }
	        else
	        {
	            redis.hset(traceID, span, TransferUtil.recordToMethodTemp(record));
	            redis.expire(traceID, WasherGobal.CACHE_TIME_OUT);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    
    /** 
     * 处理请求打印的日志
     * @param Reocrd(日志)
     */
    public void dealOneHttpRecord(RecordInfo record)
    {
        RequestTemp temp = null;
        String span = record.getSpan();
        boolean flag = false;
        String traceID = record.getTraceID();
        try {
	        if (redis.exists(traceID))
	        {
	            temp = redis.hget(traceID, span,new TypeReference<RequestTemp>(){});
	            if (temp != null)
	            {
	                if (span.indexOf('.') != -1)
	                {
	                    span = span.substring(4);
	                    MethodTemp tempParen = redis.hget(traceID, span,new TypeReference<MethodTemp>(){});
	                    if (tempParen != null)
	                    {
	                        temp.setParentId(tempParen.getAppId());
	                    }
	                    else 
	                    {
	                        flag=true; 
	                    }
	                }
	                temp = TransferUtil.transferRequestTemp(temp, record);
	                logger.info("插入调用链日志到数据库:{}",JSON.toJSONString(temp));
	                requestManager.insertOneRequest(temp);
	                if(flag)
	                {
	                    String id=traceID+","+temp.getAppId();
	                    WasherGobal.requestId.add(id);
	                }
	            }
	            else
	            {
	                redis.hset(traceID, span, TransferUtil.recordToRequesTemp(record));
	            }
	        }
	        else
	        {
	            redis.hset(traceID, span, TransferUtil.recordToRequesTemp(record));
	            redis.expire(traceID, WasherGobal.CACHE_TIME_OUT);
	        }
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
}
