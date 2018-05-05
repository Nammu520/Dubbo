package com.swpu.DubboMonitor.agent;

import com.swpu.DubboMonitor.record.Collector;
import com.swpu.DubboMonitor.record.Record;
import com.swpu.DubboMonitor.traceClass.Trace;
import com.swpu.DubboMonitor.utils.SpanUtils;
import com.swpu.DubboMonitor.utils.UID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 拦截其中插入的各项方法
 * @see com.swpu.DubboMonitor.transation.ClassTransformer#transform
 */
public class Interceptor {
    private static ThreadLocal<Trace> data;

    private static AppInfo appInfo = AppInfo.getInstance();

    private static Logger logger = LoggerFactory.getLogger(Interceptor.class);

    /**
     * http请求初始化
     * @param request 传入HTTP Request
     * @param response 传入HTTP Response
     */
    public static void start(HttpServletRequest request, HttpServletResponse response) {

        if(data==null){
            data = new ThreadLocal<>();
        }

        String traceId = request.getHeader("traceId");
        String span = request.getHeader("span");
        String remainderString = request.getHeader("remainder");
        String appId = request.getHeader("appId");
        String appName = request.getHeader("appName");

        Trace trace;
        //如果其中数据为空，则表明其中有数据丢失，或者其中存在
        if(StringUtils.isBlank(traceId)||StringUtils.isBlank(span)||
                StringUtils.isBlank(appId)||StringUtils.isBlank(appName)||
                StringUtils.isBlank(remainderString)) {

            trace = new Trace(UID.next());
            trace.appId = UID.next();
            data.set(trace);

        }else {
            int remainder = Integer.valueOf(remainderString);
            trace = new Trace(traceId,span,remainder);
            trace.appId = UID.next();
            data.set(trace);
        }

        Collector.addOne(new Record(data.get().traceId, data.get().span, System.currentTimeMillis(),request.getRequestURI(),
                "httpUseBegin", request.getMethod(), false, null, true, data.get().appId,appInfo.getAppName()));

    }
    
    /**
     * 非http方式的初始化，比如新建线程方式
     */
    public static void start(){
        logger.debug("Non Request usr start");

        if(data==null){
            data = new ThreadLocal<>();
        }

        Trace trace = new Trace(UID.next());
        trace.appId = UID.next();
        data.set(trace);
    }

    /**
     * 记录一次调用
     * @param name 方法名
     * @param className 类名
     */
    public static void use(String name,String className){


        if(data==null||data.get()==null){
            return;
        }
        logger.debug("use start");

        Trace trace =  data.get();
        SpanUtils.addOneSpan(trace);

        Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), className,
                name, Thread.currentThread().toString(), trace.RPCStatus, null,true,trace.appId,appInfo.getAppName()));
    }

    /**
     * 记录一次调用结束
     * @param name 方法名
     * @param className 类名
     */
    public static void beforeReturn(String name,String className){

        if(data==null||data.get()==null){
            return;
        }

        logger.debug("use End");
        Trace trace = data.get();
        Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), className,
                name, Thread.currentThread().toString(), trace.RPCStatus, null,false,trace.appId,
                appInfo.getAppName()));
        SpanUtils.reduceSpan(trace);
    }

    public static String getTraceId(){
        if(data==null||data.get()==null){
            return null;
        }
        return data.get().traceId;
    }

    public static String getSpan(){
        if(data==null||data.get()==null){
            return null;
        }
        return data.get().span;
    }

    public static int getRemainder(){
        if(data==null||data.get()==null){
            return 0;
        }
        return data.get().remainder;
    }


    public static String getAppId(){
        if(data==null||data.get()==null){
            return null;
        }
        return data.get().appId;
    }

    public static String getAppName(){
        return appInfo.getAppName();
    }

    /**
     * RPC调用后，添加RPC提供者调用链
     * @param traceId ID
     * @param span 调用链信息
     * @param remainder 当前调用次序
     * @param appId appID
     * @param appName 传递来的实例名
     */
    public static void addRPCTrace(String traceId, String span, int remainder, String appId, String appName){

        if(StringUtils.isBlank(traceId)||StringUtils.isBlank(span)||
                StringUtils.isBlank(appId)||StringUtils.isBlank(appName)){
    	    return;
		}

		Collector.write();
        if(data==null){
            data = new ThreadLocal<>();
        }
        data.set(null);
        Trace trace = new Trace(traceId,span,remainder);
        if(!appInfo.getAppName().equals(appName)){
            trace.appId = UID.next();
        }else {
            trace.appId = appId;
        }
        trace.RPCStatus = true;

        data.set(trace);
    }

    public static void endRPC(){
        if (data==null){
            return;
        }
        data.get().remainder ++;
    }

    /**
     * 记录当前SQL语句
     * @param sql SQL语句
     */
    static public void addSQL(String sql){
        if(data==null||data.get()==null){
            return;
        }
        Trace trace =  data.get();

        SpanUtils.addOneSpan(trace);

        Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), "SQL USE",
                sql, Thread.currentThread().toString(), trace.RPCStatus, null,true,trace.appId,
                appInfo.getAppName()));
        Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), "SQL USE",
                sql, Thread.currentThread().toString(), trace.RPCStatus, null,true,trace.appId,
                appInfo.getAppName()));

        SpanUtils.reduceSpan(trace);
    }


    /**
     * 发生http请求时调用该方法，会产生一条特殊日志
     * @param traceId ID
     * @param span  调用链信息
     * @param remainderString 传递的当前调用次序
     * @param appId 传递的appId
     * @param appName 传递实例名称
     */
    public static void httpBegin(String traceId, String span, String remainderString, String appId,
                                 String appName,String method,String requestURI){
        if (data == null) {
            data = new ThreadLocal<>();
        }

        Trace trace;
        //如果其中数据为空，则表明其中有数据丢失，或者其中存在
        if (StringUtils.isBlank(traceId) || StringUtils.isBlank(span) ||
                StringUtils.isBlank(appId) || StringUtils.isBlank(appName) ||
                StringUtils.isBlank(remainderString)) {

            trace = new Trace(UID.next());
            trace.appId = UID.next();
            data.set(trace);
            logger.debug("Request Info Null");
        } else {
            int remainder = Integer.valueOf(remainderString);
            trace = new Trace(traceId, span, remainder);
            trace.appId = UID.next();
            data.set(trace);
            logger.debug("Request Info Not Null");
        }

        Collector.addOne(new Record(data.get().traceId, data.get().span, System.currentTimeMillis(), requestURI!=null?requestURI:"Null Request",
                "httpUseBegin", method!=null?method:"Null Request", false, null, true, data.get().appId, appInfo.getAppName()));

    }

    /**
     * Http调用结束时需要调用的方法
     */
    public static void httpEnd(){
        try {
            if (data == null || data.get() == null) {
                return;
            }

            Trace trace = data.get();

            Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), "Http Use End",
                    "httpUseEnd", null, false, null, false, trace.appId,
                    appInfo.getAppName()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 插入disconf，用于下载codis配置文件
     * @param config codis内部数据
     */
    public static void modifyDisconf(List<String> config){
        if(!config.contains("cache.properties")){
            config.add("cache.properties");
        }
    }

    public static void codisUse(String name){
        if(data==null||data.get()==null){
            return;
        }
        Trace trace =  data.get();

        SpanUtils.addOneSpan(trace);

        Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), name,
                "codisUse", Thread.currentThread().toString(), trace.RPCStatus, null,true,trace.appId,
                appInfo.getAppName()));
        Collector.addOne(new Record(trace.traceId, trace.span, System.currentTimeMillis(), name,
                "codisUse", Thread.currentThread().toString(), trace.RPCStatus, null,false,trace.appId,
                appInfo.getAppName()));

        SpanUtils.reduceSpan(trace);
    }

}
