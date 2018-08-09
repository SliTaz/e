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

public class DiskAlarm extends Alarm {
	
	private static final Logger log = LoggerFactory.getLogger(DiskAlarm.class);


	private int alarm_disk = 0;

	@Override
	public void initSetup() {
		alarm_disk = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_DISK);
	}

	@Override
	public boolean haveAlarm() {
		try {
			float value = getDisk();
			if (value > alarm_disk) {
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
					sb.append("Disk out max num:").append(alarm_disk).append(MailUtil.NEW_LINE);
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

	private float getDisk() {
		float totalHD = 0;
		float usedHD = 0;
		Runtime rt = Runtime.getRuntime();

		BufferedReader in = null;
		try {
			Process p = rt.exec("df -hl");// df -hl 查看硬盘空间
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String str = null;
			String[] strArray = null;
			while ((str = in.readLine()) != null) {
				int m = -1;
				strArray = str.trim().split(" ");
				for (String tmp : strArray) {
					if (tmp.trim().length() == 0)
						continue;
					++m;
					if (tmp.indexOf("G") != -1) {
						if (m == 1) {
							if (!tmp.equals("") && !tmp.equals("0"))
								totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;

						}
						if (m == 2) {
							if (!tmp.equals("none") && !tmp.equals("0"))
								usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;

						}
					}
					if (tmp.indexOf("M") != -1) {
						if (m == 1) {
							if (!tmp.equals("") && !tmp.equals("0"))
								totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));

						}
						if (m == 2) {
							if (!tmp.equals("none") && !tmp.equals("0"))
								usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));
						}
					}

				}

				// }
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				log.error("", e);
			}
		}
		if (totalHD == 0) {
			return 0;
		}
		float disUsage = (usedHD / totalHD) * 100;
		return disUsage;
	}

}
