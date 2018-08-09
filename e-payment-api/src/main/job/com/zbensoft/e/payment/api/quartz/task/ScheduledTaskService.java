package com.zbensoft.e.payment.api.quartz.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.common.CommonFun;

@Service
public class ScheduledTaskService {

	/**
	 * 每5分钟执行一次，处理系统配置
	 */
	@Scheduled(cron = "0 0/5 * * * ? ")
	public void fixTimeExecution() {
		CommonFun.loadConfig();
	}

}
