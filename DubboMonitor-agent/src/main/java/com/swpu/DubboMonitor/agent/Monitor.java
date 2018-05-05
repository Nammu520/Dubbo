package com.swpu.DubboMonitor.agent;

import com.swpu.DubboMonitor.transation.ClassTransformer;

import java.lang.instrument.Instrumentation;



public class Monitor {
	
	
    public static void premain(String agentOps, Instrumentation inst) throws Exception {
    	System.out.println("agent insert begin");
        if(System.getProperty("agent.appName")==null){
            throw new Exception("Failed to get AppName, Please add your app name in VM options by using \"-Dagent.appName=\"");
        }
        XMLReader.readFile();
        //加入拦截处理器
        //ClassFileTransformer tf = new ClassTransformer();
        inst.addTransformer(new ClassTransformer());
        System.out.println("agent insert end");
    }
}
