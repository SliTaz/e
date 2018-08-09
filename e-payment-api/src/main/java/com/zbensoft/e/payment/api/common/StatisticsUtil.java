package com.zbensoft.e.payment.api.common;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.zbensoft.e.payment.api.alarm.AlarmMangerFactory;
import com.zbensoft.e.payment.api.alarm.alarm.CountRequestAlarm;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;

/**
 * 统计银行充值，银行取消<br>
 * 用户消费
 * 
 * @author xieqiang
 *
 */
public class StatisticsUtil {

	private static final Logger log = LoggerFactory.getLogger(StatisticsUtil.class);

	private static Environment env = SpringBeanUtil.getBean(Environment.class);
	private static RedisTemplate redisTemplate = SpringBeanUtil.getBean("redisTemplate", RedisTemplate.class);
	// private static long interval_ms = time_sec * 1000l;
	// private static long time_db_sec = Long.valueOf(env.getProperty("statistics.monitor.interval.db.seconds"));// 2小时
	// private static long interval_db_ms = time_db_sec * 1000l;

	private static long request_count_start_time = 0;

	/**
	 * 增加统计项，全部
	 * 
	 * @param key
	 */
	public static void addStatistics(String key, String bankId) {
		int REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC);
		int REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC_MS = REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC * 1000;
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC);
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS = REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC * 1000;

		// 10秒
		String keyWeb = key + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC_MS;
		if (StringUtils.isNotEmpty(bankId)) {
			keyWeb += RedisDef.DELIMITER.UNDERLINE + bankId;
		}
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(keyWeb);
		operations.increment(1L);
		operations.expire(REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC * 2l, TimeUnit.SECONDS);
		// 2小时
		String keyDB = RedisDef.STATISTICS_PRE.DB + key + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS;
		if (StringUtils.isNotEmpty(bankId)) {
			keyDB += RedisDef.DELIMITER.UNDERLINE + bankId;
		}
		BoundValueOperations<String, Object> operationsdb = redisTemplate.boundValueOps(keyDB);
		operationsdb.increment(1L);
		operationsdb.expire(REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC * 2l, TimeUnit.SECONDS);

		// 统计请求数
		int REQUEST_COUNT_REST_TIME_MS = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REQUEST_COUNT_REST_TIME_MS);
		String COUNT_QUESTKey = RedisDef.COUNT.COUNT_QUEST + RedisDef.DELIMITER.UNDERLINE + (Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS);
		BoundValueOperations<String, Object> operationsCOUNT_QUEST = redisTemplate.boundValueOps(COUNT_QUESTKey);
		Object obCOUNT_QUEST = operationsCOUNT_QUEST.get();

		if (obCOUNT_QUEST == null) {
			String COUNT_QUESTKey2 = RedisDef.COUNT.COUNT_QUEST + RedisDef.DELIMITER.UNDERLINE + ((Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS) - 1);
			BoundValueOperations<String, Object> operationsCOUNT_QUEST2 = redisTemplate.boundValueOps(COUNT_QUESTKey);
			obCOUNT_QUEST = operationsCOUNT_QUEST2.get();
			if (obCOUNT_QUEST != null) {
				int ALARM_REQUEST_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_REQUEST_COUNT);
				if (Integer.valueOf(obCOUNT_QUEST.toString()) > ALARM_REQUEST_COUNT) {
					Object param = AlarmMangerFactory.getInstance().getParam(CountRequestAlarm.class.getName());
					if (param == null) {
						param = new HashMap<String, String>();
					}
					((Map<String, String>) param).put(RedisDef.COUNT.COUNT_QUEST, obCOUNT_QUEST.toString());
					AlarmMangerFactory.getInstance().setParam(CountRequestAlarm.class.getName(), param);
				}
			}

			String COUNT_QUEST_SUCCKey = RedisDef.COUNT.COUNT_QUEST_SUCC + RedisDef.DELIMITER.UNDERLINE + ((Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS) - 1);
			BoundValueOperations<String, Object> operationsCOUNT_QUEST_SUCC = redisTemplate.boundValueOps(COUNT_QUEST_SUCCKey);
			Object obCOUNT_QUEST_SUCC = operationsCOUNT_QUEST_SUCC.get();
			if (obCOUNT_QUEST_SUCC != null) {
				int ALARM_REQUEST_COUNT_SUCC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_REQUEST_COUNT_SUCC);
				if (Integer.valueOf(obCOUNT_QUEST_SUCC.toString()) > ALARM_REQUEST_COUNT_SUCC) {
					Object param = AlarmMangerFactory.getInstance().getParam(CountRequestAlarm.class.getName());
					if (param == null) {
						param = new HashMap<String, String>();
					}
					((Map<String, String>) param).put(RedisDef.COUNT.COUNT_QUEST_SUCC, obCOUNT_QUEST_SUCC.toString());
					AlarmMangerFactory.getInstance().setParam(CountRequestAlarm.class.getName(), param);
				}
			}
		}

		operationsCOUNT_QUEST.increment(1L);
		operationsCOUNT_QUEST.expire(REQUEST_COUNT_REST_TIME_MS / 1000 * 2l, TimeUnit.SECONDS);
	}

	/**
	 * 增加统计项，成功
	 * 
	 * @param key
	 */
	public static void addStatisticsSucc(String key, String bankId) {
		int REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC);
		int REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC_MS = REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC * 1000;
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC);
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS = REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC * 1000;

		// 10秒
		String keyWeb = RedisDef.STATISTICS_PRE.SUCC + key + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC_MS;
		if (StringUtils.isNotEmpty(bankId)) {
			keyWeb += RedisDef.DELIMITER.UNDERLINE + bankId;
		}
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(keyWeb);
		operations.increment(1L);
		operations.expire(REDIS_STATISTICS_MONITOR_EXPIRE_TIME_SEC * 2l, TimeUnit.SECONDS);
		// 2小时
		String keyDB = RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + key + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS;
		if (StringUtils.isNotEmpty(bankId)) {
			keyDB += RedisDef.DELIMITER.UNDERLINE + bankId;
		}
		BoundValueOperations<String, Object> operationsdb = redisTemplate.boundValueOps(keyDB);
		operationsdb.increment(1L);
		operationsdb.expire(REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC * 2l, TimeUnit.SECONDS);

		// 统计请求数成功数

		int REQUEST_COUNT_REST_TIME_MS = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REQUEST_COUNT_REST_TIME_MS);
		String COUNT_QUEST_SUCCKey = RedisDef.COUNT.COUNT_QUEST_SUCC + RedisDef.DELIMITER.UNDERLINE + (Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS);
		BoundValueOperations<String, Object> operationsCOUNT_QUEST_SUCC = redisTemplate.boundValueOps(COUNT_QUEST_SUCCKey);
		operationsCOUNT_QUEST_SUCC.increment(1L);
		operationsCOUNT_QUEST_SUCC.expire(REQUEST_COUNT_REST_TIME_MS / 1000 * 2l, TimeUnit.SECONDS);
	}

	/**
	 * 实时统计，全部
	 *
	 * @param key
	 * @return
	 */
	public static Long getStatistics() {
		int REQUEST_COUNT_REST_TIME_MS = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REQUEST_COUNT_REST_TIME_MS);
		String COUNT_QUEST_SUCCKey = RedisDef.COUNT.COUNT_QUEST + RedisDef.DELIMITER.UNDERLINE + ((Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS) - 1);
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(COUNT_QUEST_SUCCKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			return 0l;
		} else {
			return Long.valueOf(balanceStr.toString());
		}
	}

	/**
	 * 实时统计，成功
	 *
	 * @param key
	 * @return
	 */
	public static Long getStatisticsSucc() {
		int REQUEST_COUNT_REST_TIME_MS = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REQUEST_COUNT_REST_TIME_MS);
		String COUNT_QUESTKey = RedisDef.COUNT.COUNT_QUEST_SUCC + RedisDef.DELIMITER.UNDERLINE + ((Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS) - 1);
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(COUNT_QUESTKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			return 0l;
		} else {
			return Long.valueOf(balanceStr.toString());
		}
	}

	private static void addStatisticsSuccLimitDay(String key, Date date, String vid, Double amount) {
		String PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE);
		if ("-1".equals(PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE)) {
			return;
		}
		String keyWeb = key + RedisDef.DELIMITER.UNDERLINE + vid + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(date, "MM-dd");
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(keyWeb);
		operations.increment(DoubleUtil.doubleToRedisLong(amount));
		operations.expire(3600 * 24 * 2l, TimeUnit.SECONDS);// 2天
	}

	public static boolean isLimitDay(String key, String vid, Double amount) {
		String PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE);
		if ("-1".equals(PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE)) {
			return false;
		}

		String keyWeb = key + RedisDef.DELIMITER.UNDERLINE + vid + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd");
		try {
			BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(keyWeb);
			Object rAmountObject = operations.get();
			Long rAmount = 0l;
			if (rAmountObject != null) {
				rAmount = Long.valueOf(rAmountObject.toString());
			}
			if ((rAmount + DoubleUtil.doubleToRedisLong(amount)) > DoubleUtil.doubleToRedisLong(Double.valueOf(PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE))) {
				return true;
			}
		} catch (Exception e) {
			log.warn(keyWeb, e);
			return true;
		}
		return false;
	}

	public static void addStatisticsSuccLimitMonth(String key, Date date, String vid, Double amount) {
		addStatisticsSuccLimitDay(key, date, vid, amount);
		String keyWeb = key + RedisDef.DELIMITER.UNDERLINE + vid + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(date, "MM");
		try {
			BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(keyWeb);
			operations.increment(DoubleUtil.doubleToRedisLong(amount));
			operations.expire(3600 * 24 * 31 * 2l, TimeUnit.SECONDS);// 2天
		} catch (Exception e) {
			log.error("addStatisticsSuccLimitMonth " + keyWeb, e);
		}
	}

	public static boolean isLimitMonth(String key, String vid, Double amount) {

		String PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE);
		if ("-1".equals(PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE)) {
			return false;
		}

		String keyWeb = key + RedisDef.DELIMITER.UNDERLINE + vid + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM");
		try {
			BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(keyWeb);
			Object rAmountObject = operations.get();
			Long rAmount = 0l;
			if (rAmountObject != null) {
				rAmount = Long.valueOf(rAmountObject.toString());
			}

			if ((rAmount + DoubleUtil.doubleToRedisLong(amount)) > DoubleUtil.doubleToRedisLong(Double.valueOf(PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE))) {
				return true;
			}
		} catch (Exception e) {
			log.warn(keyWeb, e);
			return true;
		}
		return false;
	}
	// /**
	// * 实时统计，全部
	// *
	// * @param key
	// * @return
	// */
	// public static Long getStatistics(String key) {
	// key = key + RedisDef.DELIMITER.UNDERLINE + ((System.currentTimeMillis() / interval_sec) - 1);
	// BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
	// Object balanceStr = operations.get();
	// if (balanceStr == null) {
	// return 0l;
	// } else {
	// return Long.valueOf(balanceStr.toString());
	// }
	// }
	//
	// /**
	// * 实时统计，成功
	// *
	// * @param key
	// * @return
	// */
	// public static Long getStatisticsSucc(String key) {
	// key = "SUCC" + key + RedisDef.DELIMITER.UNDERLINE + ((System.currentTimeMillis() / interval_sec) - 1);
	// BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
	// Object balanceStr = operations.get();
	// if (balanceStr == null) {
	// return 0l;
	// } else {
	// return Long.valueOf(balanceStr.toString());
	// }
	// }
	//
	// /**
	// * 每2小时统计一次，存入数据库，全部
	// *
	// * @param key
	// * @return
	// */
	// public static Long getStatisticsDB(String key) {
	// key = "DB_" + key + RedisDef.DELIMITER.UNDERLINE + ((System.currentTimeMillis() / interval_db_sec) - 1);
	// BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
	// Object balanceStr = operations.get();
	// if (balanceStr == null) {
	// return 0l;
	// } else {
	// return Long.valueOf(balanceStr.toString());
	// }
	// }
	//
	// /**
	// * 每2小时统计一次，存入数据库,成功
	// *
	// * @param key
	// * @return
	// */
	// public static Long getStatisticsDBSucc(String key) {
	// key = "SUCC" + "DB_" + key + RedisDef.DELIMITER.UNDERLINE + ((System.currentTimeMillis() / interval_db_sec) - 1);
	// BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
	// Object balanceStr = operations.get();
	// if (balanceStr == null) {
	// return 0l;
	// } else {
	// return Long.valueOf(balanceStr.toString());
	// }
	// }

}
