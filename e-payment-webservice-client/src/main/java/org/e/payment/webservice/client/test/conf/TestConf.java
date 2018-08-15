package org.e.payment.webservice.client.test.conf;

import java.util.ArrayList;
import java.util.List;

public class TestConf {
	private int threadCount = 1;
	private int maxThreadCount = 1;
	private int sleepTimeMS = 2;
	private int countForOneProcess = 1;
	private int testType = 0;
	private List<String> bankIdList = new ArrayList<String>();
	private List<String> vidList = new ArrayList<String>();
	
	private boolean isStart = false;
	
	public int getTestType() {
		return testType;
	}
	public void setTestType(int testType) {
		this.testType = testType;
	}
	public int getMaxThreadCount() {
		return maxThreadCount;
	}
	public void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public int getCountForOneProcess() {
		return countForOneProcess;
	}
	public void setCountForOneProcess(int countForOneProcess) {
		this.countForOneProcess = countForOneProcess;
	}
	public int getSleepTimeMS() {
		return sleepTimeMS;
	}
	public void setSleepTimeMS(int sleepTimeMS) {
		this.sleepTimeMS = sleepTimeMS;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public List<String> getBankIdList() {
		return bankIdList;
	}
	public void setBankIdList(List<String> bankIdList) {
		this.bankIdList = bankIdList;
	}
	public List<String> getVidList() {
		return vidList;
	}
	public void setVidList(List<String> vidList) {
		this.vidList = vidList;
	}
	
	
	
}
