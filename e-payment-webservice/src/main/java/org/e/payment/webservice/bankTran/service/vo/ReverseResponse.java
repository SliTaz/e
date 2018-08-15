package org.e.payment.webservice.bankTran.service.vo;

public class ReverseResponse {
	private String interfaceVersion;
	private String refNo;
	private ReverseRespBody reverseRespBody;
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


	public ReverseRespBody getReverseRespBody() {
		return reverseRespBody;
	}

	public void setReverseRespBody(ReverseRespBody reverseRespBody) {
		this.reverseRespBody = reverseRespBody;
	}

	public String getSigMsg() {
		return SigMsg;
	}

	public void setSigMsg(String sigMsg) {
		SigMsg = sigMsg;
	}

}
