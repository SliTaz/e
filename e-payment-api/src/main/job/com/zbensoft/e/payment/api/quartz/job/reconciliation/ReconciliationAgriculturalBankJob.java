package com.zbensoft.e.payment.api.quartz.job.reconciliation;

import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;

/**
 * 银行对账每天凌晨1点执行一次
 * 
 * 0 0 1 * * ?
 * 
 * @author xieqiang
 *
 */
public class ReconciliationAgriculturalBankJob extends SimpleReconciliationJobAbs {
	Logger logger = LoggerFactory.getLogger(getClass());

	private static String key = ReconciliationAgriculturalBankJob.class.getName();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		executeJob();

	}

	protected void initdata() {
		super.key = ReconciliationAgriculturalBankJob.key;

		String JOB_RECONCLIATION_ALL_BANK_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_RECONCLIATION_ALL_BANK_FILE_PATH);
		String JOB_RECONCLIATION_ALL_BANK_BANK_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_RECONCLIATION_ALL_BANK_BANK_FILE_PATH);

		date = Calendar.getInstance().getTime();
		bankId = "0166";
		filePath = JOB_RECONCLIATION_ALL_BANK_FILE_PATH + "/agricultural/Conciliation_recharge/";
		bankFilePath = JOB_RECONCLIATION_ALL_BANK_BANK_FILE_PATH + "/agricultural/Conciliation_recharge/";
		timeWindow = 0;// 时间窗口
		// other
		yesterday = DateUtils.addDays(date, -1);
		yesterdayStr = DateUtil.convertDateToString(yesterday, "yyyyMMdd");
		fileName = bankId + "_" + yesterdayStr + ".txt";

		redisKey_bank = "recon_bank_" + bankId;
		redisKey_epay = "recon_epay_" + bankId;

		// // sftp
		// sftpPath = "/upload";
		// sftpUserName = "venezuela";
		// sftpIpAddress = "201.249.156.11";
		// sftpPort = 2222;
		// sftpPassword = null;
		// sftpKeyFile = this.getClass().getResource("/sftp/venezuela/id_rsa").getFile();
	}

}
