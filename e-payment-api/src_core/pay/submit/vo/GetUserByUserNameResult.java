package org.e.payment.core.pay.submit.vo;

public class GetUserByUserNameResult {
	private String userId;
	private String userName;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetUserByUserNameResult").append("=").append("[");
		sb.append("userId").append("=").append(userId).append(",");
		sb.append("userName").append("=").append(userName).append(",");
		sb.append("]");
		return sb.toString();
	}
}
