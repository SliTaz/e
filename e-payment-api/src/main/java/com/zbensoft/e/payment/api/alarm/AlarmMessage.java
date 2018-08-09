package com.zbensoft.e.payment.api.alarm;

public class AlarmMessage {
	private String key;
	private String name;
	private String level;
	private int alarmTimeSec;
	private int alarmFrequencyTimeSec;
	private String content;
	private boolean isAlarm = false;
	private String alarmEmailManageId;
	
	public String getAlarmEmailManageId() {
		return alarmEmailManageId;
	}
	public void setAlarmEmailManageId(String alarmEmailManageId) {
		this.alarmEmailManageId = alarmEmailManageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isAlarm() {
		return isAlarm;
	}
	public void setAlarm(boolean isAlarm) {
		this.isAlarm = isAlarm;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getAlarmTimeSec() {
		return alarmTimeSec;
	}
	public void setAlarmTimeSec(int alarmTimeSec) {
		this.alarmTimeSec = alarmTimeSec;
	}
	public int getAlarmFrequencyTimeSec() {
		return alarmFrequencyTimeSec;
	}
	public void setAlarmFrequencyTimeSec(int alarmFrequencyTimeSec) {
		this.alarmFrequencyTimeSec = alarmFrequencyTimeSec;
	}
	
}
