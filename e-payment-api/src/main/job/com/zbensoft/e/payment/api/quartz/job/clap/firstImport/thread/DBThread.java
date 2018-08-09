package com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory.RecordFactory;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory.ToDBFactory;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

public class DBThread extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(DBThread.class);

	ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);

	ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);;

	MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	ConsumerFamilyService consumerFamilyService = SpringBeanUtil.getBean(ConsumerFamilyService.class);

	ConsumerRoleUserService consumerRoleUserService = SpringBeanUtil.getBean(ConsumerRoleUserService.class);


	public DBThread(String name) {
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
				List<ConsumerUserClap> consumerUserClapList = new ArrayList<>();
				List<ConsumerFamily> consumerFamilyList = new ArrayList<>();
				List<ConsumerRoleUserKey> consumerRoleUserKeyList = new ArrayList<>();


				ToDBFactory.getInstance().getConsumerUserToDB(consumerUserList, 2000);

				ToDBFactory.getInstance().getConsumerUserClapToDB(consumerUserClapList, 2000);

				ToDBFactory.getInstance().getConsumerFamilyToDB(consumerFamilyList, 2000);

				ToDBFactory.getInstance().getConsumerRoleUserKeyToDB(consumerRoleUserKeyList, 2000);
				
				consumerUserService.insertList(consumerUserList,consumerUserClapList,consumerFamilyList,consumerRoleUserKeyList);

				//ToDBFactory.getInstance().listStatus();
				RecordFactory.getInstance().showPercent();
			} catch (Exception e) {
				log.error("", e);
			}
		}

	}
}
