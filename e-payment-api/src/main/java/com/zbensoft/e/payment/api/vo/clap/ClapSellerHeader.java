package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapSellerHeader {
	
	public ClapSellerHeader(){
		
	}
	
	public ClapSellerHeader(String record){
		String[] newSellerHeader = record.split(",",20);
		if(newSellerHeader!=null&&newSellerHeader.length==2){
			if(StringUtils.isNoneEmpty(newSellerHeader[0])){
				generateFileDate=newSellerHeader[0];
			}
			if(StringUtils.isNoneEmpty(newSellerHeader[1])){
				totalFileRecord=newSellerHeader[1];
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
