package org.e.payment.core.pay.submit.vo;

import java.io.Serializable;

public class UserAccountAmountReids implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private Double amount;
	private Double dbAmount;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDbAmount() {
		return dbAmount;
	}

	public void setDbAmount(Double dbAmount) {
		this.dbAmount = dbAmount;
	}

}
