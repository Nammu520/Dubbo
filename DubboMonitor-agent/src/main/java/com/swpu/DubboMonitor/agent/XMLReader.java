package com.swpu.DubboMonitor.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengyu
 **/
public class XMLReader {
	private static AppInfo info = AppInfo.getInstance();

	/**
	 * 读取XML文件
	 */
	
	static void readFile(){
		File file = new File(System.getProperty("agent.dir")+"config.xml");
		System.out.println(System.getProperty("agent.dir")+"config.xml");
		if(file.exists()){
			readXMLFile(file);
		}
	}

	/**
	 * 读取文件，加入黑名单和白名单
	 * @param file xml文件
	 */
	private static void readXMLFile(File file) {
		System.out.println("hello "+file.getAbsolutePath());
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			NodeList classNames = doc.getElementsByTagName("Ignore");
			NodeList allList = doc.getElementsByTagName("Add");

//			NodeList addressNode = doc.getElementsByTagName("CodisAddress");
//			NodeList pathNode = doc.getElementsByTagName("CodisPath");
//			NodeList sessionTimeoutNode = doc.getElementsByTagName("CodisTimeout");
//
//			if(addressNode!=null&&pathNode!=null&&sessionTimeoutNode!=null) {
//				String address = addressNode.item(0).getFirstChild().getNodeValue();
//				String path = pathNode.item(0).getFirstChild().getNodeValue();
//				String sessionTimeout = sessionTimeoutNode.item(0).getFirstChild().getNodeValue();
//				System.out.println(address+path+sessionTimeout);
//			}


			for(int i=0;i<classNames.getLength();i++){
				info.addBlackListItem(classNames.item(i).getFirstChild().getNodeValue()+".*");
			}

			for(int i=0;i<allList.getLength();i++){
				info.addWhiteListItem(allList.item(i).getFirstChild().getNodeValue()+".*");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> readCodisFile() {
		File file = new File(System.getProperty("agent.dir")+"config.xml");
		if(file.exists()){
			readXMLFile(file);
		}

		List<String> result = new ArrayList<>();
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			NodeList addressNode = doc.getElementsByTagName("CodisAddress");
			NodeList pathNode = doc.getElementsByTagName("CodisPath");
			NodeList sessionTimeoutNode = doc.getElementsByTagName("CodisTimeout");

			if(addressNode!=null&&pathNode!=null&&sessionTimeoutNode!=null) {
				String address = addressNode.item(0).getFirstChild().getNodeValue();
				String path = pathNode.item(0).getFirstChild().getNodeValue();
				String sessionTimeout = sessionTimeoutNode.item(0).getFirstChild().getNodeValue();
				result.add(address);
				result.add(sessionTimeout);
				result.add(path);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
