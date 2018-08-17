package org.e.payment.core.pay.calcPrice;

import com.zbensoft.e.payment.db.domain.TradeInfo;

public interface PayCalcPriceInt {

	public void calcPricePay(TradeInfo tradeInfo);

	public void calcPriceRecv(TradeInfo tradeInfo);

}
