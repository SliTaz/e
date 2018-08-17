package org.e.payment.core.pay.submit.vo;

import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.db.domain.TradeInfo;

public class ErrorHandlingBankErrorRequest {

	private String payAppId = "10005";

	private String refNo;
	private String vid;
	private String transactionDate;
	private String currencyType;
	private String transactionAmount;
	private String transactionType;
	private String channelType;
	private String bankId;
	private TradeInfo tradeInfo;

	public TradeInfo getTradeInfo() {
		return tradeInfo;
	}

	public void setTradeInfo(TradeInfo tradeInfo) {
		this.tradeInfo = tradeInfo;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getPayAppId() {
		return payAppId;
	}

	public void setPayAppId(String payAppId) {
		this.payAppId = payAppId;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("ErrorHandlingRechargeRequest").append("=").append("[");
		if (StringUtils.isNotEmpty(refNo)) {
			sb.append("refNo").append("=").append(refNo).append(",");
		}
		if (StringUtils.isNotEmpty(vid)) {
			sb.append("vid").append("=").append(vid).append(",");
		}
		if (StringUtils.isNotEmpty(transactionDate)) {
			sb.append("transactionDate").append("=").append(transactionDate).append(",");
		}
		if (StringUtils.isNotEmpty(currencyType)) {
			sb.append("currencyType").append("=").append(currencyType).append(",");
		}
		if (StringUtils.isNotEmpty(transactionAmount)) {
			sb.append("transactionAmount").append("=").append(transactionAmount).append(",");
		}
		if (StringUtils.isNotEmpty(transactionType)) {
			sb.append("transactionType").append("=").append(transactionType).append(",");
		}
		if (StringUtils.isNotEmpty(channelType)) {
			sb.append("channelType").append("=").append(channelType).append(",");
		}
		if (StringUtils.isNotEmpty(bankId)) {
			sb.append("bankId").append("=").append(bankId).append(",");
		}
		
		sb.append("]");
		return sb.toString();
	}

}
