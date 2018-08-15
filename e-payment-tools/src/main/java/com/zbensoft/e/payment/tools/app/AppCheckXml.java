package com.zbensoft.e.payment.tools.app;

import java.io.File; 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class AppCheckXml {

	private static void check() {
		// TODO Auto-generated method stub
		//数组存放路径：
		//路径改为自己当前位置的路径
		String path = "E:\\liux-svn\\epayment\\01开发\\01代码\\app\\android\\clap\\app\\src\\main\\res\\";
		String[] files = {  "values-en\\strings.xml", "values-es\\strings.xml", "values-zh\\strings.xml"};
		  
		Document document = getAreas(path+files[0]);
		//获取根节点
		Element root = document.getRootElement();
		Element stringEn=null;
		String name1=null;
		Attribute nameAttr = null;
		List list = new ArrayList();
		//遍历string-en.xml文件
		for (Iterator i = root.elementIterator("string"); i.hasNext();) {  
            stringEn = (Element) i.next(); 
            nameAttr = stringEn.attribute("name");
            name1 = nameAttr.getValue();
            list.add(name1);

		}

		List list2=new ArrayList();
		for (int i = 1; i < files.length; i++) {
			Document documentOther = getAreas(path + files[i]);
			Element root1 = documentOther.getRootElement();
			
			for (Iterator iterator = root1.elementIterator("string"); iterator.hasNext();) {
				Element  other= (Element) iterator.next();
				Attribute nameAttrOther = other.attribute("name");
				String name2 = nameAttrOther.getValue();
				list2.add(name2);
//				System.out.println(name2);
				//判断如果string-en文件中不包含相同的name属性，输出该name属性。
				if (!list.contains(name2)) {
					
					System.err.println(String.format("%s key not in %s,key = %s", files[0], files[i],name2));
				}	
				
			}

			//判断如果string-es或者string-zh文件中不包含string-en的属性，输出该name属性。，
			for(Iterator itr = root.elementIterator("string");itr.hasNext();){
				Element en = (Element) itr.next();
				Attribute nameAttrEn = en.attribute("name");
				String nameEn = nameAttrEn.getValue();
				if(!list2.contains(nameEn)){
					System.err.println(String.format("%s key not in %s,key = %s", files[i], files[0],nameEn));
				}
			}
			
		}
		
	}
	
	public static Document getAreas(String path){
		 Document document = null;  
		    try {  
		        SAXReader saxReader = new SAXReader();  
		        document = saxReader.read(new File(path)); // 读取XML文件,获得document对象  
		    } catch (Exception ex) {  
		        ex.printStackTrace();  
		    }  
		return document;
		
	}
	
	
	public static void main(String[] args) {
		check();
	}

	
}
