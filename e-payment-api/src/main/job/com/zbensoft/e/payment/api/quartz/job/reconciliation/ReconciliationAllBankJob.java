package com.zbensoft.e.payment.api.quartz.job.reconciliation;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;

/**
 * 银行对账每天凌晨1点执行一次-此任务为测试某个银行使用
 * 
 * 0 0 1 * * ?
 * 
 * @author xieqiang
 *
 */
public class ReconciliationAllBankJob extends SimpleReconciliationJobAbs {
	Logger logger = LoggerFactory.getLogger(getClass());

	private static String key = "ReconciliationAllBankJob";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		executeJob();
	}

	protected void initdata() {
		super.key = ReconciliationAllBankJob.key;
		String JOB_RECONCLIATION_ALL_BANK_DATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_RECONCLIATION_ALL_BANK_DATE);
		String JOB_RECONCLIATION_ALL_BANK_BANK_ID = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_RECONCLIATION_ALL_BANK_BANK_ID);
		String JOB_RECONCLIATION_ALL_BANK_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_RECONCLIATION_ALL_BANK_FILE_PATH);
		String JOB_RECONCLIATION_ALL_BANK_BANK_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_RECONCLIATION_ALL_BANK_BANK_FILE_PATH);

		date = DateUtil.convertStringToDate(JOB_RECONCLIATION_ALL_BANK_DATE, "yyyy-MM-dd");
		bankId = JOB_RECONCLIATION_ALL_BANK_BANK_ID;
		filePath = JOB_RECONCLIATION_ALL_BANK_FILE_PATH + "/bank/Conciliation_recharge/";
		bankFilePath = JOB_RECONCLIATION_ALL_BANK_BANK_FILE_PATH + "/bank/Conciliation_recharge/";
		timeWindow = 0;// 时间窗口

		// other
		yesterday = DateUtils.addDays(date, -1);
		yesterdayStr = DateUtil.convertDateToString(yesterday, "yyyyMMdd");
		fileName = bankId + "_" + yesterdayStr + ".txt";

		redisKey_bank = "RECON_BANK_" + bankId;
		redisKey_epay = "RECON_EPAY_" + bankId;
		// // sftp
		// sftpUserName = null;
		// sftpIpAddress = null;
		// sftpPort = 22;
		// sftpPassword = null;
		// sftpPath = null;
		// sftpKeyFile = null;
	}
}
