package com.zbensoft.e.payment.api.quartz.job.charge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.io.FileUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.common.util.DateUtil;

/**
 * 银行对账每天晚上10点执行一次
 * 
 * 0 0 22 * * ?
 * 
 * @author wangchenyang
 *
 */
public class ReconciliationVenezuelaBankJob extends SimpleChargeReconciliationJobAbs {

	private static final Logger log = LoggerFactory.getLogger(ReconciliationVenezuelaBankJob.class);

	protected String key = "ReconciliationVenezuelaBankJob";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		boolean isSucc = true;
		isSucc = copyFiles();
		if (!isSucc) {
			String alarmMsg=String.format("%s Today Charge Reconciliatioin file not receive, Please Check!", key);
			MessageAlarmFactory.getInstance().add(alarmMsg);//增加告警
			RECONCILIATION_LOG.INFO(String.format("%s ftp/copyFile is fail", key));
			throw new JobExecutionException(String.format("%s ftp/copyFile is fail", key));
		}
		if (destFilePaths != null && destFilePaths.size() > 0) {
			for (String fileName : destFilePaths) {
				destFilePathName = fileName;
				isSucc = readFileHeader(destFilePathName);
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s readFileHeader is fail,File: %s", key, fileName));
					throw new JobExecutionException(String.format("%s readFileHeader is fail", key));
				} else {
					executeJob();
				}
			}
		}
		isSucc = deleteFiles();
		if (!isSucc) {
			String alarmMsg=String.format("%s Today Charge Reconciliatioin file delete Failed, Please Check!", key);
			MessageAlarmFactory.getInstance().add(alarmMsg);//增加告警
			RECONCILIATION_LOG.INFO(String.format("%s deleteFiles is fail", key));
			throw new JobExecutionException(String.format("%s deleteFiles is fail", key));
		}
		TASK_LOG.INFO(String.format("%s End", key));
	}

	@Override
	protected void initdata() {
		if (bankChargeHeaderTmp != null && bankChargeHeaderTmp != null) {
			yesterday = DateUtil.convertStringToDate(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(), DateUtil.DATE_FORMAT_THREE, true, DateUtil.DATE_FORMAT_ONE), DateUtil.DATE_FORMAT_ONE);
		}
	}

	protected boolean readFileHeader(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String tempString = reader.readLine();
			if (tempString.startsWith(JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY)) {// 文件头
				boolean isDecode = bankChargeHeaderTmp.decode(tempString, key);// 解析头
				if (!isDecode) {
					return false;
				}
				if (!bankChargeHeaderTmp.validate(key)) {
					return false;
				}
			}

		} catch (Exception e) {
			log.error("", e);
		}
		return true;

	}

	private boolean copyFiles() {
		try {
			destFilePaths.clear();

			File fileFolder = new File(filePath);
			if (!fileFolder.exists()) {
				fileFolder.mkdirs();
			}

			File dirFile = new File(bankFilePath);
			// 判断该文件或目录是否存在，不存在时在控制台输出提醒
			if (!dirFile.exists()) {
				RECONCILIATION_LOG.ERROR("the path:" + bankFilePath + " not exit.");
				return false;
			}
			// 判断如果不是一个目录，就判断是不是一个文件，时文件则输出文件路径
			if (!dirFile.isDirectory()) {
				if (dirFile.isFile()) {
					RECONCILIATION_LOG.ERROR("the path:" + bankFilePath + " is a file");
				}
				return false;
			}
			File[] fileList = dirFile.listFiles();

			if (fileList != null && fileList.length > 0) {
				for (File fileTmp : fileList) {
					if (fileTmp.exists()) {
						File destNewFile = new File(filePath + fileTmp.getName());
						if (destNewFile.exists()) {// 如果目标文件已经存在则删除
							destNewFile.delete();
						}
						FileUtils.copyFile(fileTmp, new File(filePath + fileTmp.getName()));
						destFilePaths.add(filePath + fileTmp.getName());
					}
				}
				return true;
			} else {
				RECONCILIATION_LOG.ERROR("the path:" + bankFilePath + " has no file");
				return false;
			}

		} catch (Exception e) {
			log.error("", e);
			return false;
		}
	}

	private boolean deleteFiles() {
		try {

			File dirFile = new File(bankFilePath);
			// 判断该文件或目录是否存在，不存在时在控制台输出提醒
			if (!dirFile.exists()) {
				RECONCILIATION_LOG.ERROR("the path:" + bankFilePath + " not exit.");
				return false;
			}
			// 判断如果不是一个目录，就判断是不是一个文件，时文件则输出文件路径
			if (!dirFile.isDirectory()) {
				if (dirFile.isFile()) {
					RECONCILIATION_LOG.ERROR("the path:" + bankFilePath + " is a file");
				}
				return false;
			}
			File[] fileList = dirFile.listFiles();
			if (fileList != null && fileList.length > 0) {
				for (File fileTmp : fileList) {
					fileTmp.delete();
				}
				return true;
			} else {
				RECONCILIATION_LOG.ERROR("the path:" + bankFilePath + " has no file");
				return false;
			}

		} catch (Exception e) {
			log.error("", e);
			return false;
		}
	}

}
