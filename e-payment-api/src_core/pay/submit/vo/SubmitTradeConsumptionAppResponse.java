package org.e.payment.core.pay.submit.vo;

public class SubmitTradeConsumptionAppResponse {
	private String tradeSeq;
	private String createTime;

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubmitTradeConsumptionAppResponse").append("=").append("[");
		sb.append("tradeSeq").append("=").append(tradeSeq).append(",");
		sb.append("tradcreateTimeeSeq").append("=").append(createTime).append(",");
		sb.append("]");
		return sb.toString();
	}
}
