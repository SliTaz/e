package org.e.payment.webservice.bankTran.service.vo;

public class RechargeRequest {
	private String interfaceVersion;
	private String bankId;
	private String refNo;
	private BankRechargeReqBody bankRechargeReqBody;
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

	public BankRechargeReqBody getBankRechargeReqBody() {
		return bankRechargeReqBody;
	}

	public void setBankRechargeReqBody(BankRechargeReqBody bankRechargeReqBody) {
		this.bankRechargeReqBody = bankRechargeReqBody;
	}

	public String getSigMsg() {
		return SigMsg;
	}

	public void setSigMsg(String sigMsg) {
		SigMsg = sigMsg;
	}

}
