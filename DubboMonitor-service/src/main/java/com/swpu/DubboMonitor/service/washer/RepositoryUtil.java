package com.swpu.DubboMonitor.service.washer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.TypeReference;
import com.swpu.DubboMonitor.core.dto.MethodTemp;
import com.swpu.DubboMonitor.core.dto.RecordInfo;
import com.swpu.DubboMonitor.service.common.WasherGobal;
import com.swpu.DubboMonitor.service.util.RedisUtil;


/**
 * 工厂类，主要用来获取日志
 * @author: dengyu
 */
public class RepositoryUtil
{

    public HashSet<String> keySets = new HashSet<String>();
    public List<RecordInfo> records = new ArrayList<RecordInfo>();
    
    @Autowired
    private RedisUtil redis;
    /** 
     * 遍历list，如果没有冲突，返回一条日志，如果有就从codis中取出一条日志返回
     * @return Record
     */
    public RecordInfo getRecord()
    {
        try
        {
            if (!CollectionUtils.isEmpty(records))
            {
                synchronized (RepositoryUtil.class)
                {
                    for (int i = 0; i < records.size(); i++)
                    {
                        RecordInfo record = records.get(i);
                        String combineKey = record.getTraceID() + record.getSpan();
                        if (!keySets.contains(combineKey))
                        {
                            keySets.add(combineKey);
                            records.remove(record);
                            return record;
                        }
                    }
                }
            }
            return getRecordFromCodis();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    /** 
     * 日志处理完成后，从keySet中移除这条日志的trace和span组成的联合主键
     * @param key
     */
    public void removeKey(String key)
    {
        keySets.remove(key);
    }

    /** 
     * 从codis的队列中取出一条日志，如果该日志的trace和span组成的联合主键在keySet中存在，则将这条日志放到list中去，然后继续从codis中取，直到取出一条日志为止
     * @return Record
     */
    public RecordInfo getRecordFromCodis() throws InterruptedException
    {
        boolean flag = true;
        RecordInfo record = null;
        while (flag)
        {
            record = (RecordInfo)redis.listLeftPop(WasherGobal.KEY_DLMONTITOR,new TypeReference<RecordInfo>(){});
            if (record != null)
            {
                synchronized (RepositoryUtil.class)
                {
                    if(StringUtils.isEmpty(record.getTraceID()) || StringUtils.isEmpty(record.getSpan()))
                    {
                        continue;
                    }
                    String combineKey = record.getTraceID() + record.getSpan();
                    if (keySets.contains(combineKey))
                    {
                        records.add(record);
                    }
                    else
                    {
                        keySets.add(combineKey);
                        return record;
                    }
                }
            }
            else
            {
                Thread.sleep(200);
            }
        }
        return record;
    }
}
