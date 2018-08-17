package org.e.payment.core.pay.bankProcess;

import com.zbensoft.e.payment.db.domain.TradeInfo;

public interface BankProcess {

	public boolean process(TradeInfo tradeInfo);
}
