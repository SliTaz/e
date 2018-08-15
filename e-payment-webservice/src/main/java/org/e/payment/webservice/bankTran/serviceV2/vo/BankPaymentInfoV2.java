package org.e.payment.webservice.bankTran.serviceV2.vo;

public class BankPaymentInfoV2 {

	private String amount;
	private String vid;
	private String patrimonyCardCode;
	private String paymentTime;
	
	public String getPatrimonyCardCode() {
		return patrimonyCardCode;
	}
	public void setPatrimonyCardCode(String patrimonyCardCode) {
		this.patrimonyCardCode = patrimonyCardCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(String paymentTime) {
		this.paymentTime = paymentTime;
	}
	
}
