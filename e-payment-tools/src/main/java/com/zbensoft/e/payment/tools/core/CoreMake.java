package com.zbensoft.e.payment.tools.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class CoreMake {
	public static void main(String[] args) {
		new CoreMake().doMake();
	}

	public void doMake() {
		String projectPath = System.getProperty("user.dir").replaceAll("e-payment-tools", "");//"C:\\workspace\\sts\\e-payment";
		runScript("cmd /c c: && cd " + projectPath
				+ "&& mvn clean package -f pom-core.xml -D maven.test.skip=true -P test -e");
		
		System.out.println("copy e-payment-core-0.0.4-SNAPSHOT.jar ----> api e-payment-core-0.0.3-SNAPSHOT.jar");
		delFile(projectPath + "/e-payment-api/lib/e-payment-core-0.0.3-SNAPSHOT.jar");
		copyFile(projectPath + "/e-payment-core/target/e-payment-core-0.0.4-SNAPSHOT.jar",
				projectPath + "/e-payment-api/lib/e-payment-core-0.0.3-SNAPSHOT.jar");
		System.out.println("copy end");

	}

	public static void runScript(String cmd) {
		System.out.println("执行命令：" + cmd);
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedReader inBr = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				System.out.println(lineStr);// 打印输出信息
			}
			// 检查命令是否执行失败。
			if (p.waitFor() != 0) {
				if (p.exitValue() == 1)// p.exitValue()==0表示正常结束，1：非正常结束
					System.err.println("命令执行失败!");
			}
			inBr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
}
