package com.zbensoft.e.payment.api.config.quartz;

import java.io.IOException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置任务调度中心 [QRTZ_JOB_DETAILS], [QRTZ_TRIGGERS] and [QRTZ_CRON_TRIGGERS]
 * 
 * @author lance
 */
@Configuration
public class QuartzConfig {

	@Bean
	public Scheduler scheduler() throws IOException, SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.start();
		return scheduler;
	}
}