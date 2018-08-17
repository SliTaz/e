package org.e.payment.core.pay.submit.vo;

public class SubmitTradeTransferResponse {
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
		sb.append("SubmitTradeTransferResponse").append("=").append("[");
		sb.append("tradeSeq").append("=").append(tradeSeq).append(",");
		sb.append("]");
		return sb.toString();
	}
}
