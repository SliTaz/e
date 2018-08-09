package com.zbensoft.e.payment.api.vo.clap;

import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class ClapTrade {
	
	public ClapTrade(){
		
	}
	
	
	
	public ClapTrade(TradeInfo tradeInfo) {
		if(tradeInfo!=null){
			if(tradeInfo.getTradeSeq()!=null){
				transactionCode=tradeInfo.getTradeSeq();
			}
			if(tradeInfo.getType()!=null){
				transactionType=String.valueOf(tradeInfo.getType());
			}
			if(tradeInfo.getCouponId()!=null){
				uniqueCode=tradeInfo.getCouponId();
			}
			if(tradeInfo.getCreateTime()!=null){
				transactionTime=DateUtil.convertDateToString(tradeInfo.getCreateTime(), DateUtil.DATE_FORMAT_FIVE);
			}
		}
		
		
	}



	private String transactionCode;
	private String patrimonyCardId;//y
	private String buyerVID;//y
	private String transactionType;
	private String clapStoreNo;//y
	private String sellerVID;//y
	private String uniqueCode;
	private String amount;//y
	private String transactionTime;
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getPatrimonyCardId() {
		return patrimonyCardId;
	}
	public void setPatrimonyCardId(String patrimonyCardId) {
		this.patrimonyCardId = patrimonyCardId;
	}
	public String getBuyerVID() {
		return buyerVID;
	}
	public void setBuyerVID(String buyerVID) {
		this.buyerVID = buyerVID;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getClapStoreNo() {
		return clapStoreNo;
	}
	public void setClapStoreNo(String clapStoreNo) {
		this.clapStoreNo = clapStoreNo;
	}
	public String getSellerVID() {
		return sellerVID;
	}
	public void setSellerVID(String sellerVID) {
		this.sellerVID = sellerVID;
	}
	public String getUniqueCode() {
		return uniqueCode;
	}
	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	
	
	@Override
	public String toString() {
		
		StringBuffer sb=new StringBuffer();
		sb.append(transactionCode).append(",");
		sb.append(patrimonyCardId).append(",");
		sb.append(buyerVID).append(",");
		sb.append(transactionType).append(",");
		sb.append(clapStoreNo).append(",");
		sb.append(sellerVID).append(",");
		sb.append(uniqueCode).append(",");
		sb.append(amount).append(",");
		sb.append(transactionTime);
		return sb.toString();
	}
	
	

}
