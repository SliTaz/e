package com.zbensoft.e.payment.api.alarm.alarm;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

public class CountRequestAlarm extends Alarm {
	
	
	private static final Logger log = LoggerFactory.getLogger(CountRequestAlarm.class);

	

	@Override
	public void initSetup() {
	}

	@Override
	public boolean haveAlarm() {
		int ALARM_MAIN = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_MAIN);
		if (ALARM_MAIN != 1) {
			return false;
		}
		try {

			boolean isExperd = false;

			int ALARM_REQUEST_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_REQUEST_COUNT);
			int ALARM_REQUEST_COUNT_SUCC = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_REQUEST_COUNT_SUCC);
			Object countCOUNT_QUEST = null;
			Object countCOUNT_QUEST_SUCC = null;
			Object param = getParam();
			if (param != null) {
				countCOUNT_QUEST = ((Map<String, String>) param).get(RedisDef.COUNT.COUNT_QUEST);
				if (countCOUNT_QUEST != null) {
					isExperd = true;
				}

				countCOUNT_QUEST_SUCC = ((Map<String, String>) param).get(RedisDef.COUNT.COUNT_QUEST_SUCC);
				if (countCOUNT_QUEST_SUCC != null) {
					isExperd = true;
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
					sb.append("request count:").append(countCOUNT_QUEST).append("  (redis)").append(MailUtil.NEW_LINE);
					sb.append("request count:").append(ALARM_REQUEST_COUNT).append("  (alarm)").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
					sb.append("request count succ:").append(countCOUNT_QUEST_SUCC).append("  (redis)").append(MailUtil.NEW_LINE);
					sb.append("request count succ:").append(ALARM_REQUEST_COUNT_SUCC).append("  (alarm)").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
					sb.append("Please check the license,If not,the server will not work!").append(MailUtil.NEW_LINE);
					message.setContent(sb.toString());

					setParam(null);
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
