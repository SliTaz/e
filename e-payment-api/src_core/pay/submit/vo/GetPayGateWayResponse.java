package org.e.payment.core.pay.submit.vo;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.PayGateway;

public class GetPayGateWayResponse {
	List<PayGateway> payGatewayList;
	List<ConsumerUserBankCard> consumerUserBankCardList;
	List<MerchantUserBankCard> merchantUserBankCardList;

	public List<PayGateway> getPayGatewayList() {
		return payGatewayList;
	}

	public void setPayGatewayList(List<PayGateway> payGatewayList) {
		this.payGatewayList = payGatewayList;
	}

	public List<ConsumerUserBankCard> getConsumerUserBankCardList() {
		return consumerUserBankCardList;
	}

	public void setConsumerUserBankCardList(List<ConsumerUserBankCard> consumerUserBankCardList) {
		this.consumerUserBankCardList = consumerUserBankCardList;
	}

	public List<MerchantUserBankCard> getMerchantUserBankCardList() {
		return merchantUserBankCardList;
	}

	public void setMerchantUserBankCardList(List<MerchantUserBankCard> merchantUserBankCardList) {
		this.merchantUserBankCardList = merchantUserBankCardList;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetPayGateWayRequest").append("=").append("[");
		
		sb.append("payGatewayList").append("=").append("[");
		if (payGatewayList != null && payGatewayList.size() > 0) {
			for (PayGateway payGateway : payGatewayList) {
				sb.append(payGateway.toString());
			}
		}
		sb.append("]");

		sb.append("consumerUserBankCardList").append("=").append("[");
		if (consumerUserBankCardList != null && consumerUserBankCardList.size() > 0) {
			for (ConsumerUserBankCard consumerUserBankCard : consumerUserBankCardList) {
				sb.append(consumerUserBankCard.toString());
			}
		}
		sb.append("]");

		sb.append("merchantUserBankCardList").append("=").append("[");
		if (merchantUserBankCardList != null && merchantUserBankCardList.size() > 0) {
			for (MerchantUserBankCard merchantUserBankCard : merchantUserBankCardList) {
				sb.append(merchantUserBankCard.toString());
			}
		}
		sb.append("]");
		
		sb.append("]");

		return sb.toString();
	}
}
