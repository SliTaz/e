package org.e.payment.core.pay.submit.vo;

public class SubmitTradeConsumptionAppRequest {

	private String payAppId;
	private String orderNo;// fix：123
	private String tradeType;// 交易类型4消费
	private String payUserId;// 用户编号
	// private String payUserName;
	private String payAmount;
	private String payFee;
	private String recvUserId;// 商户编号
	// private String recvUserName;
	private String recvAmount;
	private String recvFee;
	private String consumerPayPassword;// 用户支付密码
	private String consumerCouponId;
	private String consumerUserClapId;
	private String familyId;
	private String recvEmployeeId;

	// private String recvEmployeeName;

	public String getRecvEmployeeId() {
		return recvEmployeeId;
	}

	public void setRecvEmployeeId(String recvEmployeeId) {
		this.recvEmployeeId = recvEmployeeId;
	}

	// public String getRecvEmployeeName() {
	// return recvEmployeeName;
	// }

	// public void setRecvEmployeeName(String recvEmployeeName) {
	// this.recvEmployeeName = recvEmployeeName;
	// }

	public String getConsumerCouponId() {
		return consumerCouponId;
	}

	public void setConsumerCouponId(String consumerCouponId) {
		this.consumerCouponId = consumerCouponId;
	}

	public String getConsumerUserClapId() {
		return consumerUserClapId;
	}

	public void setConsumerUserClapId(String consumerUserClapId) {
		this.consumerUserClapId = consumerUserClapId;
	}

	public String getFamilyId() {
		return familyId;
	}

	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}

	public String getPayAppId() {
		return payAppId;
	}

	public void setPayAppId(String payAppId) {
		this.payAppId = payAppId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	// public String getPayUserName() {
	// return payUserName;
	// }
	//
	// public void setPayUserName(String payUserName) {
	// this.payUserName = payUserName;
	// }

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getPayFee() {
		return payFee;
	}

	public void setPayFee(String payFee) {
		this.payFee = payFee;
	}

	public String getRecvUserId() {
		return recvUserId;
	}

	public void setRecvUserId(String recvUserId) {
		this.recvUserId = recvUserId;
	}

	// public String getRecvUserName() {
	// return recvUserName;
	// }
	//
	// public void setRecvUserName(String recvUserName) {
	// this.recvUserName = recvUserName;
	// }

	public String getRecvAmount() {
		return recvAmount;
	}

	public void setRecvAmount(String recvAmount) {
		this.recvAmount = recvAmount;
	}

	public String getRecvFee() {
		return recvFee;
	}

	public void setRecvFee(String recvFee) {
		this.recvFee = recvFee;
	}

	public String getConsumerPayPassword() {
		return consumerPayPassword;
	}

	public void setConsumerPayPassword(String consumerPayPassword) {
		this.consumerPayPassword = consumerPayPassword;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SubmitTradeConsumptionAppRequest").append("=").append("[");
		sb.append("payAppId").append("=").append(payAppId).append(",");
		sb.append("orderNo").append("=").append(orderNo).append(",");
		sb.append("tradeType").append("=").append(tradeType).append(",");
		sb.append("payUserId").append("=").append(payUserId).append(",");
		sb.append("payAmount").append("=").append(payAmount).append(",");
		sb.append("payFee").append("=").append(payFee).append(",");
		sb.append("recvUserId").append("=").append(recvUserId).append(",");
		sb.append("recvAmount").append("=").append(recvAmount).append(",");
		sb.append("recvFee").append("=").append(recvFee).append(",");
//		sb.append("consumerayPassword").append("=").append(consumerayPassword).append(",");
		sb.append("consumerCouponId").append("=").append(consumerCouponId).append(",");
		sb.append("consumerUserClapId").append("=").append(consumerUserClapId).append(",");
		sb.append("familyId").append("=").append(familyId).append(",");
		sb.append("recvEmployeeId").append("=").append(recvEmployeeId).append(",");
		sb.append("]");
		return sb.toString();
	}
}
