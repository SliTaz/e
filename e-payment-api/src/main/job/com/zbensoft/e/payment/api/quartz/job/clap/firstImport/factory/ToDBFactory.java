package com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory;

import java.util.ArrayList;
import java.util.List;

import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread.DBThread;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

public class ToDBFactory {

	private static ToDBFactory instance = null;


	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();

	private List<ConsumerUser> consumerUserList=new ArrayList<>();
	private List<ConsumerUserClap> consumerUserClapList=new ArrayList<>();
	private List<ConsumerFamily> consumerFamilyList=new ArrayList<>();
	private List<ConsumerRoleUserKey> consumerRoleUserKeyList=new ArrayList<>();
	
	public static ToDBFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new ToDBFactory();
					new DBThread("buyser-toDBThread").start();
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
	
	public void addConsumerUserClap(ConsumerUserClap consumerUserClap){
		synchronized (objectLock) {
			consumerUserClapList.add(consumerUserClap);
		}
	}
	public void addConsumerFamilyList(ConsumerFamily consumerFamily){
		synchronized (objectLock) {
			consumerFamilyList.add(consumerFamily);
		}
	}
	public void addConsumerRoleUserKeyr(ConsumerRoleUserKey consumerRoleUserKey){
		synchronized (objectLock) {
			consumerRoleUserKeyList.add(consumerRoleUserKey);
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
	
	public void getConsumerUserClapToDB(List<ConsumerUserClap> list, int count) {
		synchronized (objectLock) {
			if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
				for (int i = consumerUserClapList.size() - 1; i >= 0; i--) {
					list.add(consumerUserClapList.remove(i));
					if (list.size() >= count) {
						break;
					}
				}
			}
		}
	}
	
	public void getConsumerFamilyToDB(List<ConsumerFamily> list, int count) {
		synchronized (objectLock) {
			if (consumerFamilyList != null && consumerFamilyList.size() > 0) {
				for (int i = consumerFamilyList.size() - 1; i >= 0; i--) {
					list.add(consumerFamilyList.remove(i));
					if (list.size() >= count) {
						break;
					}
				}
			}
		}
	}
	
	
	public void getConsumerRoleUserKeyToDB(List<ConsumerRoleUserKey> list, int count) {
		synchronized (objectLock) {
			if (consumerRoleUserKeyList != null && consumerRoleUserKeyList.size() > 0) {
				for (int i = consumerRoleUserKeyList.size() - 1; i >= 0; i--) {
					list.add(consumerRoleUserKeyList.remove(i));
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
			TASK_LOG.INFO("consumerUserClapList:"+consumerUserClapList.size());
			TASK_LOG.INFO("consumerFamilyList:"+consumerFamilyList.size());
			TASK_LOG.INFO("consumerRoleUserKeyList:"+consumerRoleUserKeyList.size());
		}
	}
	
	
	
}
