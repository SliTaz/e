package com.zbensoft.e.payment.api.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.PayGatewayService;
import com.zbensoft.e.payment.db.domain.PayGateway;

/**
 * 
 * @author xieqiang
 *
 */
public class PayGatewayFactory {
	private static Logger log = Logger.getLogger(PayGatewayFactory.class);
	private PayGatewayService payGatewayService = SpringBeanUtil.getBean(PayGatewayService.class);

	private List<PayGateway> list = new ArrayList<>();
	private Map<String, PayGateway> map = new HashMap<>();
	private Map<String, PayGateway> mapById = new HashMap<>();

	private static PayGatewayFactory instance = new PayGatewayFactory();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private PayGateway payGateway = new PayGateway();

	public static PayGatewayFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new PayGatewayFactory();
				}
			}
		}
		return instance;
	}

	public void loadConfig() {
		synchronized (objectLock) {
			try {
				PageHelper.startPage(1, 100000);
				list = payGatewayService.selectPage(payGateway);
				map.clear();
				mapById.clear();
				if (list != null && list.size() > 0) {
					for (PayGateway payGateway : list) {
						if (MessageDef.PAY_GATEWAY_STATUS.ENABLE_INT == payGateway.getStatus()) {
							// BANKID+GETWAYTYPEID
							String bankId = payGateway.getBankId();
							if (bankId == null) {
								bankId = "";
							}
							String key = bankId + "_" + payGateway.getPayGatewayTypeId();
							map.put(key, payGateway);
							mapById.put(payGateway.getPayGatewayId(), payGateway);
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	public List<PayGateway> get() {
		synchronized (objectLock) {
			return list;
		}
	}

	public PayGateway getByKey(String key) {
		synchronized (objectLock) {
			return map.get(key);
		}
	}

	public PayGateway get(String bankId, int payGateWayType) {
		synchronized (objectLock) {
			if (bankId == null) {
				bankId = "";
			}
			String key = bankId + "_" + payGateWayType;
			return map.get(key);
		}

	}

	public PayGateway getById(String payGatewayId) {
		synchronized (objectLock) {
			return mapById.get(payGatewayId);
		}
	}

}
