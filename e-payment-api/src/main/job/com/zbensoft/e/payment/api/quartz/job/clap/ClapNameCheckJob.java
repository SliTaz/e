package com.zbensoft.e.payment.api.quartz.job.clap;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.quartz.job.clap.nameCheck.factory.NameUpdateToDBFactory;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

public class ClapNameCheckJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(ClapNameCheckJob.class);

	@Value("${password.default}")
	private String DEFAULT_PASSWORD;

	@Value("${payPassword.default}")
	private String DEFAULT_PAYPASSWORD;

	private static String key = "ClapNameCheckJob";
	ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);

	ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s start", key));
		nameCheck();
		TASK_LOG.INFO(String.format("%s  end", key));
	}

	private boolean nameCheck() {
		
		int start = 0;
		long startTime=System.currentTimeMillis();
		long useTime=0;
		List<ConsumerUserClap> consumerUserClapList = new ArrayList<>();
		while (true) {

			int pageNum = PageHelperUtil.getPageNum(String.valueOf(start), String.valueOf(1000));
			int pageSize = PageHelperUtil.getPageSize(String.valueOf(start), String.valueOf(1000));
			PageHelper.startPage(pageNum, pageSize);
			consumerUserClapList = consumerUserClapService.selectPage(new ConsumerUserClap());

			if (consumerUserClapList != null) {
				if (consumerUserClapList.size() == 1000) {

					for (ConsumerUserClap consumerUserClapResult : consumerUserClapList) {
						ConsumerUser consumerUserUpdate = new ConsumerUser();
						consumerUserUpdate.setUserId(consumerUserClapResult.getUserId());
						consumerUserUpdate.setUserName(getUserName(consumerUserClapResult));
						
						NameUpdateToDBFactory.getInstance().addConsumerUser(consumerUserUpdate);
					}
				} else {
					if (consumerUserClapList.size() > 0) {
						for (ConsumerUserClap consumerUserClapResult : consumerUserClapList) {
							ConsumerUser consumerUserUpdate = new ConsumerUser();
							consumerUserUpdate.setUserId(consumerUserClapResult.getUserId());
							consumerUserUpdate.setUserName(getUserName(consumerUserClapResult));
							NameUpdateToDBFactory.getInstance().addConsumerUser(consumerUserUpdate);
						}
					}
					return true;
				}
			} else {
				return false;
			}
			useTime=(System.currentTimeMillis()-startTime)/1000;
			TASK_LOG.INFO(String.format("%s finish Page %d, useTime=%d", key,pageNum,useTime));
			start += 1000;

		}

	}

	/**
	 * 获取用户名: Name1 LastName1
	 * 
	 * @param consumerUserClap
	 * @return
	 */
	private String getUserName(ConsumerUserClap consumerUserClap) {
		String names = null;
		if (consumerUserClap.getName1() != null && !"".equals(consumerUserClap.getName1())) {
			names = consumerUserClap.getName1();
		}
		if (consumerUserClap.getLastName1() != null && !"".equals(consumerUserClap.getLastName1())) {
			names += " " + consumerUserClap.getLastName1();
		}

		if (names == null) {
			if (consumerUserClap.getName2() != null && !"".equals(consumerUserClap.getName2())) {
				names = consumerUserClap.getName2();
			}
			if (consumerUserClap.getLastName2() != null && !"".equals(consumerUserClap.getLastName2())) {
				names += " " + consumerUserClap.getLastName2();
			}
		}

		return names;
	}



}
