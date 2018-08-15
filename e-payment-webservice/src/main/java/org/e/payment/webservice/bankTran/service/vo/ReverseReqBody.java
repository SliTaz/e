package org.e.payment.webservice.bankTran.service.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReverseReqBody implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7171073821686231287L;
	private String reverseRefNo;

	public String getReverseRefNo() {
		return reverseRefNo;
	}

	public void setReverseRefNo(String reverseRefNo) {
		this.reverseRefNo = reverseRefNo;
	}
	
}
