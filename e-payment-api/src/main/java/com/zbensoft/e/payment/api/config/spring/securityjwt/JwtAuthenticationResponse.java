package com.zbensoft.e.payment.api.config.spring.securityjwt;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationResponse implements Serializable {
	public boolean isEmplyoeeFlag() {
		return emplyoeeFlag;
	}

	public void setEmplyoeeFlag(boolean emplyoeeFlag) {
		this.emplyoeeFlag = emplyoeeFlag;
	}

	private static final long serialVersionUID = 1250166508152483573L;

	private String token;
	private String userId;
	private String userName;


	private String idNumber;
	private String clapStoreNumber;


	private String loginUserName;
	private Collection<ZBGrantedAuthority> authorities;
	private JwtUser user;
	
	private Integer isDefaultPassword;
	private Integer isDefaultPayPassword;
	private boolean emplyoeeFlag = false;
	private String emplyoeeSellerUserId;
	private int remainingTimes = -1;
	private int emailBindStatus;


	public JwtAuthenticationResponse() {

	}

	public JwtAuthenticationResponse(String token, String userId, String userName,String loginUserName,Integer isDefaultPassword,Integer isDefaultPayPassword,Integer emailBindStatus,Collection<? extends GrantedAuthority> authorities, JwtUser user) {
		this.token = token;
		this.userId = userId;
		this.userName = userName;
		this.loginUserName = loginUserName;
		this.authorities = (Collection<ZBGrantedAuthority>) authorities;
		this.isDefaultPassword=isDefaultPassword;
		this.isDefaultPayPassword=isDefaultPayPassword;
		this.user = user;
		this.emplyoeeFlag = user.isEmplyoee();
		this.emplyoeeSellerUserId = user.getEmplyoeeSellerUserId();
		this.emailBindStatus=emailBindStatus;
	}


	public int getRemainingTimes() {
		return remainingTimes;
	}

	public void setRemainingTimes(int remainingTimes) {
		this.remainingTimes = remainingTimes;
	}

	public String getEmplyoeeSellerUserId() {
		return emplyoeeSellerUserId;
	}

	public void setEmplyoeeSellerUserId(String emplyoeeSellerUserId) {
		this.emplyoeeSellerUserId = emplyoeeSellerUserId;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public void setUser(JwtUser user) {
		this.user = user;
	}

	public JwtUser getUser() {
		return user;
	}

	public String getUserId() {
		return userId;
	}

	public String getToken() {
		return this.token;
	}

	public String getUserName() {
		return userName;
	}

	public Collection<ZBGrantedAuthority> getAuthorities() {
		return authorities;
	}
	public String getClapStoreNumber() {
		return clapStoreNumber;
	}

	public void setClapStoreNumber(String clapStoreNumber) {
		this.clapStoreNumber = clapStoreNumber;
	}
	
	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public Integer getIsDefaultPassword() {
		return isDefaultPassword;
	}

	public void setIsDefaultPassword(Integer isDefaultPassword) {
		this.isDefaultPassword = isDefaultPassword;
	}

	public Integer getIsDefaultPayPassword() {
		return isDefaultPayPassword;
	}

	public void setIsDefaultPayPassword(Integer isDefaultPayPassword) {
		this.isDefaultPayPassword = isDefaultPayPassword;
	}

	public int getEmailBindStatus() {
		return emailBindStatus;
	}

	public void setEmailBindStatus(int emailBindStatus) {
		this.emailBindStatus = emailBindStatus;
	}

}
