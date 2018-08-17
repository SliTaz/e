package org.e.payment.core.pay.submit.vo;

import java.util.List;

public class GetUserByUserNameResponse {
	List<GetUserByUserNameResult> getUserByUserNameResultList;

	public List<GetUserByUserNameResult> getGetUserByUserNameResultList() {
		return getUserByUserNameResultList;
	}

	public void setGetUserByUserNameResultList(List<GetUserByUserNameResult> getUserByUserNameResultList) {
		this.getUserByUserNameResultList = getUserByUserNameResultList;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetUserByUserNameResponse").append("=").append("[");

		sb.append("getUserByUserNameResultList").append("=").append("[");
		if (getUserByUserNameResultList != null && getUserByUserNameResultList.size() > 0) {
			for (GetUserByUserNameResult getUserByUserNameResult : getUserByUserNameResultList) {
				sb.append(getUserByUserNameResult.toString());
			}
		}
		sb.append("]");

		sb.append("]");

		return sb.toString();
	}
}
