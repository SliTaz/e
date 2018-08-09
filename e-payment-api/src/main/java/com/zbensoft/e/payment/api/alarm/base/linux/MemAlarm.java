package com.zbensoft.e.payment.api.alarm.base.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

public class MemAlarm extends Alarm{

private static final Logger log = LoggerFactory.getLogger(MemAlarm.class);


	private int alarm_mem = 0;
	@Override
	public void initSetup() {
		alarm_mem = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_MEM);
	}

	@Override
	public boolean haveAlarm() {
		try {
			float value = getMem();
			if (value > alarm_mem) {
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
					sb.append("memery out max num:").append(alarm_mem).append(MailUtil.NEW_LINE);
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

	private float getMem() {
			float memUsage = 0.0f;
			Process pro = null;
			Runtime r = Runtime.getRuntime();
			try {
				String command = "cat /proc/meminfo";
				pro = r.exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
				String line = null;
				int count = 0;
				long totalMem = 0, freeMem = 0;
				while ((line = in.readLine()) != null) {
					String[] memInfo = line.split("\\s+");
					if (memInfo[0].startsWith("MemTotal")) {
						totalMem = Long.parseLong(memInfo[1]);
					}
					if (memInfo[0].startsWith("MemFree")) {
						freeMem = Long.parseLong(memInfo[1]);
					}
					memUsage = 1 - (float) freeMem / (float) totalMem;
					if (++count == 2) {
						break;
					}
				}
				in.close();
				pro.destroy();
			} catch (IOException e) {
				log.error("", e);
			}
	
			return memUsage;
	}

}
