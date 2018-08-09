package com.zbensoft.e.payment.api.vo.clap;

public class ClapTradeHeader {
	
	public ClapTradeHeader(){
		
	}
	
	
	private String generateFileDate;
	private String totalPackages;
	private String totalAmount;
	
	public String getGenerateFileDate() {
		return generateFileDate;
	}
	public void setGenerateFileDate(String generateFileDate) {
		this.generateFileDate = generateFileDate;
	}
	public String getTotalPackages() {
		return totalPackages;
	}
	public void setTotalPackages(String totalPackages) {
		this.totalPackages = totalPackages;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
	@Override
	public String toString() {
		
		StringBuffer sb=new StringBuffer();
		sb.append(generateFileDate).append(",");
		sb.append(totalPackages).append(",");
		sb.append(totalAmount);
		
		return sb.toString();
	}

}
