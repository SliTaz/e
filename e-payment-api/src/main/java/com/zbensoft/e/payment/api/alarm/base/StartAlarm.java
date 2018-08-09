package com.zbensoft.e.payment.api.alarm.base;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;

public class StartAlarm extends Alarm {
	private static boolean isAlarm = true;

	@Override
	public void initSetup() {

	}

	@Override
	public boolean haveAlarm() {

		if (isAlarm) {
			isAlarm = false;
			message.setAlarm(true);
			StringBuffer sb = new StringBuffer();
			sb.append(AlarmUtil.getIPInfo());
			sb.append("Start Server now,The Server Path is ").append(System.getProperty("user.dir")).append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
			sb.append("IF It is a unknow start.Please check the server!").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
			message.setContent(sb.toString());
			return true;
		} else {
			message.setAlarm(false);
			message.setContent(null);
			return false;
		}

	}
}
