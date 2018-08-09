package com.zbensoft.e.payment.api.vo.bankCharge;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.api.log.TASK_LOG;

public class BankChargeAdjustment {
	
	public BankChargeAdjustment(){
		
	}
	
	
	private String identifierRegistry;
	private String amount;
	private String description;
	private String adjustmentCode;
	private String adjustmentType;
	
	
	
	
	public String getIdentifierRegistry() {
		return identifierRegistry;
	}



	public void setIdentifierRegistry(String identifierRegistry) {
		this.identifierRegistry = identifierRegistry;
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



	public String getAdjustmentCode() {
		return adjustmentCode;
	}



	public void setAdjustmentCode(String adjustmentCode) {
		this.adjustmentCode = adjustmentCode;
	}



	public String getAdjustmentType() {
		return adjustmentType;
	}



	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}



	public boolean decode(String tempString, String key) {
		if (StringUtils.isEmpty(tempString)) {
			TASK_LOG.INFO(String.format("%s read adjustment is empty", key));
			return false;
		}
		if (tempString.length() != 102) {
			TASK_LOG.INFO(String.format("%s read adjustment length not 102,%s", key, tempString));
			return false;
		}
		int index = 0;
		
		identifierRegistry= tempString.substring(index, index+8).trim();
		index += 8;
		amount= tempString.substring(index, index+18).trim();
		index += 18;           
		description= tempString.substring(index, index+70).trim();
		index += 70;     
		adjustmentCode= tempString.substring(index, index+3).trim();
		index += 3;      
		adjustmentType=tempString.substring(index).trim();  
		
		
		return true;
	}



	public boolean validate(String key) {
		// TODO 暂时无需校验
		return true;
	}
	
	

}
