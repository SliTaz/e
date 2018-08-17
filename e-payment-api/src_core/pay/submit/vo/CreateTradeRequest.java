package org.e.payment.core.pay.submit.vo;

public class CreateTradeRequest {
	private String payAppId;
	private String orderNo;
	private String tradeType;
	private String consumptionName;
	private String callbackUrl;
	private String payUserId;
	private String payUserName;
	private String payBankId;
	private String payBankCardNo;
	private String payAmount;
	private String recvUserId;
	private String recvUserName;
	private String recvBankId;
	private String recvBankCardNo;

	public String getPayBankId() {
		return payBankId;
	}

	public void setPayBankId(String payBankId) {
		this.payBankId = payBankId;
	}

	public String getPayBankCardNo() {
		return payBankCardNo;
	}

	public void setPayBankCardNo(String payBankCardNo) {
		this.payBankCardNo = payBankCardNo;
	}

	public String getRecvBankId() {
		return recvBankId;
	}

	public void setRecvBankId(String recvBankId) {
		this.recvBankId = recvBankId;
	}

	public String getRecvBankCardNo() {
		return recvBankCardNo;
	}

	public void setRecvBankCardNo(String recvBankCardNo) {
		this.recvBankCardNo = recvBankCardNo;
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

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getConsumptionName() {
		return consumptionName;
	}

	public void setConsumptionName(String consumptionName) {
		this.consumptionName = consumptionName;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	public String getPayUserName() {
		return payUserName;
	}

	public void setPayUserName(String payUserName) {
		this.payUserName = payUserName;
	}

	public String getRecvUserId() {
		return recvUserId;
	}

	public void setRecvUserId(String recvUserId) {
		this.recvUserId = recvUserId;
	}

	public String getRecvUserName() {
		return recvUserName;
	}

	public void setRecvUserName(String recvUserName) {
		this.recvUserName = recvUserName;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CreateTradeRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("tradeType").append("=").append(tradeType).append(",");
		sb.append("consumptionName").append("=").append(consumptionName).append(",");
		sb.append("callbackUrl").append("=").append(callbackUrl).append(",");
		sb.append("payUserId").append("=").append(payUserId).append(",");
		sb.append("payUserId").append("=").append(payUserId).append(",");
		sb.append("payUserName").append("=").append(payUserName).append(",");
		sb.append("payBankId").append("=").append(payBankId).append(",");
		sb.append("payBankCardNo").append("=").append(payBankCardNo).append(",");
		sb.append("payAmount").append("=").append(payAmount).append(",");
		sb.append("recvUserId").append("=").append(recvUserId).append(",");
		sb.append("recvUserName").append("=").append(recvUserName).append(",");
		sb.append("recvBankId").append("=").append(recvBankId).append(",");
		sb.append("recvBankCardNo").append("=").append(recvBankCardNo).append(",");
		sb.append("]");
		return sb.toString();
	}
}
