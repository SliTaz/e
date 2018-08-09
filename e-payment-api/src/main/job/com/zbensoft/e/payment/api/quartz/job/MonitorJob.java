package com.zbensoft.e.payment.api.quartz.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.factory.BankInfoFactory;
import com.zbensoft.e.payment.api.log.MONITOR_LOG;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.BankInfo;

/**
 * 监控日志， 每10s执行一次
 * 
 * 5/10 * * * * ?
 * 
 * @author xieqiang
 *
 */
public class MonitorJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(MonitorJob.class);

	private static String key = "MonitorJob";

//	private Environment env = SpringBeanUtil.getBean(Environment.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		int REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC);
		int REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC_MS = REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC * 1000;

		long inerval = ((System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC_MS) - 1);
		List<BankInfo> list = BankInfoFactory.getInstance().get();
		writeBankStatistics(RedisDef.STATISTICS.BANK_RECHARGE, inerval, list);
		writeBankStatistics(RedisDef.STATISTICS.BANK_REVERSE, inerval, list);
		writeStatistics(RedisDef.STATISTICS.RECHARGE, inerval);
		writeStatistics(RedisDef.STATISTICS.CONSUMPTION, inerval);
		writelog();
	}

	private void writeBankStatistics(String bankRecharge, long inerval, List<BankInfo> list) {
		if (list != null && list.size() > 0) {
			for (BankInfo bankInfo : list) {
				String keyWeb = bankRecharge + RedisDef.DELIMITER.UNDERLINE + inerval + RedisDef.DELIMITER.UNDERLINE + bankInfo.getBankId();
				writelog(keyWeb);
				writelog(RedisDef.STATISTICS_PRE.SUCC + keyWeb);
			}
		}
	}

	private void writeStatistics(String bankRecharge, long inerval) {
		String keyWeb = bankRecharge + RedisDef.DELIMITER.UNDERLINE + inerval;
		writelog(keyWeb);
		writelog(RedisDef.STATISTICS_PRE.SUCC + keyWeb);
	}

	private void writelog() {
		Long all = StatisticsUtil.getStatistics();
		if (all > 0) {
			MONITOR_LOG.INFO(String.format("%s=%s", RedisDef.COUNT.COUNT_QUEST, all));
		}
		Long succ = StatisticsUtil.getStatisticsSucc();
		if (succ > 0) {
			MONITOR_LOG.INFO(String.format("%s=%s", RedisDef.COUNT.COUNT_QUEST_SUCC, succ));
		}
	}

	private void writelog(String keyWeb) {
		String redisKey = keyWeb;
		try {
			Object balanceStr = RedisUtil.getByKey(redisKey);
			if (balanceStr != null) {
				Long longValue = Long.valueOf(balanceStr.toString());
				if (balanceStr != null && longValue > 0) {
					MONITOR_LOG.INFO(String.format("%s=%s", redisKey, longValue));
				}
			}
		} catch (Exception e) {
			log.error(String.format("%s fail,key=%s", key, redisKey), e);
			MONITOR_LOG.INFO(String.format("%s fail,key=%s", key, redisKey));
			MONITOR_LOG.ERROR(String.format("%s fail,key=%s", key, redisKey), e);
		}
	}
}
