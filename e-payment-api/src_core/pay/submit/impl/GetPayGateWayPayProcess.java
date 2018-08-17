package org.e.payment.core.pay.submit.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.submit.vo.GetPayGateWayRequest;
import org.e.payment.core.pay.submit.vo.GetPayGateWayResponse;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.factory.BankInfoFactory;
import com.zbensoft.e.payment.api.factory.PayAppGatewayFactory;
import com.zbensoft.e.payment.api.factory.PayGatewayFactory;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUserStoreType;
import com.zbensoft.e.payment.db.domain.PayAppGateway;
import com.zbensoft.e.payment.db.domain.PayGateway;

public class GetPayGateWayPayProcess extends AbsSubmitPayProcess {

	@Override
	public ResponseRestEntity<GetPayGateWayResponse> payProcess(Object request) {

		validateRequestClass(request != null && request instanceof GetPayGateWayRequest);
		if (isErrorResponse()) {
			return response;
		}

		GetPayGateWayRequest getPayGateWayRequest = (GetPayGateWayRequest) request;

		if (getPayGateWayRequest.getPayAppId() == null || getPayGateWayRequest.getPayAppId().isEmpty()) {
			return new ResponseRestEntity<GetPayGateWayResponse>(HttpRestStatus.PAY_APP_ID_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.payapp.no.notempty"));

		}
		List<PayAppGateway> payAppGatewayList = PayAppGatewayFactory.getInstance().getByAppId(getPayGateWayRequest.getPayAppId());// payAppGatewayService.selectByPayAppId(getPayGateWayRequest.getPayAppId());
		if (payAppGatewayList == null || payAppGatewayList.size() == 0) {
			return new ResponseRestEntity<GetPayGateWayResponse>(HttpRestStatus.PAY_APP_ID_NOEXIST);
		}

		Object payUser = getUserByUserId(getPayGateWayRequest.getPayUserId());
		if (payUser == null) {
			return new ResponseRestEntity<GetPayGateWayResponse>(HttpRestStatus.PAY_PAY_USER_NOEXIST);
		}

		GetPayGateWayResponse getPayGateWayResponse = new GetPayGateWayResponse();
		if (payUser instanceof ConsumerUser) {

			List<ConsumerUserBankCard> consumerUserBankCardList = consumerUserBankCardService.selectByUserId(((ConsumerUser) payUser).getUserId());
			for (ConsumerUserBankCard consumerUserBankCard : consumerUserBankCardList) {
				if(consumerUserBankCard!=null&&consumerUserBankCard.getBankId()!=null){
					BankInfo bankInfo=BankInfoFactory.getInstance().get(consumerUserBankCard.getBankId());
					if(bankInfo!=null&&bankInfo.getName()!=null){
						consumerUserBankCard.setBankName(bankInfo.getName());
					}
					
				}
			}
			getPayGateWayResponse.setConsumerUserBankCardList(consumerUserBankCardList);
		}

		if (payUser instanceof MerchantUser) {
			String payUserId=((MerchantUser) payUser).getUserId();
//			List<MerchantUserStoreType> storeTypeList=merchantUserStoreTypeService.selectByUserId(payUserId);
//			if(storeTypeList!=null&&storeTypeList.size()==1){
//				if(storeTypeList.get(0)!=null&&StringUtils.isNotEmpty(storeTypeList.get(0).getHeadFficeId())){
//					payUserId=storeTypeList.get(0).getHeadFficeId();
//				}
//			}

			List<MerchantUserBankCard> merchantUserBankCardList = merchantUserBankCardService.selectByUserId(payUserId);
			for (MerchantUserBankCard merchantUserBankCard : merchantUserBankCardList) {
				if(merchantUserBankCard!=null&&merchantUserBankCard.getBankId()!=null){
					BankInfo bankInfo=BankInfoFactory.getInstance().get(merchantUserBankCard.getBankId());
					if(bankInfo!=null&&bankInfo.getName()!=null){
						merchantUserBankCard.setBankName(bankInfo.getName());
					}
					
				}
			}
			getPayGateWayResponse.setMerchantUserBankCardList(merchantUserBankCardList);
		}

		List<PayGateway> payGatewayList = new ArrayList<>();
		for (PayAppGateway payAppGateway : payAppGatewayList) {
			PayGateway payGateway = PayGatewayFactory.getInstance().getById(payAppGateway.getPayGatewayId());// payGatewayService.selectByPrimaryKey(payAppGateway.getPayGatewayId());
			if (payGateway != null) {
				payGatewayList.add(payGateway);
			}
		}
		getPayGateWayResponse.setPayGatewayList(payGatewayList);

		return new ResponseRestEntity<GetPayGateWayResponse>(getPayGateWayResponse, HttpRestStatus.OK);

	}

}
