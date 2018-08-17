package org.e.payment.core.pay.submit.vo;

import java.util.Date;

public class BankTranWebserviceRechargeRequest {

	private String payAppId = "10005";
	private String orderNo;// fix：123
	private String tradeType;// 交易类型 11：银行充值
	private String ipAddress;// IP地址
	private String bankId;
	private String vid;// 当前登录用户
	private String patrimonyCardCode;
	private Double amount;
	private Date paymentTime;// 用户支付时间
	private String interfaceVersion;

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}


	public String getPatrimonyCardCode() {
		return patrimonyCardCode;
	}

	public void setPatrimonyCardCode(String patrimonyCardCode) {
		this.patrimonyCardCode = patrimonyCardCode;
	}

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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BankTranWebserviceRechargeRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("tradeType").append("=").append(tradeType).append(",");
		sb.append("ipAddress").append("=").append(ipAddress).append(",");
		sb.append("bankId").append("=").append(bankId).append(",");
		sb.append("vid").append("=").append(vid).append(",");
		sb.append("amount").append("=").append(amount).append(",");
		sb.append("paymentTime").append("=").append(paymentTime).append(",");
		sb.append("]");
		return sb.toString();
	}
}
