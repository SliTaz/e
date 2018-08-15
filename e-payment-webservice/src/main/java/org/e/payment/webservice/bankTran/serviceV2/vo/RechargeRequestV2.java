package org.e.payment.webservice.bankTran.serviceV2.vo;

public class RechargeRequestV2 {
	private String interfaceVersion;
	private String bankId;
	private String refNo;
	private BankRechargeReqBodyV2 bankRechargeReqBody;
	private String SigMsg;

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

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

	public BankRechargeReqBodyV2 getBankRechargeReqBody() {
		return bankRechargeReqBody;
	}

	public void setBankRechargeReqBody(BankRechargeReqBodyV2 bankRechargeReqBody) {
		this.bankRechargeReqBody = bankRechargeReqBody;
	}

	public String getSigMsg() {
		return SigMsg;
	}

	public void setSigMsg(String sigMsg) {
		SigMsg = sigMsg;
	}

}
