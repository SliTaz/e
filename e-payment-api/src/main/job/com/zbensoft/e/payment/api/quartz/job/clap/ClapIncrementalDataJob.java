package com.zbensoft.e.payment.api.quartz.job.clap;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.SftpException;
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
public class ClapIncrementalDataJob extends ClapIncrementalAbs implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(ClapIncrementalDataJob.class);

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
		TASK_LOG.INFO(String.format("%s ********************ftp Start*********************", key));
		isSucc=ftp();//FTP 下载文件
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s ftp is error", key));
			throw new JobExecutionException(String.format("%s ftp is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s ftp is Success", key));
		}
		TASK_LOG.INFO(String.format("%s ********************Seller Update Start*********************", key));
		isSucc = executeClapSellerUpdateJob();//更新Seller数据
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s executeClapSellerUpdateJob is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s executeClapSellerUpdateJob is Success", key));
		}
		TASK_LOG.INFO(String.format("%s ********************Seller Account Update Start*********************", key));
		isSucc =executeClapSellerAccountUpdateJob();//更新SellerAccount数据
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s executeClapSellerAccountUpdateJob is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s executeClapSellerAccountUpdateJob is Success", key));
		}
		TASK_LOG.INFO(String.format("%s ********************Buyer Update Start*********************", key));
		isSucc =executeClapBuyerUpdateJob();//更新Buyer数据
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s executeClapBuyerUpdateJob is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s executeClapBuyerUpdateJob is Success", key));
		}
		TASK_LOG.INFO(String.format("%s ********************Distribution Update Start*********************", key));
		isSucc =executeClapDistributionUpdateJob();//更新券数据
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s executeClapDistributionUpdateJob is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s executeClapDistributionUpdateJob is Success", key));
		}
		TASK_LOG.INFO(String.format("%s End", key));
	}
	
	
	private boolean ftp() {
		File downloadPath=new File(JOB_CLAP_DOWNLOAD_FILE_PATH);
		if(!downloadPath.exists()){
			downloadPath.mkdirs();
		}
		
			if (StringUtils.isNotEmpty(JOB_CLAP_SFTP_USER_NAME)) {
				SFTPUtil sftp = new SFTPUtil(JOB_CLAP_SFTP_USER_NAME, JOB_CLAP_SFTP_PASSWORD, JOB_CLAP_SFTP_IP_ADDRESS, JOB_CLAP_SFTP_PORT);
				boolean hasFile=false;
				try {
				sftp.login();
				} catch (Exception e) {
					TASK_LOG.ERROR(String.format("%s ftp Exception", key), e);
					return false;
				}
				try {
					sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, sellerFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + sellerFileName);
					hasFile=true;
				} catch (FileNotFoundException | SftpException e) {
					TASK_LOG.ERROR(String.format("%s ftp Exception, sellerFile download failed.", key), e);
				}
				try {
					sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, sellerAccountFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + sellerAccountFileName);
					hasFile=true;
				} catch (FileNotFoundException | SftpException e) {
					TASK_LOG.ERROR(String.format("%s ftp Exception sellerAccountFile download failed.", key), e);
				}
				try {
					sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, buyerFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + buyerFileName);
					hasFile=true;
				} catch (FileNotFoundException | SftpException e) {
					TASK_LOG.ERROR(String.format("%s ftp Exception buyerFileName download failed.", key), e);
				}
				try {
					sftp.download(JOB_CLAP_UPDATE_FILE_SFTP_PATH, disFileName, JOB_CLAP_DOWNLOAD_FILE_PATH + disFileName);
					hasFile=true;
				} catch (FileNotFoundException | SftpException e) {
					TASK_LOG.ERROR(String.format("%s ftp Exception disFileName download failed.", key), e);
				}
				sftp.logout();
				return hasFile;
			}
		
		return false;
	
	}
}
