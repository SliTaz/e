package org.e.payment.core.pay.fraud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.FraultRuleService;
import com.zbensoft.e.payment.db.domain.FraultRule;

public class FraudFactory {

	private static final Logger log = LoggerFactory.getLogger(FraudFactory.class);

	private static FraudFactory instance = null;

	private static List<FraultRule> list = new ArrayList<>();

	private static Object oblock = new Object();

	private FraultRuleService fraultRuleService = SpringBeanUtil.getBean(FraultRuleService.class);
	private static boolean isLoad = false;

	private FraudFactory() {

	}

	public static FraudFactory getInstance() {
		if (instance == null) {
			instance = new FraudFactory();
		}
		return instance;
	}

	public void loadConfig() {
		synchronized (oblock) {
			list.clear();
			FraultRule fraultRule = new FraultRule();
			fraultRule.setStatus(MessageDef.STATUS.ENABLE_INT);
			List<FraultRule> listTmp = fraultRuleService.selectPage(fraultRule);
			if (listTmp != null && listTmp.size() > 0) {
				list.addAll(listTmp);
			}
			isLoad = true;
		}
	}

	public FraudResult process(Map<String, String> param) {
		synchronized (oblock) {
			if (!isLoad) {
				loadConfig();
			}
			FraudResult fraudResult = FraudResult.SUCC;
			if (list != null && list.size() > 0) {
				for (FraultRule fraultRule : list) {
					try {
						FraudProcess fraudProcess = (FraudProcess) Class.forName(fraultRule.getHandleClass()).newInstance();
						if (fraudProcess != null) {
							FraudResult fraudResultTmp = fraudProcess.process(param);
							if (fraudResultTmp == FraudResult.ERROR) {
								return fraudResultTmp;
							}
							if (fraudResultTmp.getCode() > fraudResult.getCode()) {
								fraudResult = fraudResultTmp;
							}
						}
					} catch (Exception e) {
						log.error("", e);
					}
				}
			}
			return fraudResult;
		}
	}

}
