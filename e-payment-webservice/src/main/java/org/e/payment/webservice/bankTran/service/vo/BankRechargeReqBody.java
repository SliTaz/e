package org.e.payment.webservice.bankTran.service.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BankRechargeReqBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2431736745020007923L;
	
	
	private BankPaymentInfo bankPaymentInfo;

	public BankPaymentInfo getBankPaymentInfo() {
		return bankPaymentInfo;
	}

	public void setBankPaymentInfo(BankPaymentInfo bankPaymentInfo) {
		this.bankPaymentInfo = bankPaymentInfo;
	}
	
}
