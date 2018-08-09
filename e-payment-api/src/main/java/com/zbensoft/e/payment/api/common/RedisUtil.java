package com.zbensoft.e.payment.api.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.e.payment.core.pay.bankProcess.BankProcessErrorCode;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.common.RedisDef.TRADE_SUMBIT;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.config.spring.securityjwt.ZBGrantedAuthority;
import com.zbensoft.e.payment.api.log.ACCOUNT_LOG;
import com.zbensoft.e.payment.api.log.BI_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.vo.redis.RedisVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.BankTradeInfo;
import com.zbensoft.e.payment.db.domain.Bookkeepking;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserQRCode;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class RedisUtil {
	private static RedisTemplate redisTemplate = SpringBeanUtil.getBean("redisTemplate", RedisTemplate.class);

	public static void increment_BI_PROFIT_STATEMENT(Bookkeepking bookkeepking) {
		Double value = DoubleUtil.sub(bookkeepking.getLoanAmount(), bookkeepking.getBorrowAmount());
		if (value > 0) {
			String redisKey = RedisDef.BI.PROFIT_STATEMENT + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(bookkeepking.getCreateTime(), "MMdd");
			BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
			operations.increment(DoubleUtil.doubleToRedisLong(value));
			operations.expire(3600 * 24 * 2l, TimeUnit.SECONDS);
			BI_LOG.INFO(String.format("PROFIT_STATEMENT value=%s,%s", value, JSONObject.toJSON(bookkeepking)));
		}
	}

	public static Double get_BI_PROFIT_STATEMENT(Date yesterday) {
		String redisKey = RedisDef.BI.PROFIT_STATEMENT + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(yesterday, "MMdd");
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object objectValue = operations.get();
		Double value = 0d;
		if (objectValue != null) {
			value = DoubleUtil.redisLongToDouble(objectValue);
		}
		return value;
	}

	public static Double increment_ACCOUNT_AMOUNT(ConsumerUserService consumerUserService, String userId) {
		Double balance = 0d;
		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + userId;
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(userId);
			Long end = operations.increment(DoubleUtil.doubleToRedisLong(consumerUser.getBalance()));
			balance = consumerUser.getBalance();
			ACCOUNT_LOG.INFO(consumerUser.getUserId(), DoubleUtil.doubleToRedisLong(consumerUser.getBalance()), 0l, end, "first to redis for queryConsumerBalance");
		} else {
			balance = DoubleUtil.redisLongToDouble(balanceStr);
		}
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

		return balance;
	}

	public static Double increment_ACCOUNT_AMOUNT(ConsumerUserService consumerUserService, String userId, Double balance) {
		Double retbalance = 0d;
		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + userId;
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			Long end = operations.increment(DoubleUtil.doubleToRedisLong(balance));
			ACCOUNT_LOG.INFO(userId, DoubleUtil.doubleToRedisLong(balance), 0l, end, "first to redis for selectByPrimaryKey");
			retbalance = balance;
		} else {
			retbalance = DoubleUtil.redisLongToDouble(balanceStr);
		}
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);
		return retbalance;
	}

	public static Double increment_ACCOUNT_AMOUNT(MerchantUserService merchantUserService, String userId) {
		Double retbalance = 0d;
		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + userId;
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(userId);
			Long end = operations.increment(DoubleUtil.doubleToRedisLong(merchantUser.getBalance()));
			retbalance = merchantUser.getBalance();
			ACCOUNT_LOG.INFO(merchantUser.getUserId(), DoubleUtil.doubleToRedisLong(merchantUser.getBalance()), 0l, end, "first to redis for queryMerchantBalance");
		} else {
			retbalance = DoubleUtil.redisLongToDouble(balanceStr);
		}
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

		return retbalance;
	}

	public static Double increment_ACCOUNT_AMOUNT(MerchantUserService merchantUserService, String userId, Double balance) {
		Double retbalance = 0d;
		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + userId;
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			Long end = operations.increment(DoubleUtil.doubleToRedisLong(balance));
			ACCOUNT_LOG.INFO(userId, DoubleUtil.doubleToRedisLong(balance), 0l, end, "first to redis for selectByPrimaryKey");
			retbalance = balance;
		} else {
			retbalance = DoubleUtil.redisLongToDouble(balanceStr);
		}
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

		return retbalance;
	}

	public static Double get_ACCOUNT_AMOUNT(String userId) {
		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + userId;
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			return null;
		} else {
			return DoubleUtil.redisLongToDouble(balanceStr);
		}
	}

	@SuppressWarnings("unchecked")
	public static Cursor<String> scan(String pattern, int limit) {
		ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();
		RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
		return (Cursor) redisTemplate.executeWithStickyConnection(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
				return new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize);
			}
		});
	}

	public static List<String> scan(String pattern, long limit) {
		ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();
		RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
		Cursor<String> retCursor = (Cursor) redisTemplate.executeWithStickyConnection(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
				return new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize);
			}
		});
		List<String> list = new ArrayList<>();
		if (retCursor != null) {
			while (retCursor.hasNext()) {
				list.add(retCursor.next());
			}
		}
		return list;
	}

	public static List<String> keys_ACCOUNT_AMOUNT() {
		return scan(RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + "*", 1000l);
	}

	public static String userId_ACCOUNT_AMOUNT(String redisKey) {
		return redisKey.replaceAll(RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE, "");
	}

	public static BankProcessErrorCode increment_pay_ACCOUNT_AMOUNT(ConsumerUserService consumerUserService, String payUserId, double payAmount, TradeInfo tradeInfo) {
		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + payUserId;

		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			ConsumerUser consumerUserTmp = consumerUserService.selectByPrimaryKey(payUserId);
			if (consumerUserTmp != null) {
				balanceStr = operations.increment(DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()));
				ACCOUNT_LOG.INFO(payUserId, DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()), 0l, Long.valueOf(balanceStr.toString()), "first to redis for processPayUserAccount_ACCOUNT_NUMBER");
			}
		}
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

		if (Long.valueOf(balanceStr.toString()) >= DoubleUtil.doubleToRedisLong(payAmount)) {
			long balanceEndStr = operations.increment(DoubleUtil.doubleToRedisLong(DoubleUtil.mul(payAmount, -1d)));
			ACCOUNT_LOG.INFO(payUserId, DoubleUtil.doubleToRedisLong(DoubleUtil.mul(payAmount, -1d)), Long.valueOf(balanceStr.toString()), balanceEndStr, tradeInfo);
			tradeInfo.setPayStartMoney(DoubleUtil.redisLongToDouble(balanceStr));
			tradeInfo.setPayEndMoney(DoubleUtil.redisLongToDouble(balanceEndStr));
			return BankProcessErrorCode.SUCC;
		} else {
			return BankProcessErrorCode.ACCOUNT_AMOUNT_NOT_ENOUGH;
		}
	}

	public static BankProcessErrorCode increment_recv_ACCOUNT_AMOUNT(ConsumerUserService consumerUserService, String recvUserId, double recvAmount, TradeInfo tradeInfo) {

		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + recvUserId;

		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			ConsumerUser consumerUserTmp = consumerUserService.selectByPrimaryKey(recvUserId);
			if (consumerUserTmp != null) {
				balanceStr = operations.increment(DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()));
				ACCOUNT_LOG.INFO(recvUserId, DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()), 0l, Long.valueOf(balanceStr.toString()), "first to redis for processRecvUserAccount_ACCOUNT_NUMBER");

			}
		}

		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

		Long balanceEndStr = operations.increment(DoubleUtil.doubleToRedisLong(recvAmount));
		ACCOUNT_LOG.INFO(recvUserId, DoubleUtil.doubleToRedisLong(recvAmount), Long.valueOf(balanceStr.toString()), balanceEndStr, tradeInfo);

		tradeInfo.setPayStartMoney(DoubleUtil.redisLongToDouble(balanceStr));
		tradeInfo.setPayEndMoney(DoubleUtil.redisLongToDouble(balanceEndStr));
		return BankProcessErrorCode.SUCC;

	}

	public static void increment_rollback_pay_ACCOUNT_AMOUNT(ConsumerUserService consumerUserService, String payUserId, double payAmount, TradeInfo tradeInfo) {

		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + payUserId;

		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			ConsumerUser consumerUserTmp = consumerUserService.selectByPrimaryKey(payUserId);
			if (consumerUserTmp != null) {
				balanceStr = operations.increment(DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()));
				ACCOUNT_LOG.INFO(payUserId, DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()), 0l, Long.valueOf(balanceStr.toString()), "first to redis for rollbackPayUserAccount_ACCOUNT_NUMBER");
			}
		}
		Long balanceEndStr = operations.increment(DoubleUtil.doubleToRedisLong(payAmount));
		ACCOUNT_LOG.INFO(payUserId, DoubleUtil.doubleToRedisLong(payAmount), Long.valueOf(balanceStr.toString()), balanceEndStr, tradeInfo);
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

	}

	public static void increment_rollback_recv_ACCOUNT_AMOUNT(ConsumerUserService consumerUserService, String recvUserId, double recvAmount, TradeInfo tradeInfo) {

		String redisKey = RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + recvUserId;

		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKey);
		Object balanceStr = operations.get();
		if (balanceStr == null) {
			ConsumerUser consumerUserTmp = consumerUserService.selectByPrimaryKey(recvUserId);
			if (consumerUserTmp != null) {
				balanceStr = operations.increment(DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()));
				ACCOUNT_LOG.INFO(recvUserId, DoubleUtil.doubleToRedisLong(consumerUserTmp.getBalance()), 0l, Long.valueOf(balanceStr.toString()), "first to redis for rollbackPayUserAccount_ACCOUNT_NUMBER");
			}
		}

		Long balanceEndStr = operations.increment(DoubleUtil.doubleToRedisLong(DoubleUtil.mul(recvAmount, -1d)));
		ACCOUNT_LOG.INFO(recvUserId, DoubleUtil.doubleToRedisLong(DoubleUtil.mul(recvAmount, -1d)), Long.valueOf(balanceStr.toString()), balanceEndStr, tradeInfo);
		// int REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC);
		//
		// operations.expire(REDIS_ACCOUNT_AMOUNT_EXPIRE_TIME_SEC, TimeUnit.SECONDS);
	}

	public static void create_TOKEN(String userName, JwtAuthenticationResponse token) {

		String redisKey = RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + userName;
		StringBuffer sb = new StringBuffer();
		sb.append(token.getToken()).append("|");
		sb.append(token.getUser().getId()).append("|");
		for (ZBGrantedAuthority zbGrantedAuthority : token.getAuthorities()) {
			sb.append(zbGrantedAuthority.getAuthority()).append(",");
		}
		String redisValue = sb.toString();
		token.getUser().setPassword(null);

		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(redisKey, redisValue);
		int REDIS_TOKEN_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_TOKEN_EXPIRE_TIME_SEC);
		redisTemplate.expire(redisKey, REDIS_TOKEN_EXPIRE_TIME_SEC, TimeUnit.SECONDS);
		token.setUser(null);
	}

	public static String key_TOKEN(String username) {
		return RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + username;
	}

	public static String get_TOKEN(String redisKey) {
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		String token = (String) operations.get(redisKey);
		if (CommonFun.isUpdateTokenTime(redisKey)) {
			int REDIS_TOKEN_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_TOKEN_EXPIRE_TIME_SEC);
			redisTemplate.expire(redisKey, REDIS_TOKEN_EXPIRE_TIME_SEC, TimeUnit.SECONDS);
		}
		return token;
	}

	public static String get_TOKEN_NO_UPDATE_TIME(String redisKey) {
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		String token = (String) operations.get(redisKey);
		return token;
	}

	public static void delete_TOKEN(String username) {
		String redisKey = RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + username;
		if (redisTemplate.hasKey(redisKey)) {
			redisTemplate.delete(redisKey);
		}
	}

	public static boolean islimit_ERROR_PASSWORD_COUNT(String userName, Date time) {
		userName = getFormatUserName(userName);
		String redisKeyErrorPasswordCount = RedisDef.LOGIN.ERROR_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + userName;
		int LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT);
		// if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
		// BoundValueOperations<String, Object> operationsErrorPasswordCount = redisTemplate.boundValueOps(redisKeyErrorPasswordCount);
		// Object errorPasswordCountObject = operationsErrorPasswordCount.get();
		// if (errorPasswordCountObject != null) {
		// return Integer.valueOf(errorPasswordCountObject.toString());
		// }
		// }
		if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			String redisValue = (String) operations.get(redisKeyErrorPasswordCount);
			if (!redisValue.contains("|")) {
				return false;
			}
			String[] values = redisValue.split("\\|");
			int now = Integer.valueOf(values[0]);
			long errTime = Long.valueOf(values[1]);
			if (now == LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT) {
				if (System.currentTimeMillis() < (errTime + 1000 * 60 * 5)) {// 5分钟
					return true;
				}
			} else if (now == LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 2) {
				if (System.currentTimeMillis() < (errTime + 1000 * 60 * 30)) {// 30分钟
					return true;
				}
			} else if (now >= LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 3) {
				if (System.currentTimeMillis() < (errTime + 1000 * 60 * 60 * 24)) {// 24小时
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 次数|时间
	 * 
	 * @param userName
	 * @param time
	 */
	public static int increment_ERROR_PASSWORD_COUNT(String userName, Date time) {
		userName = getFormatUserName(userName);
		String redisKeyErrorPasswordCount = RedisDef.LOGIN.ERROR_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + userName;
		int LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT);
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		String redisValue = "0|" + System.currentTimeMillis();
		if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
			redisValue = (String) operations.get(redisKeyErrorPasswordCount);
			if (!redisValue.contains("|")) {
				redisValue = "0|" + System.currentTimeMillis();
			}
		}
		String[] values = redisValue.split("\\|");
		int now = Integer.valueOf(values[0]) + 1;
		int returnNum = 0;
		if (now <= LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT) {
			returnNum = LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT - now;
		} else if (now <= (LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 2)) {
			returnNum = LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 2 - now;
		} else if (now <= (LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 3)) {
			returnNum = LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 3 - now;
		}
		redisValue = now + "|" + System.currentTimeMillis();
		operations.set(redisKeyErrorPasswordCount, redisValue);
		redisTemplate.expire(redisKeyErrorPasswordCount, 3600 * 24l, TimeUnit.SECONDS);
		return returnNum;
		// BoundValueOperations<String, Object> operationsErrorPasswordCount = redisTemplate.boundValueOps(redisKeyErrorPasswordCount);
		// operationsErrorPasswordCount.increment(1);
		// operationsErrorPasswordCount.expire(3600 * 24l, TimeUnit.SECONDS);// 1天

	}

	private static String getFormatUserName(String userName) {
		if (userName.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE)) {// buyer web
		} else if (userName.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN + RedisDef.DELIMITER.UNDERLINE)) {// buy app
			userName = MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + userName.substring(3);
		} else if (userName.startsWith(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE)) {// seller web
		} else if (userName.startsWith(MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN + RedisDef.DELIMITER.UNDERLINE)) {// seller app
			userName = MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + userName.substring(3);
		}
		return userName;
	}

	public static void delete_ERROR_PASSWORD_COUNT(String userName, Date time) {
		userName = getFormatUserName(userName);
		String redisKeyErrorPasswordCount = RedisDef.LOGIN.ERROR_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + userName;
		if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
			redisTemplate.delete(redisKeyErrorPasswordCount);
		}

	}

	public static boolean islimit_ERROR_PAY_PASSWORD_COUNT(String id, Date time) {
		int LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT);
		String redisKeyErrorPasswordCount = RedisDef.LOGIN.ERROR_PAY_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + id;
		// BoundValueOperations<String, Object> operationsErrorPasswordCount = redisTemplate.boundValueOps(redisKeyErrorPasswordCount);
		// Object errorPasswordCountObject = operationsErrorPasswordCount.get();
		// if (errorPasswordCountObject != null) {
		// return Integer.valueOf(errorPasswordCountObject.toString());
		// }
		// return 0;
		if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			String redisValue = (String) operations.get(redisKeyErrorPasswordCount);
			if (!redisValue.contains("|")) {
				return false;
			}
			String[] values = redisValue.split("\\|");
			int now = Integer.valueOf(values[0]);
			long errTime = Long.valueOf(values[1]);
			if (now == LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT) {
				if (System.currentTimeMillis() < (errTime + 1000 * 60 * 5)) {// 5分钟
					return true;
				}
			} else if (now == LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 2) {
				if (System.currentTimeMillis() < (errTime + 1000 * 60 * 30)) {// 30分钟
					return true;
				}
			} else if (now >= LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 3) {
				if (System.currentTimeMillis() < (errTime + 1000 * 60 * 60 * 24)) {// 24小时
					return true;
				}
			}
		}
		return false;
	}

	public static int increment_ERROR_PAY_PASSWORD_COUNT(String id, Date time) {
		String redisKeyErrorPasswordCount = RedisDef.LOGIN.ERROR_PAY_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + id;
		int LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT);
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		String redisValue = "0|" + System.currentTimeMillis();
		if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
			redisValue = (String) operations.get(redisKeyErrorPasswordCount);
			if (!redisValue.contains("|")) {
				redisValue = "0|" + System.currentTimeMillis();
			}
		}
		String[] values = redisValue.split("\\|");
		int now = Integer.valueOf(values[0]) + 1;
		int returnNum = 0;
		if (now <= LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT) {
			returnNum = LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT - now;
		} else if (now <= (LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 2)) {
			returnNum = LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 2 - now;
		} else if (now <= (LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 3)) {
			returnNum = LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT * 3 - now;
		}
		redisValue = now + "|" + System.currentTimeMillis();
		operations.set(redisKeyErrorPasswordCount, redisValue);
		redisTemplate.expire(redisKeyErrorPasswordCount, 3600 * 24l, TimeUnit.SECONDS);
		return returnNum;

		// BoundValueOperations<String, Object> operationsErrorPasswordCount = redisTemplate.boundValueOps(redisKeyErrorPasswordCount);
		// operationsErrorPasswordCount.increment(1);
		// operationsErrorPasswordCount.expire(3600 * 24l, TimeUnit.SECONDS);// 1天
	}

	public static void delete_ERROR_PAY_PASSWORD_COUNT(String id, Date time) {
		String redisKeyErrorPasswordCount = RedisDef.LOGIN.ERROR_PAY_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + id;
		if (redisTemplate.hasKey(redisKeyErrorPasswordCount)) {
			redisTemplate.delete(redisKeyErrorPasswordCount);
		}
	}

	public static String key_ORDER_NO(String payBankId, String orderNo, Date now) {
		if (payBankId != null && payBankId.length() > 0) {
			return RedisDef.TRADE_SUMBIT.ORDER_NO + RedisDef.DELIMITER.UNDERLINE + payBankId + RedisDef.DELIMITER.UNDERLINE + orderNo;
		}
		return RedisDef.TRADE_SUMBIT.ORDER_NO + RedisDef.DELIMITER.UNDERLINE + orderNo;// + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(now,
																						// DateUtil.DATE_FORMAT_ONE);
	}

	public static void set_ORDER_NO(String redisKey, String orderNo, Date now) {
		String redisValue = "1";
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(redisKey, redisValue);
		int REDIS_PAY_ORDER_NO_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_PAY_ORDER_NO_EXPIRE_TIME_SEC);
		redisTemplate.expire(redisKey, REDIS_PAY_ORDER_NO_EXPIRE_TIME_SEC, TimeUnit.SECONDS);
	}

	public static boolean hasKey(String redisKey) {
		return redisTemplate.hasKey(redisKey);
	}

	public static String get_ORDER_NO_CHARGE() {
		String key = TRADE_SUMBIT.ORDER_NO_CHARGE;
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
		Long value = operations.increment(1L);
		if (value > 99999999l) {
			if (value == 100000000l) {
				operations.set(0);
			}
			return null;
		}
		String str = value.toString();
		String retStr = "0000000000000000".subSequence(0, 8 - str.length()) + str;
		return retStr;
	}

	public static Integer get_COUNT_BUYER() {
		String redisKey = RedisDef.COUNT.COUNT_BUYER;
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(redisKey);
		Object count = operations.get();
		if (count != null) {
			return Integer.valueOf(count.toString());
		}
		return null;
	}

	public static boolean hasKey_QR_CODE(String qrcode) {

		String redisKey = RedisDef.QR_CODE.QR_CODE + RedisDef.DELIMITER.UNDERLINE + qrcode;
		return hasKey(redisKey);
	}

	public static void setQR_CODE(String qrcode, ConsumerUserQRCode consumerUserQRCode) {
		String redisKey = RedisDef.QR_CODE.QR_CODE + RedisDef.DELIMITER.UNDERLINE + qrcode;
		ConsumerUserQRCode redisValue = consumerUserQRCode;

		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(redisKey, redisValue);
		int REDIS_QRCODE_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_QRCODE_EXPIRE_TIME_SEC);
		redisTemplate.expire(redisKey, REDIS_QRCODE_EXPIRE_TIME_SEC, TimeUnit.SECONDS);

	}

	public static ConsumerUserQRCode get_QR_CODE(String qrcode) {
		String redisKey = RedisDef.QR_CODE.QR_CODE + RedisDef.DELIMITER.UNDERLINE + qrcode;
		ValueOperations<ConsumerUserQRCode, Object> operations = redisTemplate.opsForValue();
		return (ConsumerUserQRCode) operations.get(redisKey);
	}

	public static void delete_key(String redisKey) {
		if (redisTemplate.hasKey(redisKey)) {
			redisTemplate.delete(redisKey);
		}
	}

	public static Object getByKey(String redisKey) {
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(redisKey);
		Object ob = operations.get();
		return ob;
	}

	public static void set_COUNT_BUYER(int count) {
		String redisKey = RedisDef.COUNT.COUNT_BUYER;
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(redisKey);
		operations.set(count);
	}

	public static Set<String> intersect(String redisKey_bank, String redisKey_epay) {
		return redisTemplate.opsForSet().intersect(redisKey_bank, redisKey_epay);
	}

	public static Set<String> difference(String redisKey_epay, String redisKey_bank) {
		return redisTemplate.opsForSet().difference(redisKey_epay, redisKey_bank);
	}

	public static void addRedisEpayPay(String redisKey_epay, List<TradeInfo> tradeInfoList) {
		List<Object> pipelinedResults = redisTemplate.executePipelined(new SessionCallback<Object>() {
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				for (TradeInfo tradeInfo : tradeInfoList) {
					Double mulD = DoubleUtil.mul(tradeInfo.getPaySumAmount(), 100d);
					String amount = mulD.toString();
					if (amount.contains(".")) {
						amount = amount.substring(0, amount.indexOf("."));
					}
					operations.opsForSet().add(redisKey_epay, tradeInfo.getMerchantOrderNo() + "_" + amount);
				}
				return null;
			}
		});
	}

	public static void addRedisBank(String redisKey_bank, List<BankTradeInfo> bankTradeInfoList) {
		List<Object> pipelinedResults = redisTemplate.executePipelined(new SessionCallback<Object>() {
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				for (BankTradeInfo bankTradeInfo : bankTradeInfoList) {
					operations.opsForSet().add(redisKey_bank, bankTradeInfo.getRefNo() + "_" + bankTradeInfo.getTransactionAmount());
				}
				return null;
			}
		});
	}

	/**
	 * 邮箱验证码，30分钟有效
	 * 
	 * @param userId
	 * @param vCode
	 */
	public static void setEmailVCode(String userId, String vCode) {
		String redisKey = RedisDef.EMAIL.VC + RedisDef.DELIMITER.UNDERLINE + userId;
		BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(redisKey);
		operations.set(vCode);
		redisTemplate.expire(redisKey, 60 * 30, TimeUnit.SECONDS);
	}

	public static String getEmailVCode(String userId) {
		String redisKey = RedisDef.EMAIL.VC + RedisDef.DELIMITER.UNDERLINE + userId;
		if (redisTemplate.hasKey(redisKey)) {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			return (String) operations.get(redisKey);
		}
		return null;
	}

	public static List<RedisVo> getAllRedisValue(String key) {
		List<RedisVo> list = new ArrayList<>();
		Date now = Calendar.getInstance().getTime();
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC);
		int REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS = REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC * 1000;

		int REQUEST_COUNT_REST_TIME_MS = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REQUEST_COUNT_REST_TIME_MS);

		getAllRedisValueForSet(list, key, "key");
		getAllRedisValueForSet(list, RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + MessageDef.USER_TYPE.CONSUMER + RedisDef.DELIMITER.UNDERLINE + key, "TOKEN");
		getAllRedisValueForSet(list, RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + MessageDef.USER_TYPE.MERCHANT + RedisDef.DELIMITER.UNDERLINE + key, "TOKEN");
		getAllRedisValueForSet(list, RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + MessageDef.USER_TYPE.GOV + RedisDef.DELIMITER.UNDERLINE + key, "TOKEN");
		getAllRedisValueForSet(list, RedisDef.LOGIN.TOKEN + RedisDef.DELIMITER.UNDERLINE + MessageDef.USER_TYPE.SYS + RedisDef.DELIMITER.UNDERLINE + key, "TOKEN");
		getAllRedisValueForSet(list, RedisDef.LOGIN.ERROR_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + key, "ERROR_PASSWORD_COUNT");
		getAllRedisValueForSet(list, RedisDef.LOGIN.ERROR_PAY_PASSWORD_COUNT + RedisDef.DELIMITER.UNDERLINE + key, "ERROR_PAY_PASSWORD_COUNT");
		getAllRedisValueForSet(list, RedisDef.QR_CODE.QR_CODE + RedisDef.DELIMITER.UNDERLINE + key, "QR_CODE");
		getAllRedisValueForSet(list, RedisDef.TRADE_SUMBIT.ORDER_NO + RedisDef.DELIMITER.UNDERLINE + key, "ORDER_NO");// bankid+_+orderno,or,orderno
		getAllRedisValueForSet(list, TRADE_SUMBIT.ORDER_NO_CHARGE, "ORDER_NO_CHARGE");
		getAllRedisValueForSet(list, RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE + key, "ACCOUNT_AMOUNT");

		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.DB + key + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS, "STATISTICS_REQUEST");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + key + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST_SUCC");

		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.BANK_RECHARGE + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST BANK_RECHARGE");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.BANK_RECHARGE + RedisDef.DELIMITER.UNDERLINE
				+ System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS, "STATISTICS_REQUEST_SUCC BANK_RECHARGE");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.BANK_REVERSE + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST BANK_REVERSE");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.BANK_REVERSE + RedisDef.DELIMITER.UNDERLINE
				+ System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS, "STATISTICS_REQUEST_SUCC BANK_REVERSE");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.CONSUMPTION + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST CONSUMPTION");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.CONSUMPTION + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST_SUCC CONSUMPTION");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.RECHARGE + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST RECHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.RECHARGE + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST_SUCC RECHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.CHARGE + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST_SUCC CHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_PRE.SUCC + RedisDef.STATISTICS_PRE.DB + RedisDef.STATISTICS.CHARGE + RedisDef.DELIMITER.UNDERLINE + System.currentTimeMillis() / REDIS_STATISTICS_MONITOR_TO_DB_EXPIRE_TIME_SEC_MS,
				"STATISTICS_REQUEST_SUCC CHARGE");

		getAllRedisValueForSet(list, RedisDef.COUNT.COUNT_BUYER, "COUNT_BUYER");
		getAllRedisValueForSet(list, RedisDef.COUNT.COUNT_QUEST + RedisDef.DELIMITER.UNDERLINE + (Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS), "COUNT_QUEST");
		getAllRedisValueForSet(list, RedisDef.COUNT.COUNT_QUEST_SUCC + RedisDef.DELIMITER.UNDERLINE + (Calendar.getInstance().getTime().getTime() / REQUEST_COUNT_REST_TIME_MS), "COUNT_QUEST_SUCC");
		getAllRedisValueForSet(list, RedisDef.COUNT.COUNT_DOWNLOAD_APP + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd"), "COUNT_DOWNLOAD_APP");

		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_DAY.BANK_RECHARGE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd"),
				"STATISTICS_LIMIT_DAY.BANK_RECHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_DAY.BANK_REVERSE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd"),
				"STATISTICS_LIMIT_DAY.BANK_REVERSE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_DAY.CONSUMPTION + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd"),
				"STATISTICS_LIMIT_DAY.CONSUMPTION");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_LIMIT_DAY.RECHARGE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd"),
				"STATISTICS_LIMIT_DAY.RECHARGE");
		getAllRedisValueForSet(list, RedisDef.STATISTICS_LIMIT_DAY.CHARGE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM-dd"),
				"STATISTICS_LIMIT_DAY.CHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM"),
				"STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_REVERSE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM"),
				"STATISTICS_LIMIT_MONTH.MONTHLY_BANK_REVERSE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_CONSUMPTION + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM"),
				"STATISTICS_LIMIT_MONTH.MONTHLY_CONSUMPTION");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_RECHARGE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM"),
				"STATISTICS_LIMIT_MONTH.MONTHLY_RECHARGE");
		getAllRedisValueForSet(list,
				RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_CHARGE + RedisDef.DELIMITER.UNDERLINE + key + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MM"),
				"STATISTICS_LIMIT_MONTH.MONTHLY_CHARGE");
		getAllRedisValueForSet(list, RedisDef.BI.PROFIT_STATEMENT + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(now, "MMdd"), "PROFIT_STATEMENT");
		getAllRedisValueForSet(list, RedisDef.EMAIL.VC + RedisDef.DELIMITER.UNDERLINE + key, "PROFIT_STATEMENT");

		return list;
	}

	private static void getAllRedisValueForSet(List<RedisVo> list, String redisKey, String remark) {
		if (redisTemplate.hasKey(redisKey)) {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			Object value = operations.get(redisKey);
			if (value != null) {
				RedisVo redisVo = new RedisVo();
				redisVo.setKey(redisKey);
				redisVo.setValue(String.valueOf(value));
				redisVo.setType(value.getClass().getName());
				redisVo.setRemark(remark);
				list.add(redisVo);
			}
		}
	}

	public static boolean setRedisValue(String key, String value, String type) {
		// if (key.startsWith(RedisDef.ACCOUNT_AMOUNT.ACCOUNT_AMOUNT + RedisDef.DELIMITER.UNDERLINE)) {
		// return false;
		// }
		if (type.contains("String")) {
			if (redisTemplate.hasKey(key)) {
				ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
				operations.set(key, value);
				redisTemplate.expire(key, 60 * 60 * 24, TimeUnit.SECONDS);// 24小时
				return true;
			}
		}

		if (type.contains("Integer")) {
			if (redisTemplate.hasKey(key)) {
				ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
				operations.set(key, Integer.valueOf(value));
				redisTemplate.expire(key, 60 * 60 * 24, TimeUnit.SECONDS);// 24小时
				return true;
			}
		}
		if (type.contains("Long")) {
			if (redisTemplate.hasKey(key)) {
				ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
				operations.set(key, Long.valueOf(value));
				redisTemplate.expire(key, 60 * 60 * 24, TimeUnit.SECONDS);// 24小时
				return true;
			}
		}
		if (type.contains("Double")) {
			if (redisTemplate.hasKey(key)) {
				ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
				operations.set(key, Double.valueOf(value));
				redisTemplate.expire(key, 60 * 60 * 24, TimeUnit.SECONDS);// 24小时
				return true;
			}
		}
		return false;
	}

	/**
	 * 用户验证码，存放15分钟
	 * 
	 * @param key
	 *            验证码key
	 * @param value
	 *            验证码值
	 * @return
	 */
	public static String setVerificationCode(String key, String value) {
		String redisKey = RedisDef.VERIFICATION_CODE.IMG_VERIFI_CODE + RedisDef.DELIMITER.UNDERLINE + key;
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		operations.set(redisKey, value);
		
		int REDIS_IMG_VALIDATE_CODE_EXPIRE_TIME_SEC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REDIS_IMG_VALIDATE_CODE_EXPIRE_TIME_SEC);
		redisTemplate.expire(redisKey, REDIS_IMG_VALIDATE_CODE_EXPIRE_TIME_SEC, TimeUnit.SECONDS);
		return key;
	}

	/**
	 * 获取用户验证码
	 * 
	 * @param key
	 *            验证码key
	 * @return 验证码值
	 */
	public static String getVerificationCode(String key) {
		String redisKey = RedisDef.VERIFICATION_CODE.IMG_VERIFI_CODE + RedisDef.DELIMITER.UNDERLINE + key;
		if (redisTemplate.hasKey(redisKey)) {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			return (String) operations.get(redisKey);
		}
		return null;
	}

	public static void deleteRedisKey(String redisKey) {
		if (redisTemplate.hasKey(redisKey)) {
			redisTemplate.delete(redisKey);
		}
	}

	/**
	 * 下载app统计
	 */
	public static void increment_COUNT_DOWNLOAD_APP() {
		String redisKeyCOUNT_DOWNLOAD_APP = RedisDef.COUNT.COUNT_DOWNLOAD_APP + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MMdd");
		boolean isFirst = false;
		if (!redisTemplate.hasKey(redisKeyCOUNT_DOWNLOAD_APP)) {
			isFirst = true;
		}
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKeyCOUNT_DOWNLOAD_APP);
		operations.increment(1);
		if (isFirst) {
			redisTemplate.expire(redisKeyCOUNT_DOWNLOAD_APP, 60 * 60 * 24 * 2, TimeUnit.SECONDS);// 24小时*2
		}
	}

	public static Long get_COUNT_DOWNLOAD_APP() {
		String redisKeyCOUNT_DOWNLOAD_APP = RedisDef.COUNT.COUNT_DOWNLOAD_APP + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(Calendar.getInstance().getTime(), "MMdd");
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKeyCOUNT_DOWNLOAD_APP);
		Object objectValue = operations.get();
		Long value = 0l;
		if (objectValue != null) {
			value = Long.valueOf(String.valueOf(objectValue));
		}
		return value;
	}

	public static Long get_YesterdayCOUNT_DOWNLOAD_APP() {
		String redisKeyCOUNT_DOWNLOAD_APP = RedisDef.COUNT.COUNT_DOWNLOAD_APP + RedisDef.DELIMITER.UNDERLINE + DateUtil.convertDateToString(DateUtils.addDays(Calendar.getInstance().getTime(), -1), "MMdd");
		BoundValueOperations<String, Long> operations = redisTemplate.boundValueOps(redisKeyCOUNT_DOWNLOAD_APP);
		Object objectValue = operations.get();
		Long value = 0l;
		if (objectValue != null) {
			value = Long.valueOf(String.valueOf(objectValue));
		}
		return value;
	}
}
