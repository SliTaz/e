package com.zbensoft.e.payment.api.alarm;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.AlarmEmailManageService;
import com.zbensoft.e.payment.db.domain.AlarmEmailManage;

public class AlarmEmailMangerFacory {

	private static final Logger log = LoggerFactory.getLogger(AlarmEmailMangerFacory.class);

	private static AlarmEmailMangerFacory instance = null;

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private ConcurrentMap<String, AlarmEmailManage> map = new ConcurrentHashMap<>();
	private AlarmEmailManageService alarmEmailManageService = SpringBeanUtil.getBean(AlarmEmailManageService.class);

	public static AlarmEmailMangerFacory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new AlarmEmailMangerFacory();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化，告警等级入口
	 */
	@SuppressWarnings("unchecked")
	public void loadConfig() {
		synchronized (objectLock) {
			map.clear();
			PageHelper.startPage(1, 100000);
			// 从数据库中取对应的配置
			List<AlarmEmailManage> alarmLevelList = alarmEmailManageService.selectAll();

			if (alarmLevelList != null && alarmLevelList.size() > 0) {
				for (AlarmEmailManage alarmEmailManage : alarmLevelList) {
					map.put(alarmEmailManage.getAlarmEmailManageId(), alarmEmailManage);
				}
			}
		}
	}

	public AlarmEmailManage get(String key) {
		synchronized (objectLock) {
			if (key == null || key.length() == 0) {
				return map.get("-1");
			}
			return map.get(key);
		}
	}

	public AlarmEmailManage get() {
		synchronized (objectLock) {
			return map.get("-1");
		}
	}

}
