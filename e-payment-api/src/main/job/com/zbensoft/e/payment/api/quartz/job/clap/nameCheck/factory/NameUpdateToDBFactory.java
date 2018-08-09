package com.zbensoft.e.payment.api.quartz.job.clap.nameCheck.factory;

import java.util.ArrayList;
import java.util.List;

import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread.DBThread;
import com.zbensoft.e.payment.api.quartz.job.clap.nameCheck.thread.NameUpdateToDBThread;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

public class NameUpdateToDBFactory {

	private static NameUpdateToDBFactory instance = null;


	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();

	private List<ConsumerUser> consumerUserList=new ArrayList<>();
	
	public static NameUpdateToDBFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new NameUpdateToDBFactory();
					new NameUpdateToDBThread("----NameUpdateToDBThread----").start();
				}
			}
		}
		return instance;
	}
	
	public void addConsumerUser(ConsumerUser consumerUser){
		synchronized (objectLock) {
			consumerUserList.add(consumerUser);
		}
	}
	
	
	
	
	/**
	 * 线程取得入库数据
	 * 
	
	 */
	public void getConsumerUserToDB(List<ConsumerUser> list, int count) {
		synchronized (objectLock) {
			if (consumerUserList != null && consumerUserList.size() > 0) {
				for (int i = consumerUserList.size() - 1; i >= 0; i--) {
					list.add(consumerUserList.remove(i));
					if (list.size() >= count) {
						break;
					}
				}
			}
		}

	}
	
	
	
	
	
	
	public void listStatus() throws Exception {
		synchronized (objectLock) {
			TASK_LOG.INFO("consumerUserList:"+consumerUserList.size());
		}
	}
	
	
	
}
