package com.zbensoft.e.payment.api.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.alarm.type.AlarmTypeProcess;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.AlarmLevelService;
import com.zbensoft.e.payment.api.service.api.AlarmLevelTypeService;
import com.zbensoft.e.payment.api.service.api.AlarmTypeService;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.domain.AlarmLevelType;
import com.zbensoft.e.payment.db.domain.AlarmType;

public class AlarmLevelManger {

	private static final Logger log = LoggerFactory.getLogger(AlarmLevelManger.class);

	private static AlarmLevelManger instance = null;

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private Map<String, List<String>> levelMap = new HashMap<String, List<String>>();// 级别和告警类型对应map
	private Map<String, AlarmTypeProcess> alarmTypeMap = new HashMap<String, AlarmTypeProcess>();// 告警类型对应class
	private Map<String, String> alarmTypeKeyMap = new HashMap<String, String>();// 告警类型对应className
	private AlarmTypeService alarmTypeService = SpringBeanUtil.getBean(AlarmTypeService.class);
	private AlarmLevelService alarmLevelService = SpringBeanUtil.getBean(AlarmLevelService.class);
	private AlarmLevelTypeService alarmLevelTypeService = SpringBeanUtil.getBean(AlarmLevelTypeService.class);

	public static AlarmLevelManger getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new AlarmLevelManger();
				}
			}
		}
		return instance;
	}

	/**
	 * 将告警等级对应的告警方式放入map，并实例化
	 * 
	 * @param alarmlevel
	 * @param alarmType
	 */
	private void addLevel(AlarmLevel alarmlevel, AlarmType alarmType) {
		synchronized (objectLock) {
			try {
				// 将告警等级对应的告警类型放入map
				String levelCode = alarmlevel.getAlarmLevelCode();
				String alarmTypeStr = alarmType.getAlarmTypeCode();
				List<String> list = levelMap.get(levelCode);
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(alarmTypeStr);
				levelMap.put(levelCode, list);

				// 讲告警类型实例化放入map
				String alarmTypeClass = alarmType.getHandleClass();
				String className = alarmTypeKeyMap.get(alarmTypeStr);
				if (className == null || !className.equals(alarmTypeClass)) {// 没有或不等
					AlarmTypeProcess alarmTypeProcess = (AlarmTypeProcess) Class.forName(alarmTypeClass).newInstance();
					alarmTypeMap.put(alarmTypeStr, alarmTypeProcess);
					alarmTypeKeyMap.put(alarmTypeStr, alarmTypeClass);
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	/**
	 * 初始化，告警等级入口
	 */
	@SuppressWarnings("unchecked")
	public void loadConfig() {
		synchronized (objectLock) {
			levelMap.clear();
			// alarmTypeKeyMap.clear();

			PageHelper.startPage(1, 100000);
			// 从数据库中取对应的配置
			List<AlarmLevel> alarmLevelList = alarmLevelService.selectAll();

			PageHelper.startPage(1, 100000);
			// 取出AlarmLevelType配置
			List<AlarmLevelType> alarmLevelTypeList = alarmLevelTypeService.selectAll();

			for (AlarmLevel alarmlevel : alarmLevelList) {
				// 取出告警等级对应的告警类型
				for (AlarmLevelType alarmLevelType2 : alarmLevelTypeList) {
					if (alarmlevel.getAlarmLevelCode().equals(alarmLevelType2.getAlarmLevelCode())) {
						AlarmType alarmType = alarmTypeService.selectByPrimaryKey(alarmLevelType2.getAlarmTypeCode());
						addLevel(alarmlevel, alarmType);
					}
				}

			}

			// 将已经删除的告警方式删除
			List<AlarmType> alarmTypeList = alarmTypeService.selectAll();
			if (alarmTypeList != null && alarmTypeList.size() > 0) {
				Set<String> set = alarmTypeMap.keySet();
				if (set != null && set.size() > 0) {
					List<String> keyList = new ArrayList<String>();
					for (Iterator<String> keys = set.iterator(); keys.hasNext();) {
						String key = (String) keys.next();
						boolean flag = true;
						for (AlarmType alarmType1 : alarmTypeList) {
							if (alarmType1.getAlarmTypeCode().equals(key)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							keyList.add(key);
						}
					}
					for (String key : keyList) {
						alarmTypeMap.remove(key);
						alarmTypeKeyMap.remove(key);
					}
				}
			}
		}
	}

	public void handle(AlarmMessage message) {
		synchronized (objectLock) {
			List<String> list = levelMap.get(message.getLevel());
			if (list != null && list.size() > 0) {
				for (String alarmTypeProcessStr : list) {
					AlarmTypeProcess alarmTypeProcess = alarmTypeMap.get(alarmTypeProcessStr);
					if (alarmTypeProcess != null) {
						alarmTypeProcess.process(message);
					}
				}
			}
		}
	}

}
