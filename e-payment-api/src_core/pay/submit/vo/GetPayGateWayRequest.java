package org.e.payment.core.pay.submit.vo;

public class GetPayGateWayRequest {
	private String payAppId;
	private String payUserId;

	public String getPayAppId() {
		return payAppId;
	}

	public void setPayAppId(String payAppId) {
		this.payAppId = payAppId;
	}

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetPayGateWayRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("payUserId").append("=").append(payUserId).append(",");
		sb.append("]");
		return sb.toString();
	}

}
