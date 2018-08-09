package com.zbensoft.e.payment.api.vo.codeHelp;

public class CodeHelp {

	private int code;
	private String key;
	String reason;
	String reason_es;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReason_es() {
		return reason_es;
	}
	public void setReason_es(String reason_es) {
		this.reason_es = reason_es;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
