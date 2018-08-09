package com.zbensoft.e.payment.api.vo.errorHandling;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.db.domain.BankChargeInfo;
import com.zbensoft.e.payment.db.domain.BankTradeInfo;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class ErrorHandlingChargeVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TradeInfo tradeInfo;
	private BankChargeInfo bankChargeInfo;
	private String bankId;
	private String reconciliationTime;

	public String getReconciliationTime() {
		return reconciliationTime;
	}

	public void setReconciliationTime(String reconciliationTime) {
		this.reconciliationTime = reconciliationTime;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public TradeInfo getTradeInfo() {
		return tradeInfo;
	}

	public void setTradeInfo(TradeInfo tradeInfo) {
		this.tradeInfo = tradeInfo;
	}


	public BankChargeInfo getBankChargeInfo() {
		return bankChargeInfo;
	}

	public void setBankChargeInfo(BankChargeInfo bankChargeInfo) {
		this.bankChargeInfo = bankChargeInfo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("ErrorHandlingVo").append("=").append("[");
		if (tradeInfo != null) {
			sb.append(tradeInfo.toString()).append(",");
		}
		if (bankChargeInfo != null) {
			sb.append(bankChargeInfo.toString()).append(",");
		}
		if (StringUtils.isNotEmpty(bankId)) {
			sb.append("bankId").append("=").append(bankId).append(",");
		}

		sb.append("]");
		return sb.toString();
	}
}
