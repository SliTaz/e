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
import com.zbensoft.e.payment.api.alarm.thread.AlarmThread;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.AlarmMangerService;
import com.zbensoft.e.payment.db.domain.AlarmManger;

/**
 * 告警主入口类
 * 
 * @author xieqiang
 *
 */
public class AlarmMangerFactory {

	private static final Logger log = LoggerFactory.getLogger(AlarmMangerFactory.class);

	private static AlarmMangerFactory instance = null;

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	/** 注册map **/
	private Map<String, AlarmInterface> registerMap = new HashMap<String, AlarmInterface>();
	/** 所有class，key对应class **/
	private Map<String, String> classMap = new HashMap<String, String>();
	/** 所有class，class对应key **/
	private Map<String, String> classNameKeyMap = new HashMap<String, String>();

	AlarmMangerService alarmMangerService = SpringBeanUtil.getBean(AlarmMangerService.class);

	public static AlarmMangerFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new AlarmMangerFactory();
					AlarmThread t = new AlarmThread("AlarmThread");
					t.start();
				}
			}
		}
		return instance;
	}

	private void register(AlarmManger alarmManager) {
		synchronized (objectLock) {
			try {
				String key = alarmManager.getAlarmKey();
				String alarmClass = alarmManager.getHandleClass();
				String className = classMap.get(key);
				if (className == null || !className.equals(alarmClass)) {//
					AlarmInterface alarm = (AlarmInterface) Class.forName(alarmClass).newInstance();
					registerMap.put(key, alarm);
					classMap.put(key, alarmClass);
				}
				classNameKeyMap.put(alarmClass, key);
				AlarmInterface alarm = registerMap.get(key);
				alarm.init(alarmManager);
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	/**
	 * 告警主入口
	 */
	@SuppressWarnings("unchecked")
	public void loadConfig() {
		synchronized (objectLock) {
			// classMap.clear();
			// 类名对应key清除
			classNameKeyMap.clear();

			PageHelper.startPage(1, 100000);
			List<AlarmManger> managerList = alarmMangerService.selectAll();
			if (managerList != null && managerList.size() > 0) {
				for (AlarmManger alarmManger : managerList) {
					register(alarmManger);
				}
			}

			// 将已经删除的告警删除
			Set<String> set = registerMap.keySet();
			if (set != null && set.size() > 0) {
				List<String> keyList = new ArrayList<String>();
				for (Iterator<String> keys = set.iterator(); keys.hasNext();) {
					String key = (String) keys.next();
					boolean flag = true;
					for (AlarmManger alarmManger : managerList) {
						if (alarmManger.getAlarmKey().equals(key)) {
							flag = false;
							break;
						}
					}
					if (flag) {
						keyList.add(key);
					}
				}
				for (String key : keyList) {
					registerMap.remove(key);
					classMap.remove(key);
				}
			}
		}
		AlarmLevelManger.getInstance().loadConfig();
		AlarmEmailMangerFacory.getInstance().loadConfig();
	}

	public void handle() {
		synchronized (objectLock) {
			Set<String> set = registerMap.keySet();
			if (set != null && set.size() > 0) {
				for (Iterator<String> keys = set.iterator(); keys.hasNext();) {
					String key = (String) keys.next();
					if (key != null && key.length() > 0) {
						try {
							AlarmInterface alarm = registerMap.get(key);// 更具注册信息获得告警列表
							if (alarm.haveAlarm()) {
								AlarmMessage message = alarm.getAlarmMessage();
								AlarmLevelManger.getInstance().handle(message);
							}
						} catch (Exception e) {
							log.error("", e);
						}
					}
				}
			}
		}
	}

	public void setParam(String key, Object ob) {
		synchronized (objectLock) {
			String code = classNameKeyMap.get(key);
			if (code != null) {
				AlarmInterface alarm = registerMap.get(code);
				if (alarm == null) {
					return;
				} else {
					alarm.setParam(ob);
				}
			}
		}
	}

	public Object getParam(String key) {
		synchronized (objectLock) {
			String code = classNameKeyMap.get(key);
			if (code != null) {
				AlarmInterface alarm = registerMap.get(code);
				if (alarm == null) {
					return null;
				} else {
					return alarm.getParam();
				}
			}
			return null;
		}
	}
}
