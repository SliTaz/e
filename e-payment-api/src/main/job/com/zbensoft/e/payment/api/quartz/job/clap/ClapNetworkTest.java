package com.zbensoft.e.payment.api.quartz.job.clap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zbensoft.e.payment.api.common.SFTPUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.NETWORK_MONITOR_LOG;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
/**
 * 用户增量更新，每天凌晨1点执行。
 * 
 * 0 0 1 * * ?
 * 
 * @author Wang Chenyang
 *
 */
public class ClapNetworkTest extends ClapIncrementalAbs implements Job {
	private static String key = "ClapCenterSFTPTest";
	
	String JOB_CLAP_SFTP_USER_NAME = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_USER_NAME);
	String JOB_CLAP_SFTP_IP_ADDRESS = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_IP_ADDRESS);
	int JOB_CLAP_SFTP_PORT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_PORT);
	String JOB_CLAP_SFTP_PASSWORD = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_PASSWORD);
	String JOB_CLAP_UPDATE_FILE_SFTP_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_UPDATE_FILE_SFTP_PATH);
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		NETWORK_MONITOR_LOG.INFO(String.format("%s Start", key));
		boolean isSucc = true;
		isSucc=isPingAble();
		if (!isSucc) {
			NETWORK_MONITOR_LOG.INFO(String.format("%s Ping Test is error", key));
			throw new JobExecutionException(String.format("%s Ping 8.8.8.8 is fail", key));
		} else {
			NETWORK_MONITOR_LOG.INFO(String.format("%s Ping Test is Success", key));
		}
		isSucc =ftp();
		if (!isSucc) {
			NETWORK_MONITOR_LOG.INFO(String.format("%s ftp Test is error", key));
			throw new JobExecutionException(String.format("%s ftp is fail", key));
		} else {
			NETWORK_MONITOR_LOG.INFO(String.format("%s ftp Test is Success", key));
		}
		NETWORK_MONITOR_LOG.INFO(String.format("%s End", key));
	}
	
	
	private boolean isPingAble(){
		 try {
			InetAddress address2 = InetAddress.getByName("8.8.8.8");
			boolean result=address2.isReachable(5000);
			NETWORK_MONITOR_LOG.INFO("Ping 8.8.8.8------------>Result:"+result); 
			return result;
		 } catch (Exception e) {
			NETWORK_MONITOR_LOG.ERROR("Ping 8.8.8.8------------>Exception",e); 
		}
		return false;
		
	}
	
	
	private boolean ftp() {
		try {
			if (StringUtils.isNotEmpty(JOB_CLAP_SFTP_USER_NAME)) {
				SFTPUtil sftp = new SFTPUtil(JOB_CLAP_SFTP_USER_NAME, JOB_CLAP_SFTP_PASSWORD, JOB_CLAP_SFTP_IP_ADDRESS, JOB_CLAP_SFTP_PORT);
				sftp.login();
				sftp.logout();
			}
		} catch (Exception e) {
			NETWORK_MONITOR_LOG.ERROR(String.format("%s ftp Exception", key), e);
			return false;
		}
		return true;
	
	}
	
	private void runScript(String cmd) {
		NETWORK_MONITOR_LOG.INFO("Excute commond: " + cmd);
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedReader inBr = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
			String lineStr;
			while ((lineStr = inBr.readLine()) != null)
				// 获得命令执行后在控制台的输出信息
				NETWORK_MONITOR_LOG.INFO(lineStr);// 打印输出信息
			// 检查命令是否执行失败。
			if (p.waitFor() != 0) {
				if (p.exitValue() == 1)// p.exitValue()==0表示正常结束，1：非正常结束
					NETWORK_MONITOR_LOG.ERROR("Excute CMD failed!");
			}
			inBr.close();
		} catch (Exception e) {
			NETWORK_MONITOR_LOG.ERROR("run Script exception",e);
		}
	}
}
