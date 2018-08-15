package org.e.payment.webservice.bankTran.service.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReverseRespBody implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7102826479085487954L;
	private int resultCode;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
}
