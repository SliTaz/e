package org.e.payment.core.pay.submit.vo;

public class BankTranWebserviceReverseRequest {

	private String payAppId = "10005";
	private String orderNo;// fix：123
	private String tradeType;// 交易类型 12：银行取消
	private String ipAddress;// IP地址
	private String bankId;
	private String reverseRefNo;

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPayAppId() {
		return payAppId;
	}

	public void setPayAppId(String payAppId) {
		this.payAppId = payAppId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getReverseRefNo() {
		return reverseRefNo;
	}

	public void setReverseRefNo(String reverseRefNo) {
		this.reverseRefNo = reverseRefNo;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BankTranWebserviceReverseRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("tradeType").append("=").append(tradeType).append(",");
		sb.append("ipAddress").append("=").append(ipAddress).append(",");
		sb.append("bankId").append("=").append(bankId).append(",");
		sb.append("reverseRefNo").append("=").append(reverseRefNo).append(",");
		sb.append("]");
		return sb.toString();
	}
}
