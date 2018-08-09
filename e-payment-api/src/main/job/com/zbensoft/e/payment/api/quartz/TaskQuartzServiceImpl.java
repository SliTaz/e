package com.zbensoft.e.payment.api.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.log.TASK_LOG;

@Service
public class TaskQuartzServiceImpl {

	private static final Logger log = LoggerFactory.getLogger(TaskQuartzServiceImpl.class);

	@Autowired
	private Scheduler scheduler;

	// @Value("${spring.jackson.time-zone}")

//	@Value("${quartz.time-zone}")
//	private String timezone;

	/**
	 * 所有任务列表 2016年10月9日上午11:16:59
	 */
	public List<TaskQuartzInfo> list() {
		List<TaskQuartzInfo> list = new ArrayList<>();

		try {
			for (String groupJob : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupJob))) {
					List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
					for (Trigger trigger : triggers) {
						Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

						String cronExpression = "", createTime = "";

						TaskQuartzInfo info = new TaskQuartzInfo();
						if (trigger instanceof CronTrigger) {
							CronTrigger cronTrigger = (CronTrigger) trigger;
							cronExpression = cronTrigger.getCronExpression();
							createTime = cronTrigger.getDescription();
							info.setNextFireTime(cronTrigger.getNextFireTime());
							info.setPreviousFireTime(cronTrigger.getPreviousFireTime());
							info.setFinalFireTime(cronTrigger.getFinalFireTime());
							info.setStartTime(cronTrigger.getStartTime());
							info.setEndTime(cronTrigger.getEndTime());
							info.setTimeZone(cronTrigger.getTimeZone());
						}
						info.setJobName(jobKey.getName());
						info.setJobGroup(jobKey.getGroup());
						try {

							JobDetail jobDetail = scheduler.getJobDetail(jobKey);
							info.setJobDescription(jobDetail.getDescription());
						} catch (Exception e) {
							log.error("", e);
						}
						info.setJobStatus(triggerState.name());
						info.setCronExpression(cronExpression);
						info.setCreateTime(createTime);
						list.add(info);
					}
				}
			}
		} catch (SchedulerException e) {
			log.error("", e);
		}

		return list;
	}

	public HttpRestStatus addJob(TaskQuartzInfo info) {
		String jobName = info.getJobName(), jobGroup = info.getJobGroup(), cronExpression = info.getCronExpression(), jobDescription = info.getJobDescription(),
				createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		try {
			if (checkExists(jobName, jobGroup)) {
				return HttpRestStatus.EXIST;
			}

			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
//			TimeZone userTimeZone = TimeZone.getTimeZone(timezone);
			CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression);//.withMisfireHandlingInstructionDoNothing().inTimeZone(userTimeZone);
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(schedBuilder).build();

			Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(jobDescription).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			return HttpRestStatus.TASK_CRON_ERROR;
		} catch (ClassNotFoundException e) {
			return HttpRestStatus.TASK_JOB_CLASS_NOT_EXIST;
		} catch (Exception e) {
			return HttpRestStatus.UNKNOWN;
		}

		TASK_LOG.INFO(String.format("Add Task=%s", JSONArray.toJSONString(info)));
		return HttpRestStatus.OK;
	}

	public HttpRestStatus edit(TaskQuartzInfo info) {
		String jobName = info.getJobName(), jobGroup = info.getJobGroup(), cronExpression = info.getCronExpression(), jobDescription = info.getJobDescription(),
				createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		try {
			if (!checkExists(jobName, jobGroup)) {
				return HttpRestStatus.NOT_EXIST;
			}
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			JobKey jobKey = new JobKey(jobName, jobGroup);
//			TimeZone userTimeZone = TimeZone.getTimeZone(timezone);
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);//.withMisfireHandlingInstructionDoNothing().inTimeZone(userTimeZone);
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime).withSchedule(cronScheduleBuilder).build();

			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			jobDetail.getJobBuilder().withDescription(jobDescription);
			HashSet<Trigger> triggerSet = new HashSet<>();
			triggerSet.add(cronTrigger);

			scheduler.scheduleJob(jobDetail, triggerSet, true);
		} catch (SchedulerException e) {
			return HttpRestStatus.TASK_CRON_ERROR;
		} catch (Exception e) {
			return HttpRestStatus.UNKNOWN;
		}
		TASK_LOG.INFO(String.format("Edit Task=%s", JSONArray.toJSONString(info)));
		return HttpRestStatus.OK;
	}

	/**
	 * 删除定时任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 *            2016年10月9日下午1:51:12
	 */
	public HttpRestStatus delete(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			if (checkExists(jobName, jobGroup)) {
				scheduler.pauseTrigger(triggerKey);
				scheduler.unscheduleJob(triggerKey);
			}
		} catch (SchedulerException e) {
			return HttpRestStatus.UNKNOWN;
		}
		TASK_LOG.INFO(String.format("Delete jobName=%s,jobGroup=%s", jobName, jobGroup));
		return HttpRestStatus.OK;
	}

	/**
	 * 暂停定时任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 *            2016年10月10日上午9:40:19
	 */
	public HttpRestStatus pause(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			if (checkExists(jobName, jobGroup)) {
				scheduler.pauseTrigger(triggerKey);
			}
		} catch (SchedulerException e) {
			return HttpRestStatus.UNKNOWN;
		}
		TASK_LOG.INFO(String.format("pause jobName=%s,jobGroup=%s", jobName, jobGroup));
		return HttpRestStatus.OK;
	}

	/**
	 * 重新开始任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 *            2016年10月10日上午9:40:58
	 */
	public HttpRestStatus resume(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

		try {
			if (checkExists(jobName, jobGroup)) {
				scheduler.resumeTrigger(triggerKey);
			}
		} catch (SchedulerException e) {
			return HttpRestStatus.UNKNOWN;
		}
		TASK_LOG.INFO(String.format("resume jobName=%s,jobGroup=%s", jobName, jobGroup));
		return HttpRestStatus.OK;
	}

	/**
	 * 验证是否存在
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @throws SchedulerException
	 *             2016年10月8日下午5:30:43
	 */
	private boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		return scheduler.checkExists(triggerKey);
	}
}
