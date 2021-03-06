package org.e.payment.core.pay.submit.vo;

public class GetUserByUserNameRequest {
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetUserByUserNameRequest").append("=").append("[");
		sb.append("userName").append("=").append(userName).append(",");
		sb.append("]");
		return sb.toString();
	}

}
