package com.zbensoft.e.payment.api.vo.webservice.bankTran;

public class BankTranWebServiceReverseControllerRequest {
	private String userName;
	private String password;
	private String ipAddress;
	private String bankId;
	private String interfaceVersion;
	private String refNo;
	private String reverseRefNo;

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getReverseRefNo() {
		return reverseRefNo;
	}

	public void setReverseRefNo(String reverseRefNo) {
		this.reverseRefNo = reverseRefNo;
	}

}
