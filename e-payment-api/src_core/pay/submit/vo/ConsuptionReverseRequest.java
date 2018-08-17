package org.e.payment.core.pay.submit.vo;

public class ConsuptionReverseRequest {

	private String tradeSeq;
	private String receiveUserId;
	private String payEmployeeId;
	private String payBackAmount;
	private String consumerUserClapId;
	private String familyId;
	private String tradeType;
	private String payAppId;
	private String orderNo;
	private String payUserId;
	private String consumerPayPassword;
	public String getReceiveUserId() {
		return receiveUserId;
	}

	public void setReceiveUserId(String receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getPayEmployeeId() {
		return payEmployeeId;
	}

	public void setPayEmployeeId(String payEmployeeId) {
		this.payEmployeeId = payEmployeeId;
	}

	public String getPayBackAmount() {
		return payBackAmount;
	}

	public void setPayBackAmount(String payBackAmount) {
		this.payBackAmount = payBackAmount;
	}

	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}

	public String getConsumerUserClapId() {
		return consumerUserClapId;
	}

	public void setConsumerUserClapId(String consumerUserClapId) {
		this.consumerUserClapId = consumerUserClapId;
	}

	public String getFamilyId() {
		return familyId;
	}

	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
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

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	public String getConsumerPayPassword() {
		return consumerPayPassword;
	}

	public void setConsumerPayPassword(String consumerPayPassword) {
		this.consumerPayPassword = consumerPayPassword;
	}

	
}
