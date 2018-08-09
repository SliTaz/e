package com.zbensoft.e.payment.api.quartz.job.clap;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread.EnCodeThread2;
import com.zbensoft.e.payment.common.mutliThread.MultiThreadManage;

/**
 * 测试
 * 
 * 0 10 0/2 * * ?
 * 
 * @author xieqiang
 *
 */
public class CloseFirstBuyerDataJob implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		MultiThreadManage.getInstance().addThread(EnCodeThread2.class, 0,100, 100);
	}
}
