package com.zbensoft.e.payment.api.quartz.job;

import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.BI_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.BalanceStatisticsService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.BalanceStatistics;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantUser;

/**
 * 账户余额更新入库， 每天凌晨2点执行一次
 * 
 * 0 0 2 * * ?
 * 
 * @author xieqiang
 *
 */
public class AccountAmountJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(AccountAmountJob.class);

	private static String key = "AccountAmountJob";

	private ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);
	private MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	private BalanceStatisticsService balanceStatisticsService = SpringBeanUtil.getBean(BalanceStatisticsService.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		BI_LOG.INFO(String.format("%s Start", key));
		List<String> sets = RedisUtil.keys_ACCOUNT_AMOUNT();
		if (sets != null && sets.size() > 0) {
			for (String redisKey : sets) {
				try {
					String userId = RedisUtil.userId_ACCOUNT_AMOUNT(redisKey);
					Double balance = RedisUtil.get_ACCOUNT_AMOUNT(userId);
					if (balance != null) {
						updateUserAmount(userId, balance);
					}
				} catch (Exception e) {
					log.error(String.format("%s updateUserAmount fail,key=%s", key, redisKey), e);
					TASK_LOG.INFO(String.format("%s updateUserAmount fail,key=%s", key, redisKey));
					TASK_LOG.ERROR(String.format("%s updateUserAmount fail,key=%s", key, redisKey), e);
				}
			}
		}

		Double buyerBalance = consumerUserService.selectSumBalance();
		Double sellerBalance = merchantUserService.selectSumBalance();
		BalanceStatistics balanceStatistics = new BalanceStatistics();
		balanceStatistics.setStatisticsTime(DateUtil.convertDateToString(Calendar.getInstance().getTime()));
		balanceStatistics.setBuyerBalance(buyerBalance);
		balanceStatistics.setSellerBalance(sellerBalance);
		balanceStatisticsService.insert(balanceStatistics);
		TASK_LOG.INFO(String.format("%s buyerBalance=%s,sellerBalance=%s", key, buyerBalance, sellerBalance));
		BI_LOG.INFO(String.format("%s buyerBalance=%s,sellerBalance=%s", key, buyerBalance, sellerBalance));

		TASK_LOG.INFO(String.format("%s End", key));
		BI_LOG.INFO(String.format("%s End", key));
	}

	private void updateUserAmount(String userId, Double balance) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerUser consumerUser = new ConsumerUser();
			consumerUser.setUserId(userId);
			consumerUser.setBalance(balance);
			consumerUserService.updateAmountByPrimaryKey(consumerUser);
		} else if (userId.startsWith(MessageDef.USER_TYPE.MERCHANT_STRING)) {
			MerchantUser merchantUser = new MerchantUser();
			merchantUser.setUserId(userId);
			merchantUser.setBalance(balance);
			merchantUserService.updateAmountByPrimaryKey(merchantUser);
		} else {
			TASK_LOG.INFO(String.format("%s updateUserAmount not this type user,userId=%s", key, userId));
		}
	}

}
