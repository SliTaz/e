package com.zbensoft.e.payment.api.quartz.job;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.BI_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ProfitStatementService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ProfitStatement;

/**
 * BI利润表， 每天6点执行一次
 * 
 * 0 0 6 * * ?
 * 
 * @author xieqiang
 *
 */
public class BIProfitStatementToDBJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(BIProfitStatementToDBJob.class);

	private static String key = "BIProfitStatementToDBJob";

	private ProfitStatementService profitStatementService = SpringBeanUtil.getBean(ProfitStatementService.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		BI_LOG.INFO(String.format("%s Start", key));
		try {

			Date date = Calendar.getInstance().getTime();
			Date yesterday = DateUtils.addDays(date, -1);
			Double value = RedisUtil.get_BI_PROFIT_STATEMENT(yesterday);

			ProfitStatement profitStatement = new ProfitStatement();
			profitStatement.setStatisticsTime(DateUtil.convertDateToString(yesterday));
			profitStatement.setAmount(value);
			profitStatementService.insert(profitStatement);
			TASK_LOG.INFO(String.format("%s profitStatement = %s", key, value));
			BI_LOG.INFO(String.format("%s profitStatement = %s", key, value));
		} catch (Exception e) {
			log.error(String.format("%s error", key), e);
			TASK_LOG.ERROR(String.format("%s error", key), e);
			BI_LOG.ERROR(String.format("%s error", key), e);
		}
		TASK_LOG.INFO(String.format("%s end", key));
		BI_LOG.INFO(String.format("%s end", key));
	}

}
