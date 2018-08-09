package com.zbensoft.e.payment.api.vo.buyerRegister;

public class RegisterResponse {
	private String idNumber;
	private String patrimonyCardCode;

	private String imgCode;
	private String imgValidateCode;
	
	private String r_cod;
	private String r_ser;
	private boolean r_stat;
	private String r_ced;
	private String r_n1;
	private String r_n2;
	private String r_ap1;
	private String r_ap2;
	private String r_gen;
	private String r_fnac;
	private String r_mail;
	
	private String loginPassword;
	private String paymentPassword;
	
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getPatrimonyCardCode() {
		return patrimonyCardCode;
	}
	public void setPatrimonyCardCode(String patrimonyCardCode) {
		this.patrimonyCardCode = patrimonyCardCode;
	}
	public String getImgCode() {
		return imgCode;
	}
	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}
	public String getImgValidateCode() {
		return imgValidateCode;
	}
	public void setImgValidateCode(String imgValidateCode) {
		this.imgValidateCode = imgValidateCode;
	}
	public String getLoginPassword() {
		return loginPassword;
	}
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	public String getPaymentPassword() {
		return paymentPassword;
	}
	public void setPaymentPassword(String paymentPassword) {
		this.paymentPassword = paymentPassword;
	}
	public String getR_cod() {
		return r_cod;
	}
	public void setR_cod(String r_cod) {
		this.r_cod = r_cod;
	}
	public String getR_ser() {
		return r_ser;
	}
	public void setR_ser(String r_ser) {
		this.r_ser = r_ser;
	}
	
	public boolean isR_stat() {
		return r_stat;
	}
	public void setR_stat(boolean r_stat) {
		this.r_stat = r_stat;
	}
	public String getR_ced() {
		return r_ced;
	}
	public void setR_ced(String r_ced) {
		this.r_ced = r_ced;
	}
	public String getR_n1() {
		return r_n1;
	}
	public void setR_n1(String r_n1) {
		this.r_n1 = r_n1;
	}
	public String getR_n2() {
		return r_n2;
	}
	public void setR_n2(String r_n2) {
		this.r_n2 = r_n2;
	}
	public String getR_ap1() {
		return r_ap1;
	}
	public void setR_ap1(String r_ap1) {
		this.r_ap1 = r_ap1;
	}
	public String getR_ap2() {
		return r_ap2;
	}
	public void setR_ap2(String r_ap2) {
		this.r_ap2 = r_ap2;
	}
	public String getR_gen() {
		return r_gen;
	}
	public void setR_gen(String r_gen) {
		this.r_gen = r_gen;
	}
	public String getR_fnac() {
		return r_fnac;
	}
	public void setR_fnac(String r_fnac) {
		this.r_fnac = r_fnac;
	}
	public String getR_mail() {
		return r_mail;
	}
	public void setR_mail(String r_mail) {
		this.r_mail = r_mail;
	}

}
