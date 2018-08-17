package org.e.payment.core.pay.submit.vo;

public class SubmitTradeTransferBankRequest {

	private String payAppId;
	private String orderNo;// fix：123
	private String tradeType;// 交易类型5：转账到银行卡
	private String bankBindId;// 快捷支付，绑定的银行卡编号，可以为空，空：使用余额转账
	private String payUserId;// 当前登录用户
	// private String payUserName;
	private String payAmount;
	private String payFee;
	private String recvBankCardNo;// 收款银行卡号
	private String recvAmount;
	private String recvFee;
	private String payPassword;// 当前用户支付密码

	public String getRecvBankCardNo() {
		return recvBankCardNo;
	}

	public void setRecvBankCardNo(String recvBankCardNo) {
		this.recvBankCardNo = recvBankCardNo;
	}

	public String getBankBindId() {
		return bankBindId;
	}

	public void setBankBindId(String bankBindId) {
		this.bankBindId = bankBindId;
	}

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
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

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	// public String getPayUserName() {
	// return payUserName;
	// }

	// public void setPayUserName(String payUserName) {
	// this.payUserName = payUserName;
	// }

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
		sb.append("SubmitTradeTransferBankRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("tradeType").append("=").append(tradeType).append(",");
		sb.append("bankBindId").append("=").append(bankBindId).append(",");
		sb.append("payUserId").append("=").append(payUserId).append(",");
		sb.append("payAmount").append("=").append(payAmount).append(",");
		sb.append("payFee").append("=").append(payFee).append(",");
		sb.append("recvBankCardNo").append("=").append(recvBankCardNo).append(",");
		sb.append("recvAmount").append("=").append(recvAmount).append(",");
		sb.append("recvFee").append("=").append(recvFee).append(",");
		sb.append("payPassword").append("=").append(payPassword).append(",");
		sb.append("]");
		return sb.toString();
	}
}
