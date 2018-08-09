package com.zbensoft.e.payment.api.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.PayAppService;
import com.zbensoft.e.payment.db.domain.PayApp;

/**
 * 
 * @author xieqiang
 *
 */
public class PayAppFactory {
	private static Logger log = Logger.getLogger(PayAppFactory.class);
	private PayAppService payAppService = SpringBeanUtil.getBean(PayAppService.class);

	private List<PayApp> list = new ArrayList<>();
	private Map<String, PayApp> map = new HashMap<>();

	private static PayAppFactory instance = new PayAppFactory();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private PayApp payApp = new PayApp();

	public static PayAppFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new PayAppFactory();
				}
			}
		}
		return instance;
	}

	public void loadConfig() {
		synchronized (objectLock) {
			try {
				PageHelper.startPage(1, 100000);
				list = payAppService.selectPage(payApp);
				map.clear();
				if (list != null && list.size() > 0) {
					for (PayApp payApp : list) {
						if (MessageDef.PAY_APP_STATUS.ENABLE_INT == payApp.getStatus()) {
							map.put(payApp.getPayAppId(), payApp);
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	public List<PayApp> get() {
		synchronized (objectLock) {
			return list;
		}
	}

	public PayApp getById(String id) {
		synchronized (objectLock) {
			return map.get(id);
		}

	}

}
