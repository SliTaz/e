package com.zbensoft.e.payment.api.vo.consumer;

public class QueryConsumerBalanceVo {
	private String userName;
	private Double balance;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

}
