package com.swpu.DubboMonitor.record;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swpu.DubboMonitor.utils.RedisUtil;



/**
 * 数据缓冲区以及写入类
 */
public class Collector {

	private static ConcurrentLinkedQueue<Record> writeQueue;
	private static ConcurrentLinkedQueue<Record> readQueue;
	private static ConcurrentLinkedQueue<Record> bufferOne = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Record> bufferTwo = new ConcurrentLinkedQueue<>();

	static Logger logger = LoggerFactory.getLogger(Collector.class);
	static final String CODIS_KEY = "dengyuMonitor";

	private static boolean writeToOne = true;
	static boolean loopFlag = true;
	private static boolean stopFlag = false;

	private static WriteThread writeThread = new WriteThread();
	private static boolean isStart = false;

	private static RedisUtil redis;
	static {
		writeQueue = bufferOne;
		readQueue = bufferTwo;
	}


//	private static void initService() throws Exception {
//		if (RedisUtil.returnResource(jedis);==null) {
//			List<String> stringList = XMLReader.readCodisFile();
//			codisService = new CodisServiceImpl(stringList.get(0), Integer.valueOf(stringList.get(1)), stringList.get(2));
//		}
//	}

	/**
	 * 开启线程
	 */

	public static void write(){
		try {
			if (!isStart) {
				try {
					initService();
				} catch (Exception e) {
					e.printStackTrace();
					stopFlag = true;
					return;
				}
				new Thread(writeThread).start();
				isStart = true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	private static void initService() throws Exception {
        if (redis==null) {
            redis = new RedisUtil();
        }
    }
	/**
	 * 添加一条标准调用日志
	 * @param record 记录实例
	 */
	static public void addOne(Record record){
		if(stopFlag){
			return;
		}
		writeQueue.add(record);
	}

	@Deprecated
	static public void addOneAppUse(Record record){
		if(stopFlag){
			return;
		}
		writeQueue.add(record);
	}

	/**
	 * 线程实现类
	 */
	static class WriteThread implements Runnable{

		public void run() {

			while (loopFlag){
				if(readQueue.isEmpty()){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				while (!readQueue.isEmpty()){
				    Record record = readQueue.poll();
				    logger.info(record.toString());
					redis.listRightPush(CODIS_KEY,record);
				}

				if(writeToOne){
					writeQueue = bufferTwo;
					readQueue = bufferOne;
					writeToOne = false;
				}else {
					writeQueue = bufferOne;
					readQueue = bufferTwo;
					writeToOne = true;
				}
			}
		}
	}


}
