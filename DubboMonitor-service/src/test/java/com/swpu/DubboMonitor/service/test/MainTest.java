package com.swpu.DubboMonitor.service.test;

import java.io.Serializable;

import org.junit.Assert;

import redis.clients.jedis.Jedis;

import com.swpu.DubboMonitor.core.dto.RecordInfo;
import com.swpu.DubboMonitor.service.util.RedisUtil;

public class MainTest
{

    public static void main(String[] args)
    {
        String traceID = "192.168.50.2";
        String key = "dengyuMonitor";
        // TODO Auto-generated method stub
        RedisUtil redisUtil = new RedisUtil();
        //redisUtil.initJedisPool();
       /* Jedis jedis = redisUtil.getJedis();
        Record record1 = new Record(traceID, "1", 1926488585, "www.baidu.com", "httpUseEnd", "POST", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        System.out.println(jedis.llen(key));
        jedis.lpush(redisUtil.serialize(key), redisUtil.serialize(record1));
        System.out.println(jedis.llen(key));
        Record ret = (Record)redisUtil.unserizlize(jedis.rpop(redisUtil.serialize(key)));
        System.out.println(ret);
        redisUtil.listRightPush(key, traceID);
        String lString = (String) redisUtil.listLeftPop(key);
        System.out.println(lString);*/
        
        RecordInfo record = new RecordInfo(traceID, "1", 1926488568, "www.baidu.com", "httpUseEnd", "POST", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        redisUtil.listRightPush(key, record);
        RecordInfo record1 = new RecordInfo(traceID, "1", 1926488585, "www.baidu.com", "httpUseEnd", "POST", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        redisUtil.listRightPush(key, record1);
        RecordInfo record2 = new RecordInfo(traceID, "1.1", 1926488568, "RedisUtil", "httsspUseEnd", "[ThreadNUM-Main-2]", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        redisUtil.listRightPush(key, record2);
        RecordInfo record3 = new RecordInfo(traceID, "1.1", 1926488569, "RedisUtil", "httsspUseEnd", "[ThreadNUM-Main-2]", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        redisUtil.listRightPush(key, record3);
        RecordInfo record4 = new RecordInfo(traceID, "1.1.1", 1926488568, "RedisUtil", "httsspUseEnd", "[ThreadNUM-Main-2]", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        redisUtil.listRightPush(key, record4);
        RecordInfo record5 = new RecordInfo(traceID, "1.1.1", 1926488569, "RedisUtil", "httsspUseEnd", "[ThreadNUM-Main-2]", true, "java.class.String", true, "1585661ds4a651sd", "appAAA");
        redisUtil.listRightPush(key, record5);
        System.out.println(redisUtil.lsize(key));
        //Record tempRecord = (Record)redisUtil.listLeftPop(key);
        //System.out.println(tempRecord.getTraceID());
    }

}
class t implements Serializable{
    String key;
}
