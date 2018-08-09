package com.zbensoft.e.payment.api.alarm.alarm.factory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息告警管理
 * 
 * @author xieqiang
 * 
 */
public class MessageAlarmFactory {
	private static Logger log = LoggerFactory.getLogger(MessageAlarmFactory.class);
	private static MessageAlarmFactory instance = null;
	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private List<String> list = new ArrayList<>();

	public static MessageAlarmFactory getInstance() {
		if (instance == null) {
			synchronized (uniqueLock) {
				instance = new MessageAlarmFactory();
			}
		}
		return instance;
	}

	public void add(String message) {
		synchronized (objectLock) {
			if (message != null && message.length() > 0) {
				list.add(message);
			}
		}
	}

	public String get() {
		synchronized (objectLock) {
			if (list != null && list.size() > 0) {
				return list.remove(0);
			}
			return null;
		}
	}
}
