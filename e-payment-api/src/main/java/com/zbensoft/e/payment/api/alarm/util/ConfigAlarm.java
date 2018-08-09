package com.zbensoft.e.payment.api.alarm.util;

public interface ConfigAlarm {

	/** 告警线程多久处理一次 **/
	public static final String ALARM_THREAD_TIME_SEC = "alarm_thread_time_sec";//秒
	/** 告警邮件是否发生：1：发送 **/
	public static final String ALARM_EMAIL_FLAG = "alarm_email_enable";
	/** 告警邮件发送到那些邮箱 **/
	public static final String ALARM_EMAIL = "alarm_email";
	/** 告警邮件用户名 **/
	public static final String ALARM_SEND_EMAIL_USER_NAME = "alarm_send_email_user_name";
	/** 告警邮件用户密码 **/
	public static final String ALARM_SEND_EMAIL_USER_PASSWORD = "alarm_send_email_user_password";
	/** 告警邮件smtp **/
	public static final String ALARM_SEND_EMAIL_USER_SMTP_HOST = "alarm_send_email_user_smtp_host";
	/** 内存对话超过多少告警 **/
	public static final String ALARM_DIALOG_NUM = "alarm_dialog_num";
	/** cpu多高告警 **/
	public static final String ALARM_CPU = "alarm_cpu";
	/** mem多高告警 **/
	public static final String ALARM_MEM = "alarm_mem";
	/** IO多高告警 **/
	public static final String ALARM_IO = "alarm_io";
	/** NET多高告警 **/
	public static final String ALARM_NET = "alarm_net";
	/** NET告警-网口带宽,Mbps **/
	public static final String ALARM_NET_BAND_WIDTH = "alarm_net_Band_width";
	/** DISK多高告警 **/
	public static final String ALARM_DISK = "alarm_disk";
	/** 告警日志 **/
	public static final String ALARM_LOG = "alarm_log";
	/** 告警授权剩余天数 **/
	public static final String ALARM_LINCENSE_DAY = "alarm_lincense_day";

}
