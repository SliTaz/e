package com.zbensoft.e.payment.api.alarm;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.zbensoft.e.payment.db.domain.AlarmManger;

public abstract class Alarm implements AlarmInterface {
	protected AlarmMessage message = new AlarmMessage();

	protected boolean isFirstCount = true;
	protected long startTime = 0;
	private Object parm;

	public AlarmMessage getAlarmMessage() {
		return message;
	}

	public void setParam(Object ob) {
		parm = ob;
	}

	public Object getParam() {
		return parm;
	}

	public void init(AlarmManger alarmManager) {
		message.setKey(alarmManager.getAlarmKey());
		message.setName(alarmManager.getName());
		message.setLevel(alarmManager.getAlarmLevelCode());
		message.setAlarmTimeSec(Integer.valueOf(alarmManager.getFirstTimeSec()));// 10分钟
		message.setAlarmFrequencyTimeSec(Integer.valueOf(alarmManager.getFrequencyTimeSec()));
		message.setAlarmEmailManageId(alarmManager.getAlarmEmailManageId());
		initSetup();
	}
}
