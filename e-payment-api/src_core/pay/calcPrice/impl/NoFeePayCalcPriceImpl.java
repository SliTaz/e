package org.e.payment.core.pay.calcPrice.impl;

import org.e.payment.core.pay.calcPrice.PayCalcPriceInt;

import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class NoFeePayCalcPriceImpl implements PayCalcPriceInt {

	@Override
	public void calcPricePay(TradeInfo tradeInfo) {
		tradeInfo.setPayFee(0d);
		tradeInfo.setPaySumAmount(DoubleUtil.add(tradeInfo.getPayAmount(), tradeInfo.getPayFee()));
	}

	@Override
	public void calcPriceRecv(TradeInfo tradeInfo) {
		tradeInfo.setRecvFee(0d);
		tradeInfo.setRecvSumAmount(DoubleUtil.add(tradeInfo.getRecvAmount(), DoubleUtil.mul(-1d, tradeInfo.getRecvFee())));
	}

}
