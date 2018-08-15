package org.e.payment.webservice.bankTran.serviceV2.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BankRechargeReqBodyV2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2431736745020007923L;
	
	
	private BankPaymentInfoV2 bankPaymentInfo;

	public BankPaymentInfoV2 getBankPaymentInfo() {
		return bankPaymentInfo;
	}

	public void setBankPaymentInfo(BankPaymentInfoV2 bankPaymentInfo) {
		this.bankPaymentInfo = bankPaymentInfo;
	}
	
}
