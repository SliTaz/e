package com.zbensoft.e.payment.tools.webservice.certification.pro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.zbensoft.e.payment.utils.FileUtils;

public class ProDeleteCertification {

	public static void main(String[] args) throws IOException {
		String bankName = "BOD";
		
		
		
		
		String path = System.getProperty("user.dir")
				+ "\\src\\main\\java\\com\\zbensoft\\e\\payment\\tools\\webservice\\certification\\pro\\cer\\";
		String cmd = "";

		String fileName = path + "\\encryption-client-keystore-" + bankName + ".jks";
		File file = new File(fileName);
		if (!file.exists()) {
			System.err.println("bank=" + bankName + ",file not exist,not delete");
			return;
		}

		FileUtils.delFile(path + "\\encryption-client-keystore-" + bankName + ".jks");
		FileUtils.delFile(path + "\\encryption-client-public-" + bankName + ".cer");
		FileUtils.delFile(path + "\\encryption-client-keystore-" + bankName + ".p12");
		
		// 删除服务器信任证书中的证书（慎重操作，可以删除特定银行在服务器端的信任证书）
		cmd = "keytool -delete -alias epayment-webservice-clientcert-" + bankName + " -keystore " + path
				+ "\\encryption-server-truststore.jks -keypass webservice911epay -storepass webservice911epay";
		runScript(cmd);

		runScript("cmd /c rd/s/q " + path + "\\" + bankName);
		
		//查看jks服务器信任证书内容(所有银行的都在这可以查看)
		cmd = "keytool -list -v -keystore "+path+"\\encryption-server-truststore.jks -keypass webservice911epay -storepass webservice911epay";
		runScript(cmd);

		FileUtils.delFile(System.getProperty("user.dir").replaceAll("e-payment-tools", "e-payment-webservice")+"\\src\\main\\resources\\encryption-server-truststore.jks");
		FileUtils.copyFile(path+"\\"+"encryption-server-truststore.jks",System.getProperty("user.dir").replaceAll("e-payment-tools", "e-payment-webservice")+"\\src\\main\\resources\\encryption-server-truststore.jks");
		
		System.out.println("DeleteCertification succ");

	}

	public static void runScript(String cmd) {
		System.out.println("执行命令：" + cmd);
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedReader inBr = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null)
				// 获得命令执行后在控制台的输出信息
				System.out.println(lineStr);// 打印输出信息
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
}
