package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapDistribution {

	
	public ClapDistribution(){
		
	}
	public ClapDistribution(String record){

		String[] newDistribution = record.split(",",20);
		if(newDistribution!=null&&newDistribution.length==8){
			if(StringUtils.isNoneEmpty(newDistribution[0])){
				name=newDistribution[0];
			}
			if(StringUtils.isNoneEmpty(newDistribution[1])){
				startTime=newDistribution[1];
			}
			if(StringUtils.isNoneEmpty(newDistribution[2])){
				endTime=newDistribution[2];
			}
			if(StringUtils.isNoneEmpty(newDistribution[3])){
				status=newDistribution[3];
			}
			if(StringUtils.isNoneEmpty(newDistribution[4])){
				uniqueCode=newDistribution[4];
			}
			if(StringUtils.isNoneEmpty(newDistribution[5])){
				amount=newDistribution[5];
			}
			if(StringUtils.isNoneEmpty(newDistribution[6])){
				description=newDistribution[6];
			}
			if(StringUtils.isNoneEmpty(newDistribution[7])){
				clapStoreCode=newDistribution[7];
			}
			
			
			
		}
	
	}
	
	private String name;
	private String startTime;
	private String endTime;
	private String status;
	private String uniqueCode;
	private String amount;
	private String description;
	private String clapStoreCode;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUniqueCode() {
		return uniqueCode;
	}
	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getClapStoreCode() {
		return clapStoreCode;
	}
	public void setClapStoreCode(String clapStoreCode) {
		this.clapStoreCode = clapStoreCode;
	}
	
	
	
}
