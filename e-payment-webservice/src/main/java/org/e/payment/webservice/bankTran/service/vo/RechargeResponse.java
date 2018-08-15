package org.e.payment.webservice.bankTran.service.vo;

public class RechargeResponse {
	private String interfaceVersion;
	private String refNo;
	private BankRechargeRespBody bankRechargeRespBody;
	private String SigMsg;

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public BankRechargeRespBody getBankRechargeRespBody() {
		return bankRechargeRespBody;
	}

	public void setBankRechargeRespBody(BankRechargeRespBody bankRechargeRespBody) {
		this.bankRechargeRespBody = bankRechargeRespBody;
	}

	public String getSigMsg() {
		return SigMsg;
	}

	public void setSigMsg(String sigMsg) {
		SigMsg = sigMsg;
	}

}
