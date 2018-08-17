package org.e.payment.core.pay.submit.impl;

import java.util.ArrayList;
import java.util.List;

import org.e.payment.core.pay.submit.vo.GetUserByUserNameRequest;
import org.e.payment.core.pay.submit.vo.GetUserByUserNameResponse;
import org.e.payment.core.pay.submit.vo.GetUserByUserNameResult;

import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;

public class GetUserByUserNamePayProcess extends AbsSubmitPayProcess {

	@Override
	public ResponseRestEntity<GetUserByUserNameResponse> payProcess(Object request) {

		validateRequestClass(request != null && request instanceof GetUserByUserNameRequest);
		if (isErrorResponse()) {
			return response;
		}
		List<GetUserByUserNameResult> getUserByUserNameResultList = new ArrayList<>();
		GetUserByUserNameRequest getUserByUserNameRequest = (GetUserByUserNameRequest) request;

		if (getUserByUserNameRequest.getUserName() == null || getUserByUserNameRequest.getUserName().isEmpty()) {
			return new ResponseRestEntity<GetUserByUserNameResponse>(HttpRestStatus.PAY_USER_NAME_NOTEMPTY, "用户名不能为空");

		}

		ConsumerUserClap consumerUserClapIdNumber = consumerUserClapService.selectByIdNumber(getUserByUserNameRequest.getUserName());
		if (consumerUserClapIdNumber != null) {
			ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(consumerUserClapIdNumber.getUserId());
			if (consumerUser != null) {
				if (notExit(getUserByUserNameResultList, consumerUser.getUserId())) {
					GetUserByUserNameResult getUserByUserNameResult = new GetUserByUserNameResult();
					getUserByUserNameResult.setUserId(consumerUser.getUserId());
					getUserByUserNameResult.setUserName(consumerUser.getUserName());
					getUserByUserNameResultList.add(getUserByUserNameResult);
				}
			}
		}

		ConsumerUserClap consumerUserClapClapId = consumerUserClapService.selectByClapId(getUserByUserNameRequest.getUserName());
		if (consumerUserClapClapId != null) {
			ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(consumerUserClapClapId.getUserId());
			if (consumerUser != null) {
				if (notExit(getUserByUserNameResultList, consumerUser.getUserId())) {
					GetUserByUserNameResult getUserByUserNameResult = new GetUserByUserNameResult();
					getUserByUserNameResult.setUserId(consumerUser.getUserId());
					getUserByUserNameResult.setUserName(consumerUser.getUserName());
					getUserByUserNameResultList.add(getUserByUserNameResult);
				}
			}
		}
		MerchantEmployee merchantEmployeeSer =new MerchantEmployee();
		merchantEmployeeSer.setClapStoreNo(getUserByUserNameRequest.getUserName());
		List<MerchantEmployee> merchantEmployeeList = merchantEmployeeService.seletCalpBySelective(merchantEmployeeSer);
		if (merchantEmployeeList != null&&merchantEmployeeList.size()>0&&merchantEmployeeList.get(0).getUserId()!=null) {
			MerchantUser merchantUser=merchantUserService.selectByPrimaryKey(merchantEmployeeList.get(0).getUserId());
			if(merchantUser!=null){
				if (notExit(getUserByUserNameResultList, merchantUser.getUserId())) {
					GetUserByUserNameResult getUserByUserNameResult = new GetUserByUserNameResult();
					getUserByUserNameResult.setUserId(merchantUser.getUserId());
					getUserByUserNameResult.setUserName(CommonFun.getMerchantUserName(merchantUser));
					getUserByUserNameResultList.add(getUserByUserNameResult);
				}
			}
			
		}
		GetUserByUserNameResponse getUserByUserNameResponse = new GetUserByUserNameResponse();
		getUserByUserNameResponse.setGetUserByUserNameResultList(getUserByUserNameResultList);

		return new ResponseRestEntity<GetUserByUserNameResponse>(getUserByUserNameResponse, HttpRestStatus.OK);
	}

	private boolean notExit(List<GetUserByUserNameResult> getUserByUserNameResultList, String userId) {
		if (getUserByUserNameResultList != null && getUserByUserNameResultList.size() > 0) {
			for (GetUserByUserNameResult getUserByUserNameResult : getUserByUserNameResultList) {
				if (userId.equals(getUserByUserNameResult.getUserId())) {
					return true;
				}
			}
		}
		return true;
	}
	
	
}
