package com.zbensoft.e.payment.api.config.spring.securityjwt;

import java.io.Serializable;

public class JwtAuthenticationRequest implements Serializable {

	private static final long serialVersionUID = -8445943548965154778L;

	private String userName;
	private String password;
	private String appVersion;
	private String mobileInfo;

	public JwtAuthenticationRequest() {
		super();
	}

	public JwtAuthenticationRequest(String username, String password) {
		this.setUserName(username);
		this.setPassword(password);
	}



	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getMobileInfo() {
		return mobileInfo;
	}

	public void setMobileInfo(String mobileInfo) {
		this.mobileInfo = mobileInfo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
