package com.zbensoft.e.payment.api.vo.bankCharge;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;

public class BankChargeDocument {
	
	public BankChargeDocument(){
		
	}
	
	
	private String identifierRegistry;
	private String documentName;
	private String documentType;
	private String originalAmount;
	private String payableAmount;
	private String documentDate; 
	
	public String getIdentifierRegistry() {
		return identifierRegistry;
	}



	public void setIdentifierRegistry(String identifierRegistry) {
		this.identifierRegistry = identifierRegistry;
	}



	public String getDocumentName() {
		return documentName;
	}



	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}



	public String getDocumentType() {
		return documentType;
	}



	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}



	public String getOriginalAmount() {
		return originalAmount;
	}



	public void setOriginalAmount(String originalAmount) {
		this.originalAmount = originalAmount;
	}



	public String getPayableAmount() {
		return payableAmount;
	}



	public void setPayableAmount(String payableAmount) {
		this.payableAmount = payableAmount;
	}



	public String getDocumentDate() {
		return documentDate;
	}



	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}



	public boolean decode(String tempString, String key) {
		if (StringUtils.isEmpty(tempString)) {
			TASK_LOG.INFO(String.format("%s read doucment is empty", key));
			return false;
		}
		if (tempString.length() != 87) {
			TASK_LOG.INFO(String.format("%s read doucment length not 87,%s", key, tempString));
			return false;
		}
		int index = 0;
		
		identifierRegistry= tempString.substring(index, index+8).trim();
		index += 8;
		documentName= tempString.substring(index, index+30).trim();
		index += 30;
		documentType= tempString.substring(index, index+3).trim();
		index += 3;
		originalAmount= tempString.substring(index, index+18).trim();
		index += 18;
		payableAmount= tempString.substring(index, index+18).trim();
		index += 18;
		documentDate= tempString.substring(index).trim();
		
		return true;
	}



	public boolean validate(String key) {
		//TODO 目前无需校验
		return true;
	}
	
	

}
