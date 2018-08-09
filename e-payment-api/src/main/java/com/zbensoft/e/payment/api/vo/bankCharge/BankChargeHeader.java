package com.zbensoft.e.payment.api.vo.bankCharge;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;

public class BankChargeHeader {
	
	public BankChargeHeader(){
		
	}
	
	
	private String identifierRegistry;
	private String referenceNumber;
	private String numberNegotiation;
	private String rif;
	private String dateOfPaymen;
	private String dateOfShipment;
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



	public String getNumberNegotiation() {
		return numberNegotiation;
	}



	public void setNumberNegotiation(String numberNegotiation) {
		this.numberNegotiation = numberNegotiation;
	}



	public String getRif() {
		return rif;
	}



	public void setRif(String rif) {
		this.rif = rif;
	}



	public String getDateOfPaymen() {
		return dateOfPaymen;
	}



	public void setDateOfPaymen(String dateOfPaymen) {
		this.dateOfPaymen = dateOfPaymen;
	}



	public String getDateOfShipment() {
		return dateOfShipment;
	}



	public void setDateOfShipment(String dateOfShipment) {
		this.dateOfShipment = dateOfShipment;
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
		sb.append(identifierRegistry);//Identifier registry
		sb.append(referenceNumber);//Lot reference number
		sb.append(numberNegotiation);//Number negotiation
		sb.append(rif);//R.I.F of the payer company
		sb.append(dateOfPaymen);//Date of paymen
		sb.append(dateOfShipment);//Date of shipment
		
		return sb.toString();
	}
	
	
	public boolean decode(String tempString,String key) {
		if (StringUtils.isEmpty(tempString)) {
			TASK_LOG.INFO(String.format("%s read head is empty", key));
			return false;
		}
		if (tempString.length() != 138) {
			TASK_LOG.INFO(String.format("%s read head length not 138, only %d,%s", key, tempString.length(),tempString));
			return false;
		}
		int index = 0;
		
		identifierRegistry= tempString.substring(index, 8).trim();
		index += 8;
		referenceNumber= tempString.substring(index, index+8).trim();
		index += 8;
		numberNegotiation= tempString.substring(index, index+8).trim();
		index += 8;
		rif= tempString.substring(index, index+10).trim();
		index += 10;
		dateOfPaymen= tempString.substring(index, index+10).trim();
		index += 10;
		dateOfShipment= tempString.substring(index, index+10).trim();
		index += 10;
		status= tempString.substring(index, index+4).trim();
		index += 4;//对账结果
		rejectionReason= tempString.substring(index).trim();
		
		return true;
	}
	
	public static void main(String[] args) {
		BankChargeHeader BCH=new BankChargeHeader();
		BCH.decode("HEADER  2017091200004842J00124134513/09/201713/09/2017VL3 ORA-01722: invalid number", "11");
		
		
	}
	
	

	public boolean validate(String key) {
		if (StringUtils.isEmpty(referenceNumber)) {
			RECONCILIATION_LOG.INFO(String.format("%s reconciliationDate=%s must not empty", key, referenceNumber));
			return false;
		}
		if (referenceNumber.length() != 8) {
			RECONCILIATION_LOG.INFO(String.format("%s reconciliationDate=%s len must 8", key, referenceNumber));
			return false;
		}
		if (StringUtils.isEmpty(status)) {
			RECONCILIATION_LOG.INFO(String.format("%s bankId=%s must not empty", key, status));
			return false;
		}
		if (StringUtils.isEmpty(dateOfShipment)) {
			RECONCILIATION_LOG.INFO(String.format("%s recordsNumber=%s must not empty", key, dateOfShipment));
			return false;
		}

		return true;
	}

}
