package org.e.payment.core.pay.submit.vo;

public class CreateTradeResponse {
	private String tradeSeq;
	private String orderNo;

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CreateTradeResponse").append("=").append("[");
		sb.append("tradeSeq").append("=").append(tradeSeq).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("]");
		return sb.toString();
	}
}
