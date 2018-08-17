package org.e.payment.core.pay.submit.vo;

public class BankTranWebserviceRechargeResponse {
	private String tradeSeq;

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BankTranWebserviceRechargeResponse").append("=").append("[");
		sb.append("tradeSeq").append("=").append(tradeSeq).append(",");
		sb.append("]");
		return sb.toString();
	}
}
