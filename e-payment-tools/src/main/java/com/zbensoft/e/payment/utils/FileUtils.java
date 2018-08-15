package com.zbensoft.e.payment.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
 
/**
 * 处理WAR文件工具类。可压缩或解压缩WAR文件。
 * 
 * @author Xiong Shuhong(shelltea@gmail.com)
 */
public class FileUtils {
	
	public static void copyFile(String oldPath, String newPath) {
	    try {
	      int bytesum = 0;
	      int byteread = 0;
	      File oldfile = new File(oldPath);
	      if (oldfile.exists()) { //文件存在时
	        InputStream inStream = new FileInputStream(oldPath); //读入原文件
	        FileOutputStream fs = new FileOutputStream(newPath);
	        byte[] buffer = new byte[1444];
	        int length;
	        while ( (byteread = inStream.read(buffer)) != -1) {
	          bytesum += byteread; //字节数 文件大小
//	          System.out.println(bytesum);
	          fs.write(buffer, 0, byteread);
	        }
	        inStream.close();
	      }
	    }
	    catch (Exception e) {
	      System.out.println("复制单个文件操作出错");
	      e.printStackTrace();
	    }
	}
	
	public static void delFile(String filePathAndName) {
	    try {
	      String filePath = filePathAndName;
	      filePath = filePath.toString();
	      java.io.File myDelFile = new java.io.File(filePath);
	      myDelFile.delete();
	    }
	    catch (Exception e) {
	      System.out.println("删除文件操作出错");
	      e.printStackTrace();
	    }
	}
}