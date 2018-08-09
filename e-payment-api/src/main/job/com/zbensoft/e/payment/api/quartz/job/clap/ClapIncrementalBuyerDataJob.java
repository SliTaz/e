package com.zbensoft.e.payment.api.quartz.job.clap;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SFTPUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
/**
 * 用户增量更新，每天凌晨1点执行。
 * 
 * 0 0 1 * * ?
 * 
 * @author Wang Chenyang
 *
 */
public class ClapIncrementalBuyerDataJob extends ClapIncrementalAbs implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(ClapIncrementalBuyerDataJob.class);

	private static String key = "ClapIncrementalDataJob";
	
	String JOB_CLAP_SFTP_USER_NAME = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_USER_NAME);
	String JOB_CLAP_SFTP_IP_ADDRESS = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_IP_ADDRESS);
	int JOB_CLAP_SFTP_PORT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_PORT);
	String JOB_CLAP_SFTP_PASSWORD = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_PASSWORD);
	String JOB_CLAP_UPDATE_FILE_SFTP_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_UPDATE_FILE_SFTP_PATH);
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		boolean isSucc = true;
		isSucc =executeClapBuyerUpdateJob();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s executeClapBuyerUpdateJob is error", key));
			throw new JobExecutionException(String.format("%s executeClapBuyerUpdateJob is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s executeClapBuyerUpdateJob is Success", key));
		}
		TASK_LOG.INFO(String.format("%s End", key));
	}
	
	
	private boolean ftp() {
		try {
			if (StringUtils.isNotEmpty(JOB_CLAP_SFTP_USER_NAME)) {
				SFTPUtil sftp = new SFTPUtil(JOB_CLAP_SFTP_USER_NAME, JOB_CLAP_SFTP_PASSWORD, JOB_CLAP_SFTP_IP_ADDRESS, JOB_CLAP_SFTP_PORT);
				sftp.login();
				File downloadPath=new File(JOB_CLAP_DOWNLOAD_FILE_PATH);
				if(!downloadPath.exists()){
					downloadPath.mkdirs();
				}
				sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, sellerFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + sellerFileName);
				sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, buyerFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + buyerFileName);
				sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, disFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + disFileName);
				sftp.logout();
			}
		} catch (Exception e) {
			log.error(String.format("%s ftp Exception", key), e);
			TASK_LOG.ERROR(String.format("%s ftp Exception", key), e);
			return false;
		}
		return true;
	
	}
}
