package org.e.payment.core.pay.fraud.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.fraud.FraudProcess;
import org.e.payment.core.pay.fraud.FraudResult;

import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.FraultIdNumberService;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.FraultIdNumber;

public class BlockIdNumberFraudProcessImpl implements FraudProcess {

	private FraultIdNumberService fraultIdNumberService = SpringBeanUtil.getBean(FraultIdNumberService.class);
	private ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);

	@Override
	public FraudResult process(Map<String, String> param) {
		if (param == null) {
			return FraudResult.SUCC;
		}

		String user_id_pay = param.get("user_id_pay");
		if (StringUtils.isNotEmpty(user_id_pay)) {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(user_id_pay);
			if (consumerUserClap != null) {
				FraultIdNumber fraultIdNumber = fraultIdNumberService.selectByPrimaryKey(consumerUserClap.getIdNumber());
				if (fraultIdNumber != null) {
					return FraudResult.ERROR;
				}
			}
		}
		String user_id_recv = param.get("user_id_recv");
		if (StringUtils.isNotEmpty(user_id_recv)) {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(user_id_recv);
			if (consumerUserClap != null) {
				FraultIdNumber fraultIdNumber = fraultIdNumberService.selectByPrimaryKey(consumerUserClap.getIdNumber());
				if (fraultIdNumber != null) {
					return FraudResult.ERROR;
				}
			}
		}
		return FraudResult.SUCC;
	}

}
