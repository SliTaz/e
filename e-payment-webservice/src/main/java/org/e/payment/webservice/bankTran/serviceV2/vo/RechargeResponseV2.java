package org.e.payment.webservice.bankTran.serviceV2.vo;

public class RechargeResponseV2 {
	private String interfaceVersion;
	private String refNo;
	private BankRechargeRespBodyV2 bankRechargeRespBody;
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

	public BankRechargeRespBodyV2 getBankRechargeRespBody() {
		return bankRechargeRespBody;
	}

	public void setBankRechargeRespBody(BankRechargeRespBodyV2 bankRechargeRespBody) {
		this.bankRechargeRespBody = bankRechargeRespBody;
	}

	public String getSigMsg() {
		return SigMsg;
	}

	public void setSigMsg(String sigMsg) {
		SigMsg = sigMsg;
	}

}
