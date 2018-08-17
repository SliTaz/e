package org.e.payment.core.pay.submit.vo;

public class PayCalcPriceRequest {
	
	private String payAppId;
	private String tradeSeq;
	private String orderNo;
	private String tradeType;
	private String payGatewayId;
	private String payUserId;
	private String payUserName;
	private String payBankId;
	private String payBankCardNo;
	private String payAmount;
	private String payFee;
	private String recvUserId;
	private String recvUserName;
	private String recvBankId;
	private String recvBankCardNo;
	private String recvAmount;
	private String recvFee;
	public String getPayAppId() {
		return payAppId;
	}
	public void setPayAppId(String payAppId) {
		this.payAppId = payAppId;
	}
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
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getPayGatewayId() {
		return payGatewayId;
	}
	public void setPayGatewayId(String payGatewayId) {
		this.payGatewayId = payGatewayId;
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
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public String getPayFee() {
		return payFee;
	}
	public void setPayFee(String payFee) {
		this.payFee = payFee;
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
	public String getRecvAmount() {
		return recvAmount;
	}
	public void setRecvAmount(String recvAmount) {
		this.recvAmount = recvAmount;
	}
	public String getRecvFee() {
		return recvFee;
	}
	public void setRecvFee(String recvFee) {
		this.recvFee = recvFee;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PayCalcPriceRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("tradeSeq").append("=").append(tradeSeq).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("tradeType").append("=").append(tradeType).append(",");
		sb.append("payGatewayId").append("=").append(payGatewayId).append(",");
		sb.append("payUserId").append("=").append(payUserId).append(",");
		sb.append("payUserName").append("=").append(payUserName).append(",");
		sb.append("payBankId").append("=").append(payBankId).append(",");
		sb.append("payBankCardNo").append("=").append(payBankCardNo).append(",");
		sb.append("payAmount").append("=").append(payAmount).append(",");
		sb.append("payFee").append("=").append(payFee).append(",");
		sb.append("recvUserId").append("=").append(recvUserId).append(",");
		sb.append("recvUserName").append("=").append(recvUserName).append(",");
		sb.append("recvBankId").append("=").append(recvBankId).append(",");
		sb.append("recvBankCardNo").append("=").append(recvBankCardNo).append(",");
		sb.append("recvAmount").append("=").append(recvAmount).append(",");
		sb.append("recvFee").append("=").append(recvFee).append(",");
		sb.append("]");
		return sb.toString();
	}

}
