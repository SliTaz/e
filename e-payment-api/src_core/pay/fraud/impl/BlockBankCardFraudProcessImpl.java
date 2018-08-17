package org.e.payment.core.pay.fraud.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.fraud.FraudProcess;
import org.e.payment.core.pay.fraud.FraudResult;

import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.FraultBankCardService;
import com.zbensoft.e.payment.db.domain.FraultBankCard;

public class BlockBankCardFraudProcessImpl implements FraudProcess {

	private FraultBankCardService fraultBankCardService = SpringBeanUtil.getBean(FraultBankCardService.class);

	@Override
	public FraudResult process(Map<String, String> param) {
		if (param == null) {
			return FraudResult.SUCC;
		}

		String bank_card_pay = param.get("bank_card_pay");
		if (StringUtils.isNotEmpty(bank_card_pay)) {

			FraultBankCard fraultBankCard = fraultBankCardService.selectByPrimaryKey(bank_card_pay);
			if (fraultBankCard != null) {
				return FraudResult.ERROR;
			}
		}
		String bank_card_recv = param.get("bank_card_recv");
		if (StringUtils.isNotEmpty(bank_card_recv)) {
			FraultBankCard fraultBankCard = fraultBankCardService.selectByPrimaryKey(bank_card_recv);
			if (fraultBankCard != null) {
				return FraudResult.ERROR;
			}
		}

		return FraudResult.SUCC;
	}

}
