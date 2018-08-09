package com.zbensoft.e.payment.api.alarm.base.linux;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

public class IOAlarm extends Alarm{


private static final Logger log = LoggerFactory.getLogger(IOAlarm.class);


	private int alarm_io = 0;

	@Override
	public void initSetup() {
		alarm_io = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_IO);
		}

	@Override
	public boolean haveAlarm() {
		try {
			float value = getIO();
			if (value > alarm_io) {
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
					sb.append("IO out max num:").append(alarm_io).append(MailUtil.NEW_LINE);
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

	private float getIO() {
			float ioUsage = 0.0f;
			Process pro = null;
			Runtime r = Runtime.getRuntime();
			try {
				String command = "iostat -d -x";
				pro = r.exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
				String line = null;
				int count = 0;
				while ((line = in.readLine()) != null) {
					if (++count >= 4) {
						// log.info(line);
						String[] temp = line.split("\\s+");
						if (temp.length > 1) {
							float util = Float.parseFloat(temp[temp.length - 1]);
							ioUsage = (ioUsage > util) ? ioUsage : util;
						}
					}
				}
				if (ioUsage > 0) {
					ioUsage /= 100;
				}
				in.close();
				pro.destroy();
			} catch (Exception e) {
				log.error("", e);
			}
	
			return ioUsage;
	}

}
