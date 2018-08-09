package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapSellerAccountHeader {
	
	public ClapSellerAccountHeader(){
		
	}
	
	public ClapSellerAccountHeader(String record){
		String[] newDistributionHeader = record.split(",",20);
		if(newDistributionHeader!=null&&newDistributionHeader.length==2){
			if(StringUtils.isNoneEmpty(newDistributionHeader[0])){
				generateFileDate=newDistributionHeader[0];
			}
			if(StringUtils.isNoneEmpty(newDistributionHeader[1])){
				totalFileRecord=newDistributionHeader[1];
			}
		}
	}
	
	
	
	private String generateFileDate;
	private String totalFileRecord;
	public String getGenerateFileDate() {
		return generateFileDate;
	}

	public void setGenerateFileDate(String generateFileDate) {
		this.generateFileDate = generateFileDate;
	}

	public String getTotalFileRecord() {
		return totalFileRecord;
	}

	public void setTotalFileRecord(String totalFileRecord) {
		this.totalFileRecord = totalFileRecord;
	}
	
	


}
