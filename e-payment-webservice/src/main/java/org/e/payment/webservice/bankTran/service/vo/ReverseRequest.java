package org.e.payment.webservice.bankTran.service.vo;

public class ReverseRequest {
	private String interfaceVersion;
	private String bankId;
	private String refNo;
	private ReverseReqBody reverseReqBody;
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

	public ReverseReqBody getReverseReqBody() {
		return reverseReqBody;
	}

	public void setReverseReqBody(ReverseReqBody reverseReqBody) {
		this.reverseReqBody = reverseReqBody;
	}

	public String getSigMsg() {
		return SigMsg;
	}

	public void setSigMsg(String sigMsg) {
		SigMsg = sigMsg;
	}

}
