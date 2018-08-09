package com.zbensoft.e.payment.api.quartz.job.clap.nameCheck.thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.quartz.job.clap.nameCheck.factory.NameUpdateToDBFactory;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.ConsumerUser;


public class NameUpdateToDBThread extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(NameUpdateToDBThread.class);

	ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);

	ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);;

	MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	ConsumerFamilyService consumerFamilyService = SpringBeanUtil.getBean(ConsumerFamilyService.class);

	ConsumerRoleUserService consumerRoleUserService = SpringBeanUtil.getBean(ConsumerRoleUserService.class);


	public NameUpdateToDBThread(String name) {
		super(name);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			log.error("", e1);
		}
		while (true) {
			try {
				List<ConsumerUser> consumerUserList = new ArrayList<>();

				NameUpdateToDBFactory.getInstance().getConsumerUserToDB(consumerUserList, 2000);
				
				consumerUserService.updateList(consumerUserList);

			} catch (Exception e) {
				log.error("", e);
			}
		}

	}
}
