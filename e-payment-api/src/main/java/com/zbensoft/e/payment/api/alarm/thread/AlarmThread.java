package com.zbensoft.e.payment.api.alarm.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.AlarmMangerFactory;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

public class AlarmThread extends Thread {

	private static final Logger log = LoggerFactory.getLogger(AlarmThread.class);

	public AlarmThread(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (true) {
			try {
				AlarmMangerFactory.getInstance().handle();
				int alarm_thread_time_sec = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_THREAD_TIME_SEC);
				Thread.sleep(1000 * alarm_thread_time_sec);
				// Thread.sleep(1000);
			} catch (Exception e) {
				log.error("", e);
			}
		}

	}

}
