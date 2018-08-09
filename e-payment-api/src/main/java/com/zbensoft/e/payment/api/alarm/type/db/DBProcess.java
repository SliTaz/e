package com.zbensoft.e.payment.api.alarm.type.db;

import java.util.Calendar;

import com.zbensoft.e.payment.api.alarm.AlarmMessage;
import com.zbensoft.e.payment.api.alarm.type.AlarmTypeProcess;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.AlarmInfoService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.AlarmInfo;

public class DBProcess implements AlarmTypeProcess {
	private static AlarmInfoService alarmInfoService = (AlarmInfoService) SpringBeanUtil.getBean(AlarmInfoService.class);

	@Override
	public void process(AlarmMessage message) {
		AlarmInfo alarmInfo = new AlarmInfo();
		alarmInfo.setAlarmTime(DateUtil.convertDateToFormatString(Calendar.getInstance().getTime()));
		alarmInfo.setAlarmInfoCode(IDGenerate.generateCommOne(IDGenerate.ALARM_INFO));
		alarmInfo.setAlarmLevelCode(message.getLevel());
		alarmInfo.setContent(message.getContent());
		alarmInfo.setAlarmOrigin(message.getKey());
		alarmInfo.setStatus(MessageDef.ALARM_STATUS.UN_HANDLING);
		alarmInfoService.insert(alarmInfo);
	}

}
