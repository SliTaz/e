package org.e.payment.webservice.client.test.conf;

import org.e.payment.webservice.client.test.thread.TestThread;

import com.zbensoft.e.payment.common.mutliThread.MultiThreadManage;

public class TestManager {

	private static TestManager instance = new TestManager();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();

	private static TestConf config = new TestConf();

	public static TestManager getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new TestManager();
				}
			}
		}
		return instance;
	}

	public void start() {
		synchronized (objectLock) {
			MultiThreadManage.getInstance().addThread(TestThread.class, config.getThreadCount(),
					config.getMaxThreadCount(), config.getSleepTimeMS());
		}
	}

	public void stop() {
		synchronized (objectLock) {
			config.setThreadCount(0);
			MultiThreadManage.getInstance().addThread(TestThread.class, 0, config.getMaxThreadCount(),
					config.getSleepTimeMS());
		}
	}

	public TestConf getConf() {
		synchronized (objectLock) {
			return config;
		}
	}

	public void setConf(int threadCount, int maxThreadCount, int sleepTimeMS, int countForOneProcess,
			int testType) {
		synchronized (objectLock) {
			config.setThreadCount(threadCount);
			config.setMaxThreadCount(maxThreadCount);
			config.setSleepTimeMS(sleepTimeMS);
			config.setCountForOneProcess(countForOneProcess);
			config.setTestType(testType);;
			config.getBankIdList().clear();
			config.getVidList().clear();
		}
	}

	public void addBank(String bankId) {
		synchronized (objectLock) {
			config.getBankIdList().add(bankId);
		}
	}

	public void addVid(String vidStr) {
		synchronized (objectLock) {
			config.getVidList().add(vidStr);
		}
	}
}
