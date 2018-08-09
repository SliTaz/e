package com.zbensoft.e.payment.api.config.spring.securityjwt;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zbensoft.e.payment.api.common.CommonFun;

public class JwtUser implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String username;
	private String password;
	private String email;
	private String loginUserName;
	private Collection<? extends GrantedAuthority> authorities;
	private Date lastPasswordResetDate;
	private boolean enabled;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired = true;
	private boolean accountNonExpired = true;
	// 身份证ID
	private String idNumber;
	// clap卡编号
	private String clapCardNo;

	private Integer isDefaultPassword;
	private Integer isDefaultPayPassword;
	private boolean isEmplyoee = false;
	private String emplyoeeSellerUserId;
	private int emailBindStatus = -1;

	public JwtUser() {

	}

	/**
	 * String id,String loginUserName, String username, String password, String email,String idNumber, Collection<? extends GrantedAuthority> authorities, Date
	 * lastPasswordResetDate, boolean enabled, boolean accountNonLocked
	 * 
	 * @param id
	 * @param loginUserName
	 * @param username
	 * @param password
	 * @param email
	 * @param idNumber
	 * @param authorities
	 * @param lastPasswordResetDate
	 * @param enabled
	 * @param accountNonLocked
	 */
	public JwtUser(String id, String loginUserName, String username, String password, String email, String idNumber, String clapCardNo, Collection<? extends GrantedAuthority> authorities, Date lastPasswordResetDate,
			boolean enabled, boolean accountNonLocked, Integer isDefaultPassword, Integer isDefaultPayPassword, int emailBindStatus) {
		this(id, loginUserName, username, password, email, idNumber, clapCardNo, authorities, lastPasswordResetDate, enabled, accountNonLocked, isDefaultPassword, isDefaultPayPassword, emailBindStatus, false, null);
	}

	public JwtUser(String id, String loginUserName, String username, String password, String email, String idNumber, String clapCardNo, Collection<? extends GrantedAuthority> authorities, Date lastPasswordResetDate,
			boolean enabled, boolean accountNonLocked, Integer isDefaultPassword, Integer isDefaultPayPassword, int emailBindStatus, boolean isEmplyoee, String emplyoeeSellerUserId) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = CommonFun.getEmailWhithStar(email);
		this.authorities = authorities;
		this.lastPasswordResetDate = lastPasswordResetDate;
		this.enabled = enabled;
		this.accountNonLocked = accountNonLocked;
		this.loginUserName = loginUserName;
		this.idNumber = idNumber;
		this.clapCardNo = clapCardNo;
		this.isDefaultPassword = isDefaultPassword;
		this.isDefaultPayPassword = isDefaultPayPassword;
		this.isEmplyoee = isEmplyoee;
		this.emplyoeeSellerUserId = emplyoeeSellerUserId;
		this.emailBindStatus = emailBindStatus;
	}

	public int getEmailBindStatus() {
		return emailBindStatus;
	}

	public void setEmailBindStatus(int emailBindStatus) {
		this.emailBindStatus = emailBindStatus;
	}

	public String getEmplyoeeSellerUserId() {
		return emplyoeeSellerUserId;
	}

	public void setEmplyoeeSellerUserId(String emplyoeeSellerUserId) {
		this.emplyoeeSellerUserId = emplyoeeSellerUserId;
	}

	public boolean isEmplyoee() {
		return isEmplyoee;
	}

	public void setEmplyoee(boolean isEmplyoee) {
		this.isEmplyoee = isEmplyoee;
	}

	public String getClapCardNo() {
		return clapCardNo;
	}

	public void setClapCardNo(String clapCardNo) {
		this.clapCardNo = clapCardNo;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getId() {
		return id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}

	public String getEmail() {
		return email;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
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

}
