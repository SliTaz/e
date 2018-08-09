package com.zbensoft.e.payment.api.quartz.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.common.util.DateUtil;

/**
 * 测试
 * 
 * 0 10 0/2 * * ?
 * 
 * @author xieqiang
 *
 */
public class TestJob implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO("now Date is:"+DateUtil.convertDateToFormatString(new Date()));
	}
}
