package org.e.payment.core.pay.submit.vo;

import com.zbensoft.e.payment.db.domain.TradeInfo;

public class ErrorHandlingRechargeResponse {
	private String tradeSeq;
	private TradeInfo tradeInfo;

	public TradeInfo getTradeInfo() {
		return tradeInfo;
	}

	public void setTradeInfo(TradeInfo tradeInfo) {
		this.tradeInfo = tradeInfo;
	}

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ErrorHandlingRechargeResponse").append("=").append("[");
		sb.append("tradeSeq").append("=").append(tradeSeq).append(",");
		if (tradeInfo != null) {
			sb.append(tradeInfo.toString()).append(",");
		}
		sb.append("]");
		return sb.toString();
	}
}
