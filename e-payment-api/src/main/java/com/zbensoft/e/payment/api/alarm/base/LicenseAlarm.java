package com.zbensoft.e.payment.api.alarm.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.license.read.ReadImpl;

public class LicenseAlarm extends Alarm {

	private static final Logger log = LoggerFactory.getLogger(LicenseAlarm.class);

	int ALARM_USER_QUANTITY = 0;

	@Override
	public void initSetup() {

		ALARM_USER_QUANTITY = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_LICENSE_USER_QUANTITY);
	}

	@Override
	public boolean haveAlarm() {
		int ALARM_MAIN = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_MAIN);
		if (ALARM_MAIN != 1) {
			return false;
		}
		try {
			boolean isExperd = false;

			String endUserQuantity = "";
			Integer countInt = RedisUtil.get_COUNT_BUYER();
			if (countInt != null) {
				if (countInt >= ALARM_USER_QUANTITY) {
					isExperd = true;

					ReadImpl read = new ReadImpl();
					endUserQuantity = read.get("endUserQuantity");

				}
			}

			if (isExperd) {
				int alarmtTimeSec = 0;
				if (isFirstCount) {
					alarmtTimeSec = message.getAlarmTimeSec();
					if (startTime == 0) {
						startTime = System.currentTimeMillis();
					}
				} else {
					alarmtTimeSec = message.getAlarmFrequencyTimeSec();
				}
				if (System.currentTimeMillis() - startTime >= alarmtTimeSec * 1000) {
					startTime = System.currentTimeMillis();
					isFirstCount = false;
					message.setAlarm(true);
					StringBuffer sb = new StringBuffer();
					sb.append(AlarmUtil.getIPInfo());
					sb.append("BuyerCount:").append(countInt).append("  (DB)").append(MailUtil.NEW_LINE);
					sb.append("BuyerCount:").append(endUserQuantity).append("  (License)").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);

					sb.append("Please check the license,If not,the server will not work!").append(MailUtil.NEW_LINE);
					message.setContent(sb.toString());
					return true;
				}
			} else {
				message.setAlarm(false);
				message.setContent(null);
				return false;
			}

		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}
}
