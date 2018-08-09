package com.zbensoft.e.payment.api.vo.bankCharge;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;

public class BankChargeTotal {
	
	public BankChargeTotal(){
		
	}
	
	
	private String identifierRegistry;
	private String totalDebitRecords;
	private String totalCreditsRecords;
	private String totalBatchAmount;
	
	
	
	public String getIdentifierRegistry() {
		return identifierRegistry;
	}



	public void setIdentifierRegistry(String identifierRegistry) {
		this.identifierRegistry = identifierRegistry;
	}







	public String getTotalDebitRecords() {
		return totalDebitRecords;
	}



	public void setTotalDebitRecords(String totalDebitRecords) {
		this.totalDebitRecords = totalDebitRecords;
	}



	public String getTotalCreditsRecords() {
		return totalCreditsRecords;
	}



	public void setTotalCreditsRecords(String totalCreditsRecords) {
		this.totalCreditsRecords = totalCreditsRecords;
	}



	public String getTotalBatchAmount() {
		return totalBatchAmount;
	}



	public void setTotalBatchAmount(String totalBatchAmount) {
		this.totalBatchAmount = totalBatchAmount;
	}



	@Override
	public String toString() {
		StringBuffer sb =new StringBuffer();
		sb.append(identifierRegistry);//Record identifier
		sb.append(totalDebitRecords);//Number of debit instructions
		sb.append(totalCreditsRecords);//Amount of credits instructions
		sb.append(totalBatchAmount);//Total batch amount
		return sb.toString();
	}



	public boolean decode(String tempString, String key) {
		if (StringUtils.isEmpty(tempString)) {
			TASK_LOG.INFO(String.format("%s read adjustment is empty", key));
			return false;
		}
		if (tempString.length() != 36) {
			TASK_LOG.INFO(String.format("%s read adjustment length not 36,only %d ,%s", key, tempString.length(), tempString));
			return false;
		}
		int index = 0;
		
		identifierRegistry= tempString.substring(index, index+8).trim();
		index += 8;
		totalDebitRecords= tempString.substring(index, index+5).trim();
		index += 5;
		totalCreditsRecords= tempString.substring(index, index+5).trim();
		index += 5;
		totalBatchAmount= tempString.substring(index).trim();
		
		return true;
	}



	public boolean validate(String key) {
		if (StringUtils.isEmpty(totalDebitRecords)) {
			RECONCILIATION_LOG.INFO(String.format("%s totalDebitRecords=%s must not empty", key, totalDebitRecords));
			return false;
		}
		if (StringUtils.isEmpty(totalCreditsRecords)) {
			RECONCILIATION_LOG.INFO(String.format("%s totalCreditsRecords=%s must not empty", key, totalCreditsRecords));
			return false;
		}
		if (StringUtils.isEmpty(totalBatchAmount)) {
			RECONCILIATION_LOG.INFO(String.format("%s totalBatchAmount=%s must not empty", key, totalBatchAmount));
			return false;
		}

		return true;
	}

}
