package com.zbensoft.e.payment.api.common;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;

import com.zbensoft.e.payment.api.service.api.ApplicationServerService;
import com.zbensoft.e.payment.api.service.api.SystemConfigService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.IPUtil;
import com.zbensoft.e.payment.db.domain.ApplicationServer;
import com.zbensoft.e.payment.db.domain.SystemConfig;

public class SystemConfigFactory {
	private static Logger log = Logger.getLogger(SystemConfigFactory.class);
	private SystemConfigService systemConfigService = SpringBeanUtil.getBean(SystemConfigService.class);
	private ApplicationServerService applicationServerService = SpringBeanUtil.getBean(ApplicationServerService.class);

	private Map<String, String> systemConfigMapTmp = null; // 系统配置map
	private Map<String, String> systemConfigMap = new HashMap<String, String>(); // 系统配置map
	private Environment env = SpringBeanUtil.getBean(Environment.class);
	private String IP_STARTS_WITH = env.getProperty("system.config.ip.startsWith");
	private String SERVER_CODE = env.getProperty("server.code");

	private static SystemConfigFactory instance = new SystemConfigFactory();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();

	public static SystemConfigFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new SystemConfigFactory();
				}
			}
		}
		return instance;
	}

	public void loadConfig() {
		synchronized (objectLock) {
			try {
				systemConfigMapTmp = new HashMap<String, String>();
				String ip = IPUtil.getLocalIP(IP_STARTS_WITH);
				if (StringUtils.isNotEmpty(ip)) {
					String applicationServerCode = IPUtil.convertIpToLongString(ip);
					ApplicationServer applicationServer = applicationServerService.selectByPrimaryKey(applicationServerCode);
					if (applicationServer == null) {
						applicationServer = new ApplicationServer();
						applicationServer.setApplicationServerCode(applicationServerCode);
						applicationServer.setIpAddr(ip);
						applicationServer.setName(ip);
						applicationServer.setLastTime(Calendar.getInstance().getTime());
						applicationServerService.insert(applicationServer);
					} else {
						applicationServer = new ApplicationServer();
						applicationServer.setApplicationServerCode(applicationServerCode);
						applicationServer.setIpAddr(ip);
						applicationServer.setName(ip);
						applicationServer.setLastTime(Calendar.getInstance().getTime());
						applicationServerService.updateByPrimaryKey(applicationServer);
					}
					loadConfigByServerCode("-1");
					loadConfigByServerCode(applicationServerCode);
					
				} else {
					log.info("loadConfig getLocalIP is empty");
				}
				systemConfigMapTmp.put(SystemConfigKey.COMMON_SERVER_CODE.getKey(), SERVER_CODE);
				systemConfigMap = systemConfigMapTmp;
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private void loadConfigByServerCode(String applicationServerCode) {
		List<SystemConfig> systemConfigList = systemConfigService.selectByApplicationServerCode(applicationServerCode);
		if (systemConfigList != null && systemConfigList.size() > 0) {
			systemConfigMapTmp.clear();
			for (SystemConfig systemConfig : systemConfigList) {
				systemConfigMapTmp.put(systemConfig.getCode(), systemConfig.getValue());
			}
			systemConfigMapTmp.put(SystemConfigKey.COMMON_SERVER_CODE.getKey(), SERVER_CODE);
		}
	}

	public int getSystemConfigInt(SystemConfigKey systemConfigKey) {
		if (isEmptySystemConfig()) {
			loadConfig();
		}
		return getSystemConfigInt(systemConfigKey.getKey(), systemConfigKey.getDefaultInt());
	}

	public String getSystemConfigStr(SystemConfigKey systemConfigKey) {
		if (isEmptySystemConfig()) {
			loadConfig();
		}
		return getSystemConfigStr(systemConfigKey.getKey(), systemConfigKey.getDefaultStr());
	}

	private int getSystemConfigInt(String key, int defaultInt) {
		String o = systemConfigMap.get(key);
		return o == null ? defaultInt : Integer.valueOf(o);
	}

	private String getSystemConfigStr(String key, String defaultStr) {
		String o = systemConfigMap.get(key);
		return o == null ? defaultStr : o;
	}

	private boolean isEmptySystemConfig() {
		return systemConfigMap.keySet().size() <= 1;
	}

}
