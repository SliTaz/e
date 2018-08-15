package org.e.payment.webservice.bankTran.serviceV2.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BankRechargeRespBodyV2 implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6450463568527189177L;
	private int resultCode;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
}
