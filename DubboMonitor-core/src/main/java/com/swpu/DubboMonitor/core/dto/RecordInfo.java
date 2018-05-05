package com.swpu.DubboMonitor.core.dto;

import java.io.Serializable;


public class RecordInfo implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String traceID;
	private String span;
	private long time;
	private String className;
	private String methodName;
	private String threadNum;
	private boolean rpcUse;
	private Object result;
	private boolean useFlag;
	private String appID;
	private String appName;
	private int foreachCount;

	public RecordInfo(String traceID, String span, long time, String className, String methodName,
				  String threadNum, boolean rpcUse, Object result,boolean useFlag,
				  String appID,String appName) {
		this.traceID = traceID;
		this.span = span;
		this.time = time;
		this.className = className;
		this.methodName = methodName;
		this.threadNum = threadNum;
		this.rpcUse = rpcUse;
		this.result = result;
		this.useFlag = useFlag;
		this.appID = appID;
		this.appName = appName;
	}

	public RecordInfo() {
	}

	public int getForeachCount() {
		return foreachCount;
	}

	public void setForeachCount(int foreachCount) {
		this.foreachCount = foreachCount;
	}

	public String getTraceID() {
		return traceID;
	}

	public void setTraceID(String traceID) {
		this.traceID = traceID;
	}

	public String getSpan() {
		return span;
	}

	public void setSpan(String span) {
		this.span = span;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(String threadNum) {
		this.threadNum = threadNum;
	}

	public boolean getRpcUse() {
		return rpcUse;
	}

	public void setRpcUse(boolean rpcUse) {
		this.rpcUse = rpcUse;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(boolean useFlag) {
		this.useFlag = useFlag;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	//	private String
}
