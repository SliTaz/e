package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapSellerAccount {

	
	public ClapSellerAccount(){
		
	}
	public ClapSellerAccount(String record){

		String[] newDistribution = record.split(",",20);
		if(newDistribution!=null&&newDistribution.length==7){
			if(StringUtils.isNoneEmpty(newDistribution[0])){
				vid=newDistribution[0];
			}
			if(StringUtils.isNoneEmpty(newDistribution[1])){
				holderName=newDistribution[1];
			}
			if(StringUtils.isNoneEmpty(newDistribution[2])){
				bankId=newDistribution[2];
			}
			if(StringUtils.isNoneEmpty(newDistribution[3])){
				bankAccountNo=newDistribution[3];
			}
			if(StringUtils.isNoneEmpty(newDistribution[4])){
				bankAccountType=newDistribution[4];
			}
			if(StringUtils.isNoneEmpty(newDistribution[5])){
				status=newDistribution[5];
			}
			if(StringUtils.isNoneEmpty(newDistribution[6])){
				clapStoreNo=newDistribution[6];
			}
		}
	
	}
	
	
	
	
	private String vid;
	private String holderName;
	private String bankId;
	private String bankAccountNo;
	private String bankAccountType;
	private String status;
	private String clapStoreNo;
	
	
	
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankAccountNo() {
		return bankAccountNo;
	}
	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}
	public String getBankAccountType() {
		return bankAccountType;
	}
	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}
	public String getClapStoreNo() {
		return clapStoreNo;
	}
	public void setClapStoreNo(String clapStoreNo) {
		this.clapStoreNo = clapStoreNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
