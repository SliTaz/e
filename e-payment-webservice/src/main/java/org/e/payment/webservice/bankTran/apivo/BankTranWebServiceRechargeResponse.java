package org.e.payment.webservice.bankTran.apivo;

import com.zbensoft.e.payment.common.cxf.SOAPResultCode;

public class BankTranWebServiceRechargeResponse {

	private SOAPResultCode resultCode;

	public SOAPResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(SOAPResultCode resultCode) {
		this.resultCode = resultCode;
	}
	
}
