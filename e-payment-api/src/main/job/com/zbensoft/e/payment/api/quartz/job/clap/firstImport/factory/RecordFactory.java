package com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory;

import java.util.ArrayList;
import java.util.List;

import com.zbensoft.e.payment.api.log.TASK_LOG;

public class RecordFactory {

	private static RecordFactory instance = null;


	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();

	private List<String> recordsList=new ArrayList<>();
	private long total=0l;
	private long count1=0l;
	long tmp=0;
	
	private long startTime=0;

	public static RecordFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new RecordFactory();
				}
			}
		}
		return instance;
	}
	
	public void add(String record){
		synchronized (objectLock) {
			recordsList.add(record);
		}
	}
	
	/**
	 * 线程取得入库数据
	 * 
	
	 */
	public void getToDB(List<String> rList, int count) {
		synchronized (objectLock) {
			if (recordsList != null && recordsList.size() > 0) {
				for (int i = recordsList.size() - 1; i >= 0; i--) {
					rList.add(recordsList.remove(i));
					count1++;
					if (rList.size() >= count) {
						break;
					}
				}
			}
		}

	}
	
	public Boolean isLimit(int limit) throws Exception {
		synchronized (objectLock) {
			if(recordsList.size()>limit){
				return true;
			}
			return false;
		}
	}
	
	
	public void setTotal(double number)  {
		synchronized (objectLock) {
			total=(long)number;
			startTime=System.currentTimeMillis();
		}
	}
	
	public long getListSieze()  {
		synchronized (objectLock) {
			return recordsList.size();
		}
	}
	
	
	public String showPercent()  {
		synchronized (objectLock) {
			if(total>0){
				double perUsetime=0;
				double speed=(double)count1/(double)((System.currentTimeMillis()-startTime)/1000);
				if(speed>0){
					double time=(double)(total-count1)/speed;
					 perUsetime=time/60;
				}
				double perCent=((double)count1)/((double)total)*100;
				 return count1+"/"+total+"("+String.format("%.2f", perCent)+"% Expected time:"+(long)perUsetime+" min)";
			}
			return "0/0";
		}
		
	}
	
}
