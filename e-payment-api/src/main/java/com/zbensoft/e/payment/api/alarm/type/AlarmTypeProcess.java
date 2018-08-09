package com.zbensoft.e.payment.api.alarm.type;

import com.zbensoft.e.payment.api.alarm.AlarmMessage;

public interface AlarmTypeProcess {
	public void process(AlarmMessage message);
}
