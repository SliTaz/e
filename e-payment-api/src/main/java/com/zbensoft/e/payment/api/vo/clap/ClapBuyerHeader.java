package com.zbensoft.e.payment.api.vo.clap;

import org.apache.commons.lang3.StringUtils;

public class ClapBuyerHeader {
	
	public ClapBuyerHeader(){
		
	}
	
	public ClapBuyerHeader(String record){
		String[] newbuyerHeader = record.split(",",20);
		if(newbuyerHeader!=null&&newbuyerHeader.length==2){
			if(StringUtils.isNoneEmpty(newbuyerHeader[0])){
				generateFileDate=newbuyerHeader[0];
			}
			if(StringUtils.isNoneEmpty(newbuyerHeader[1])){
				totalFileRecord=newbuyerHeader[1];
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
