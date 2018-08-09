package com.zbensoft.e.payment.api.vo.bankCharge;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;

public class BankChargeBatch {
	
	public BankChargeBatch(){
		
	}
	
	
	private String identifierRegistry;
	private String referenceNumber;
	private String rif;
	private String payerName;
	private String valueDate;
	private String accountType;
	private String accountNumber;
	private String amount;
	private String currency;
	private String paymentType;
	private String status;//对账结果
	private String rejectionReason;//拒绝原因
	
	

	public String getIdentifierRegistry() {
		return identifierRegistry;
	}



	public void setIdentifierRegistry(String identifierRegistry) {
		this.identifierRegistry = identifierRegistry;
	}



	public String getReferenceNumber() {
		return referenceNumber;
	}



	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}



	public String getRif() {
		return rif;
	}



	public void setRif(String rif) {
		this.rif = rif;
	}



	public String getPayerName() {
		return payerName;
	}



	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}



	public String getValueDate() {
		return valueDate;
	}



	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}



	public String getAccountType() {
		return accountType;
	}



	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}



	public String getAccountNumber() {
		return accountNumber;
	}



	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}



	public String getAmount() {
		return amount;
	}



	public void setAmount(String amount) {
		this.amount = amount;
	}



	public String getCurrency() {
		return currency;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}



	public String getPaymentType() {
		return paymentType;
	}



	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getRejectionReason() {
		return rejectionReason;
	}



	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}



	@Override
	public String toString() {
		
		StringBuffer sb =new StringBuffer();
		sb.append(identifierRegistry);//Record identifier
		sb.append(referenceNumber);//Number of debit instructions
		sb.append(rif);//RIF
		sb.append(payerName);//payerName
		sb.append(valueDate);//Effective date of the debit
		sb.append(accountType);//accountType
		sb.append(accountNumber);//account Number
		sb.append(amount);//Total batch amount
		sb.append(currency);//fix value
		sb.append(paymentType);//fix value
		
		return sb.toString();
	}



	public boolean decode(String tempString, String key) {
		if (StringUtils.isEmpty(tempString)) {
			TASK_LOG.INFO(String.format("%s read one Batch is empty", key));
			return false;
		}
		if (tempString.length() != 201) {
			TASK_LOG.INFO(String.format("%s read one Batch length not 201,only %d, %s", key,tempString.length(), tempString));
			return false;
		}
		int index = 0;
		
		identifierRegistry= tempString.substring(index, index+8).trim();
		index += 8;
		referenceNumber= tempString.substring(index, index+8).trim();
		index += 8;
		rif= tempString.substring(index, index+10).trim();
		index += 10;
		payerName= tempString.substring(index, index+35).trim();
		index += 35;
		valueDate= tempString.substring(index, index+10).trim();
		index += 10;
		accountType= tempString.substring(index, index+2).trim();
		index += 2;
		accountNumber= tempString.substring(index, index+20).trim();
		index += 20;
		amount= tempString.substring(index, index+18).trim();
		index += 18;
		currency= tempString.substring(index, index+3).trim();
		index += 3;
		paymentType= tempString.substring(index, index+3).trim();
		index += 3;
		status= tempString.substring(index, index+4).trim();
		index += 4;//对账结果
		rejectionReason= tempString.substring(index).trim();
		
		return true;
	}



	public boolean validate(String key) {
		if (StringUtils.isEmpty(referenceNumber)) {
			RECONCILIATION_LOG.INFO(String.format("%s Batch referenceNumber=%s must not empty", key, referenceNumber));
			return false;
		}
		if (referenceNumber.length() != 8) {
			RECONCILIATION_LOG.INFO(String.format("%s Batch referenceNumber=%s len must 8", key, referenceNumber));
			return false;
		}
		if (StringUtils.isEmpty(status)) {
			RECONCILIATION_LOG.INFO(String.format("%s Batch status=%s must not empty", key, status));
			return false;
		}
//		else if (!status.equals(MessageDef.CHARGE_DEBITO_RESULT.SUCCESS)){//校验批次返回结果
//			RECONCILIATION_LOG.INFO(String.format("%s Batch status=%s unsuccess", key, status));
//			return false;
//		}
		
		if (StringUtils.isEmpty(valueDate)) {
			RECONCILIATION_LOG.INFO(String.format("%s Batch valueDate=%s must not empty", key, valueDate));
			return false;
		}
		if (StringUtils.isEmpty(currency)) {
			RECONCILIATION_LOG.INFO(String.format("%s Batch currency=%s must not empty", key, currency));
			return false;
		}

		return true;
	}

}
