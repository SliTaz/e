package com.zbensoft.e.payment.api.alarm.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;

public class MessageAlarm extends Alarm {

	private static final Logger log = LoggerFactory.getLogger(MessageAlarm.class);

	@Override
	public void initSetup() {
	}

	@Override
	public boolean haveAlarm() {
		try {
			String messageStr = MessageAlarmFactory.getInstance().get();
			if (messageStr != null) {
				message.setAlarm(true);
				message.setContent(AlarmUtil.getIPInfo() + messageStr);
				return true;
			}
		} catch (Exception e) {
			log.error("", e);
		}
		message.setAlarm(false);
		message.setContent(null);
		return false;
	}

}
