package com.zbensoft.e.payment.api.alarm.type.log;

import com.zbensoft.e.payment.api.alarm.AlarmMessage;
import com.zbensoft.e.payment.api.alarm.type.AlarmTypeProcess;
import com.zbensoft.e.payment.api.log.ALARM_LOG;

public class LogProcess implements AlarmTypeProcess {

	@Override
	public void process(AlarmMessage message) {
		ALARM_LOG.INFO("key=" + message.getKey() + ",name=" + message.getName() + ",level=" + message.getLevel() + ",content=" + message.getContent());
	}

}
