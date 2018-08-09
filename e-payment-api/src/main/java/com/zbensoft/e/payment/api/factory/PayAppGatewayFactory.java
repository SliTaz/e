package com.zbensoft.e.payment.api.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.PayAppGatewayService;
import com.zbensoft.e.payment.db.domain.PayAppGateway;

/**
 * 
 * @author xieqiang
 *
 */
public class PayAppGatewayFactory {
	private static Logger log = Logger.getLogger(PayAppGatewayFactory.class);
	private PayAppGatewayService payAppGatewayService = SpringBeanUtil.getBean(PayAppGatewayService.class);

	private List<PayAppGateway> list = new ArrayList<>();
	private Map<String, PayAppGateway> map = new HashMap<>();
	private Map<String, List<PayAppGateway>> mapByAppId = new HashMap<>();

	private static PayAppGatewayFactory instance = new PayAppGatewayFactory();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private PayAppGateway payAppGateway = new PayAppGateway();

	public static PayAppGatewayFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new PayAppGatewayFactory();
				}
			}
		}
		return instance;
	}

	public void loadConfig() {
		synchronized (objectLock) {
			try {
				PageHelper.startPage(1, 100000);
				list = payAppGatewayService.selectPage(payAppGateway);
				map.clear();
				mapByAppId.clear();
				if (list != null && list.size() > 0) {
					for (PayAppGateway payAppGateway : list) {
						String key = payAppGateway.getPayAppId() + "_" + payAppGateway.getPayGatewayId();
						map.put(key, payAppGateway);
						
						List<PayAppGateway> payAppGatewayList = mapByAppId.get(payAppGateway.getPayAppId());
						if(payAppGatewayList ==null){
							payAppGatewayList = new ArrayList<>();
							mapByAppId.put(payAppGateway.getPayAppId(), payAppGatewayList);
						}
						payAppGatewayList.add(payAppGateway);
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	public List<PayAppGateway> get() {
		synchronized (objectLock) {
			return list;
		}
	}

	public PayAppGateway getByKey(String key) {
		synchronized (objectLock) {
			return map.get(key);
		}

	}

	public List<PayAppGateway> getByAppId(String key) {
		synchronized (objectLock) {
			return mapByAppId.get(key);
		}

	}
	public PayAppGateway get(String payAppId, String payGatewayId) {
		synchronized (objectLock) {
			String key = payAppId + "_" + payGatewayId;
			return map.get(key);
		}

	}

}
