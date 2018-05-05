package com.swpu.DubboMonitor.utils;

public class StringUtils {
	public static boolean isBlank(String s){
		if(s==null || s.length()<1){
			return true;
		}
		return false;
	}
	public static boolean isEmpty(String s){
		return StringUtils.isBlank(s);
	}
}
