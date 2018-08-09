package com.zbensoft.e.payment.api.quartz.job;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.factory.BankInfoFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.BankInterfaceStatisticsService;
import com.zbensoft.e.payment.api.service.api.InterfaceStatisticsService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.BankInterfaceStatistics;
import com.zbensoft.e.payment.db.domain.InterfaceStatistics;

/**
 * 监控入库， 每2小时执行一次
 * 
 * 0 10 0/2 * * ?
 * 
 * @author xieqiang
 *
 */
public class MonitorToDBJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(MonitorToDBJob.class);

	private static String key = "MonitorToDBJob";

	private BankInterfaceStatisticsService bankInterfaceStatisticsService = SpringBeanUtil.getBean(BankInterfaceStatisticsService.class);
	private InterfaceStatisticsService interfaceStatisticsService = SpringBeanUtil.getBean(InterfaceStatisticsService.class);
	int interval_db_ms = 0;
	// private Environment env = SpringBeanUtil.getBean(Environment.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC);// 2小时
		interval_db_ms = REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC * 1000;
		long inerval = System.currentTimeMillis() / interval_db_ms - 1;
		List<BankInfo> list = BankInfoFactory.getInstance().get();
		InterfaceStatistics interfaceStatisticsRecharge = writeBankStatistics(RedisDef.STATISTICS.BANK_RECHARGE, inerval, list);
		InterfaceStatistics interfaceStatisticsReverse = writeBankStatistics(RedisDef.STATISTICS.BANK_REVERSE, inerval, list);
		writeStatistics(RedisDef.STATISTICS.BANK_RECHARGE, inerval, interfaceStatisticsRecharge);
		writeStatistics(RedisDef.STATISTICS.BANK_REVERSE, inerval, interfaceStatisticsReverse);
		writeStatistics(RedisDef.STATISTICS.RECHARGE, inerval);
		writeStatistics(RedisDef.STATISTICS.CHARGE, inerval);
		writeStatistics(RedisDef.STATISTICS.CONSUMPTION, inerval);
		TASK_LOG.INFO(String.format("%s end", key));
	}

	private void writeStatistics(String bankRecharge, long inerval, InterfaceStatistics interfaceStatisticsRecharge) {
		if (interfaceStatisticsRecharge != null && (interfaceStatisticsRecharge.getRequestNum() > 0 || interfaceStatisticsRecharge.getRequestSuccNum() > 0)) {
			String statisticsTime = getStatisticsTime(inerval);
			int interfaceType = getType(bankRecharge);

			// db
			interfaceStatisticsRecharge.setStatisticsTime(statisticsTime);
			interfaceStatisticsRecharge.setInterfaceType(interfaceType);
			try {
				if (interfaceStatisticsRecharge.getRequestNum() > 0l || interfaceStatisticsRecharge.getRequestSuccNum() > 0l) {
					interfaceStatisticsService.insert(interfaceStatisticsRecharge);
					TASK_LOG.INFO(String.format("%s insert %s", key, JSONArray.toJSONString(interfaceStatisticsRecharge)));
				} else {
					TASK_LOG.INFO(String.format("%s not insert %s", key, JSONArray.toJSONString(interfaceStatisticsRecharge)));
				}
			} catch (Exception e) {
				log.error(String.format("%s fail,insert %s", key, JSONArray.toJSONString(interfaceStatisticsRecharge)), e);
				TASK_LOG.INFO(String.format("%s fail,insert %s", key, JSONArray.toJSONString(interfaceStatisticsRecharge)));
				TASK_LOG.ERROR(String.format("%s fail,insert %s", key, JSONArray.toJSONString(interfaceStatisticsRecharge)), e);
			}
		}
	}

	private InterfaceStatistics writeBankStatistics(String bankRecharge, long inerval, List<BankInfo> list) {
		long requestNum = 0l;
		long requestSuccNum = 0l;
		String statisticsTime = getStatisticsTime(inerval);
		int interfaceType = getType(bankRecharge);
		if (list != null && list.size() > 0) {
			for (BankInfo bankInfo : list) {
				String redisKey = RedisDef.STATISTICS_PRE.DB + bankRecharge + RedisDef.DELIMITER.UNDERLINE + inerval + RedisDef.DELIMITER.UNDERLINE + bankInfo.getBankId();
				try {
					Object balanceStr = RedisUtil.getByKey(redisKey);
					if (balanceStr == null) {
						balanceStr = "0";
					}

					Object balanceStrSucc = RedisUtil.getByKey(RedisDef.STATISTICS_PRE.SUCC + redisKey);
					if (balanceStrSucc == null) {
						balanceStrSucc = "0";
					}

					// db
					String bankId = redisKey.replaceAll(RedisDef.STATISTICS_PRE.DB + bankRecharge + RedisDef.DELIMITER.UNDERLINE + inerval + RedisDef.DELIMITER.UNDERLINE, "");
					BankInterfaceStatistics bankInterfaceStatistics = new BankInterfaceStatistics();
					bankInterfaceStatistics.setStatisticsTime(statisticsTime);
					bankInterfaceStatistics.setInterfaceType(interfaceType);
					bankInterfaceStatistics.setBankId(bankId);
					bankInterfaceStatistics.setRequestNum(Long.valueOf(balanceStr.toString()));
					bankInterfaceStatistics.setRequestSuccNum(Long.valueOf(balanceStrSucc.toString()));
					if (bankInterfaceStatistics.getRequestNum() > 0l || bankInterfaceStatistics.getRequestSuccNum() > 0l) {
						bankInterfaceStatisticsService.insert(bankInterfaceStatistics);
						TASK_LOG.INFO(String.format("%s insert %s", key, JSONArray.toJSONString(bankInterfaceStatistics)));
					} else {
						TASK_LOG.INFO(String.format("%s not insert %s", key, JSONArray.toJSONString(bankInterfaceStatistics)));
					}

					requestNum += Long.valueOf(balanceStr.toString());
					requestSuccNum += Long.valueOf(balanceStrSucc.toString());
				} catch (Exception e) {
					log.error(String.format("%s fail,key=%s", key, redisKey), e);
					TASK_LOG.INFO(String.format("%s fail,key=%s", key, redisKey));
					TASK_LOG.ERROR(String.format("%s fail,key=%s", key, redisKey), e);
				}
			}
		}
		InterfaceStatistics interfaceStatistics = new InterfaceStatistics();
		interfaceStatistics.setRequestNum(requestNum);
		interfaceStatistics.setRequestSuccNum(requestSuccNum);
		return interfaceStatistics;
	}

	private void writeStatistics(String bankRecharge, long inerval) {
		String statisticsTime = getStatisticsTime(inerval);
		int interfaceType = getType(bankRecharge);
		long requestNum = 0l;
		long requestSuccNum = 0l;
		String redisKey = RedisDef.STATISTICS_PRE.DB + bankRecharge + RedisDef.DELIMITER.UNDERLINE + inerval;
		try {
			Object balanceStr = RedisUtil.getByKey(redisKey);
			if (balanceStr == null) {
				balanceStr = "0";
			}

			Object balanceStrSucc = RedisUtil.getByKey(RedisDef.STATISTICS_PRE.SUCC + redisKey);
			if (balanceStrSucc == null) {
				balanceStrSucc = "0";
			}
			requestNum += Long.valueOf(balanceStr.toString());
			requestSuccNum += Long.valueOf(balanceStrSucc.toString());
		} catch (Exception e) {
			log.error(String.format("%s fail,key=%s", key, redisKey), e);
			TASK_LOG.INFO(String.format("%s fail,key=%s", key, redisKey));
			TASK_LOG.ERROR(String.format("%s fail,key=%s", key, redisKey), e);
		}

		// db
		InterfaceStatistics interfaceStatistics = new InterfaceStatistics();
		interfaceStatistics.setStatisticsTime(statisticsTime);
		interfaceStatistics.setInterfaceType(interfaceType);
		interfaceStatistics.setRequestNum(requestNum);
		interfaceStatistics.setRequestSuccNum(requestSuccNum);
		try {

			if (interfaceStatistics.getRequestNum() > 0l || interfaceStatistics.getRequestSuccNum() > 0l) {
				interfaceStatisticsService.insert(interfaceStatistics);
				TASK_LOG.INFO(String.format("%s insert %s", key, JSONArray.toJSONString(interfaceStatistics)));
			} else {
				TASK_LOG.INFO(String.format("%s not insert %s", key, JSONArray.toJSONString(interfaceStatistics)));
			}
		} catch (Exception e) {
			log.error(String.format("%s fail,insert %s", key, JSONArray.toJSONString(interfaceStatistics)), e);
			TASK_LOG.INFO(String.format("%s fail,insert %s", key, JSONArray.toJSONString(interfaceStatistics)));
			TASK_LOG.ERROR(String.format("%s fail,insert %s", key, JSONArray.toJSONString(interfaceStatistics)), e);
		}
	}

	private int getType(String bankRecharge) {
		if (bankRecharge.equals(RedisDef.STATISTICS.BANK_RECHARGE)) {
			return MessageDef.TRADE_TYPE.BANK_RECHARGE;
		}
		if (bankRecharge.equals(RedisDef.STATISTICS.BANK_REVERSE)) {
			return MessageDef.TRADE_TYPE.BANK_REVERSE;
		}
		if (bankRecharge.equals(RedisDef.STATISTICS.RECHARGE)) {
			return MessageDef.TRADE_TYPE.RECHARGE;
		}
		if (bankRecharge.equals(RedisDef.STATISTICS.CHARGE)) {
			return MessageDef.TRADE_TYPE.CHARGE;
		}
		if (bankRecharge.equals(RedisDef.STATISTICS.CONSUMPTION)) {
			return MessageDef.TRADE_TYPE.CONSUMPTION;
		}
		return 0;
	}

	private String getStatisticsTime(long inerval) {
		Date date = new Date(inerval * interval_db_ms);
		return DateUtil.convertDateToString(date, "yyyy-MM-dd HH");
	}

	public static void main(String[] args) {
		Calendar.getInstance().getTime();
		long inerval = 208569l;
		long interval_db_ms = 7200 * 1000l;
		Date date = new Date(inerval * interval_db_ms);
		System.out.println(DateUtil.convertDateToString(date, "yyyy-MM-dd HH"));
	}
}
