package com.zbensoft.e.payment.api.alarm.alarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;

public class LVSRealServerAlarm extends Alarm {

private static final Logger log = LoggerFactory.getLogger(LVSRealServerAlarm.class);

	@Override
	public void initSetup() {
	}

	@Override
	public boolean haveAlarm() {
		try {
			boolean httpAlarm = isRunningHTTP();
			// boolean httpsAlarm = isRunningHTTPS();
			// if (httpAlarm || httpsAlarm) {
			if (httpAlarm) {
				int alarmtTimeSec = 0;
				if (isFirstCount) {
					alarmtTimeSec = message.getAlarmTimeSec();
					if (startTime == 0) {
						startTime = System.currentTimeMillis();
					}
				} else {
					alarmtTimeSec = message.getAlarmFrequencyTimeSec();
				}
				if (System.currentTimeMillis() - startTime > alarmtTimeSec * 1000) {
					startTime = System.currentTimeMillis();
					isFirstCount = false;
					message.setAlarm(true);
					StringBuffer sb = new StringBuffer();
					sb.append(AlarmUtil.getIPInfo());
					// sb.append("service RealServer_http status or service RealServer_https status not running").append(MailUtil.NEW_LINE);
					sb.append("service RealServer_http status status not running").append(MailUtil.NEW_LINE);
					sb.append("Please check the server!").append(MailUtil.NEW_LINE);
					message.setContent(sb.toString());
					return true;
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		message.setAlarm(false);
		message.setContent(null);
		return false;
	}

	private boolean isRunningHTTP() {
		Process pro1;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "service RealServer_http status";
			// 第一次采集CPU时间
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			while ((line = in1.readLine()) != null) {
				if (line.contains("Running")) {
					return false;
				}
			}
			in1.close();
			pro1.destroy();

		} catch (IOException e) {
			log.error("", e);
		}
		return true;
	}

	private boolean isRunningHTTPS() {
		Process pro1;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "service RealServer_https status";
			// 第一次采集CPU时间
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			while ((line = in1.readLine()) != null) {
				if (line.contains("Running")) {
					return false;
				}
			}
			in1.close();
			pro1.destroy();

		} catch (IOException e) {
			log.error("", e);
		}
		return true;
	}

}
