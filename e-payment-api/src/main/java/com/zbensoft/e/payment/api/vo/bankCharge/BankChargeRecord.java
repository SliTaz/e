package com.zbensoft.e.payment.api.vo.bankCharge;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.db.domain.BankChargeInfo;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class BankChargeRecord {
	
	public BankChargeRecord(){
		
	}
	
	
	public BankChargeRecord(TradeInfo tradeInfo) {
		if(tradeInfo!=null){
		}
		
		
	}


	private String identifierRegistry;
	private String referenceNumber;
	private String rif_ci;
	private String name;
	private String accountType;
	private String accountNumber;
	private String amount;
	private String paymentType;
	private String bankCode;
	private String durationOfCheck;
	private String agencyBanking;
	private String eMail;
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


	public String getRif_ci() {
		return rif_ci;
	}


	public void setRif_ci(String rif_ci) {
		this.rif_ci = rif_ci;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getBankCode() {
		return bankCode;
	}


	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}


	public String getDurationOfCheck() {
		return durationOfCheck;
	}


	public void setDurationOfCheck(String durationOfCheck) {
		this.durationOfCheck = durationOfCheck;
	}


	public String getAgencyBanking() {
		return agencyBanking;
	}


	public void setAgencyBanking(String agencyBanking) {
		this.agencyBanking = agencyBanking;
	}


	public String geteMail() {
		return eMail;
	}


	public void seteMail(String eMail) {
		this.eMail = eMail;
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
		sb.append(referenceNumber);//referenceNumber of record
		sb.append(rif_ci);//VID
		sb.append(name);//Receiver name
		sb.append(accountType);//account Type
		sb.append(accountNumber);//account Number
		sb.append(amount);//Amount 
		sb.append(paymentType);//paymentType
		sb.append(bankCode);//bank Code
		sb.append(durationOfCheck);//durationOfCheck
		sb.append(agencyBanking);//agencyBanking
		sb.append(eMail);//eMail
		return sb.toString();
	}


	public boolean decode(String tempString, String key) {
		if (StringUtils.isEmpty(tempString)) {
			TASK_LOG.INFO(String.format("%s read one Batch is empty", key));
			return false;
		}
		if (tempString.length() != 251) {
			TASK_LOG.INFO(String.format("%s read one Batch length not 251, only %d, %s", key,tempString.length(), tempString));
			return false;
		}
		int index = 0;
		
		identifierRegistry= tempString.substring(index, 8).trim();
		index += 8;
		referenceNumber= tempString.substring(index, index+8).trim();
		index += 8;
		rif_ci= tempString.substring(index, index+10).trim();
		index += 10;
		name= tempString.substring(index, index+30).trim();
		index += 30;
		accountType= tempString.substring(index, index+2).trim();
		index += 2;
		accountNumber= tempString.substring(index, index+20).trim();
		index += 20;
		amount= tempString.substring(index, index+18).trim();
		index += 18;
		paymentType= tempString.substring(index, index+2).trim();
		index += 2;
		bankCode= tempString.substring(index, index+12).trim();
		index += 12;
		durationOfCheck= tempString.substring(index, index+3).trim();
		index += 3;
		agencyBanking= tempString.substring(index, index+4).trim();
		index += 4;
		eMail= tempString.substring(index, index+50).trim();
		index += 50;
		status= tempString.substring(index, index+4).trim();
		index += 4;//对账结果
		rejectionReason= tempString.substring(index).trim();
		
		return true;
	}


	public boolean validate(String key) {
		if (StringUtils.isEmpty(referenceNumber)) {
			RECONCILIATION_LOG.INFO(String.format("%s referenceNumber=%s must not empty", key, referenceNumber));
			return false;
		}
		if (referenceNumber.length() != 8) {
			RECONCILIATION_LOG.INFO(String.format("%s referenceNumber=%s len must 8", key, referenceNumber));
			return false;
		}
		if (StringUtils.isEmpty(status)) {
			RECONCILIATION_LOG.INFO(String.format("%s status=%s must not empty", key, status));
			return false;
		}
		
		if (StringUtils.isEmpty(rif_ci)) {
			RECONCILIATION_LOG.INFO(String.format("%s rif_ci=%s must not empty", key, rif_ci));
			return false;
		}
		if (StringUtils.isEmpty(amount)) {
			RECONCILIATION_LOG.INFO(String.format("%s amount=%s must not empty", key, amount));
			return false;
		}
		if (StringUtils.isEmpty(paymentType)) {
			RECONCILIATION_LOG.INFO(String.format("%s paymentType=%s must not empty", key,paymentType));
			return false;
		}
		return true;
	}


	public BankChargeInfo setBankChargeInfo() {
		BankChargeInfo bankChargeInfo =new BankChargeInfo();
		bankChargeInfo.setVid(rif_ci);
		bankChargeInfo.setRefNo(referenceNumber);
		bankChargeInfo.setChargeAmount(amount);
		bankChargeInfo.setChargeResult(status);//提现结果
		return bankChargeInfo;
	}

}
