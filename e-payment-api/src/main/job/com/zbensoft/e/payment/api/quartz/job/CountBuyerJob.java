package com.zbensoft.e.payment.api.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.db.domain.ConsumerUser;

/**
 * 查询用户总数，每天执行一次
 * 
 * 0 0 2 * * ?
 * 
 * @author xieqiang
 *
 */
public class CountBuyerJob implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());

	private static String key = "CountBuyerJob";

	private ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		int count = consumerUserService.count(new ConsumerUser());
		if (count > 0) {
			RedisUtil.set_COUNT_BUYER(count);
			TASK_LOG.INFO(String.format("%s CountBuyer = %s", key, count));
		}
		TASK_LOG.INFO(String.format("%s End", key));
	}
}
