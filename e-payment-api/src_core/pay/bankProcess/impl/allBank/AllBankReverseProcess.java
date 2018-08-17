package org.e.payment.core.pay.bankProcess.impl.allBank;

import org.e.payment.core.pay.bankProcess.BankProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.db.domain.TradeInfo;

public class AllBankReverseProcess implements BankProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankReverseProcess.class);

	@Override
	public boolean process(TradeInfo tradeInfo) {

		return true;
	}

}
