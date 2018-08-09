package com.zbensoft.e.payment.api.vo.webservice.bankTran;

import com.zbensoft.e.payment.common.cxf.SOAPResultCode;

public class BankTranWebServiceRechargeControllerResponse {

	private SOAPResultCode resultCode;

	public SOAPResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(SOAPResultCode resultCode) {
		this.resultCode = resultCode;
	}
	
}
