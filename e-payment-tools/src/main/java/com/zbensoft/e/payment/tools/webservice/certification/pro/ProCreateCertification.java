package com.zbensoft.e.payment.tools.webservice.certification.pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.zbensoft.e.payment.utils.FileUtils;

public class ProCreateCertification {

	public static void main(String[] args) throws IOException {
		String bankName = "BOD";
		
		
		
		
		String path = System.getProperty("user.dir")
				+ "\\src\\main\\java\\com\\zbensoft\\e\\payment\\tools\\webservice\\certification\\pro\\cer\\";
		String cmd = "";
		
		String fileName = path+"\\encryption-client-keystore-"+bankName+".jks";
		File file = new File(fileName);
		if(file.exists()){
			System.err.println("bank=" + bankName +",file exist,not create");
			return;
		}
		//生成客户端key和证书
		cmd = "keytool -genkeypair -alias epayment-webservice-clientkey-"+bankName+" -dname \"CN=epay,OU=cantv,O=cantv,L=caracas,ST=caracas,C=venezuela\" -keyalg RSA -keypass webservice"+bankName+"119 -keystore "+path+"\\encryption-client-keystore-"+bankName+".jks -storepass webservice"+bankName+"119 -validity 36000 -keysize 2048";
		runScript(cmd);
		//导出客户端证书
		cmd = "keytool -exportcert -alias epayment-webservice-clientkey-"+bankName+" -file "+path+"\\encryption-client-public-"+bankName+".cer -keystore "+path+"\\encryption-client-keystore-"+bankName+".jks -storepass webservice"+bankName+"119";
		runScript(cmd);
		//将客户端证书导入服务器信任
		cmd = "keytool -importcert -keystore "+path+"\\encryption-server-truststore.jks -alias epayment-webservice-clientcert-"+bankName+" -file "+path+"\\encryption-client-public-"+bankName+".cer -storepass webservice911epay -noprompt";
		runScript(cmd);
		//私钥，转换为p12，供c#客户端调用
		cmd = "keytool -importkeystore -srckeystore "+path+"\\encryption-client-keystore-"+bankName+".jks -srcstoretype JKS -srcstorepass webservice"+bankName+"119 -destkeystore "+path+"\\encryption-client-keystore-"+bankName+".p12 -deststoretype PKCS12 -deststorepass webservice"+bankName+"119";
		runScript(cmd);
		//查看jks服务器信任证书内容(所有银行的都在这可以查看)
		cmd = "keytool -list -v -keystore "+path+"\\encryption-server-truststore.jks -keypass webservice911epay -storepass webservice911epay";
		runScript(cmd);
		File filef = new File(path+"\\" + bankName); 
		if(filef.exists()){
			System.err.println("bank=" + bankName +",file fold exist,not create");
			return;
		}
		filef.mkdirs();
		FileUtils.copyFile(path+"\\"+"encryption-client-keystore-" + bankName+".jks", path+"\\" + bankName+"\\"+"encryption-client-keystore-" + bankName+".jks");
		FileUtils.copyFile(path+"\\"+"encryption-client-keystore-" + bankName+".p12", path+"\\" + bankName+"\\"+"encryption-client-keystore-" + bankName+".p12");
		FileUtils.copyFile(path+"\\"+"encryption-client-truststore.jks", path+"\\" + bankName+"\\"+"encryption-client-truststore.jks");
		FileUtils.copyFile(path+"\\"+"encryption-client-truststore.p12", path+"\\" + bankName+"\\"+"encryption-client-truststore.p12");

		File filereadme = new File(path+"\\" + bankName+"\\readme.txt"); 
		filereadme.createNewFile();
		FileOutputStream fos = new FileOutputStream(filereadme);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("client name:epayment-webservice-clientkey-"+bankName+"");
		bw.newLine();
		bw.write("client password:webservice"+bankName+"119");
		bw.newLine();
		bw.write("server name:epayment-webservice-servercert");
		bw.newLine();
		bw.write("server password:webservice911epay");
		bw.newLine();
		bw.close();
		

		FileUtils.delFile(System.getProperty("user.dir").replaceAll("e-payment-tools", "e-payment-webservice")+"\\src\\main\\resources\\encryption-server-truststore.jks");
		FileUtils.copyFile(path+"\\"+"encryption-server-truststore.jks",System.getProperty("user.dir").replaceAll("e-payment-tools", "e-payment-webservice")+"\\src\\main\\resources\\encryption-server-truststore.jks");
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
