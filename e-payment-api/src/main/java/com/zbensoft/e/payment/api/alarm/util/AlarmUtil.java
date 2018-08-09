package com.zbensoft.e.payment.api.alarm.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.zbensoft.e.payment.common.util.IPUtil;

public class AlarmUtil {

	/** 当前系统时间 ,精确到秒 **/
	public static String TodayTimeString() {
		String timePattren = "yyyy-MM-dd HH:mm:ss";// 精确到秒
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattren);
		return simpleDateFormat.format(date);
	}

	public static String getIPInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("ip:").append(MailUtil.NEW_LINE);;
		List<String> ipList = IPUtil.getLocalIPList();
		if (ipList != null && ipList.size() > 0) {
			for (String ip : ipList) {
				sb.append(ip).append(MailUtil.NEW_LINE);
			}
		}
		sb.append(MailUtil.NEW_LINE);
		return sb.toString();
	}
}
