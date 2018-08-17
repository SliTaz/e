package org.e.payment.core.pay.submit.impl.submit.allBank;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferBankRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferBankResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * epay转账到银行卡
 * 
 * @author xieqiang
 *
 */
public class AllBankSubmitTransferBankPayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankSubmitTransferBankPayProcess.class);

	@Override
	public ResponseRestEntity<SubmitTradeTransferBankResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof SubmitTradeTransferBankRequest);
		if (isErrorResponse()) {
			return response;
		}

		SubmitTradeTransferBankRequest submitTradeTransferBankRequest = (SubmitTradeTransferBankRequest) request;

		validateExistOrderNo(submitTradeTransferBankRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		Object payUser = validatePayUser(submitTradeTransferBankRequest.getPayUserId());

		if (payUser instanceof ConsumerUser) {
			ConsumerUser payUserConsumerUser = (ConsumerUser) payUser;
			validatePayConsumerStatue(payUserConsumerUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserConsumerUser.getUserId();
			payUserName = payUserConsumerUser.getUserName();

			validatePayPassword(payUserConsumerUser.getUserId(), payUserConsumerUser.getPayPassword(), submitTradeTransferBankRequest.getPayPassword());
			if (isErrorResponse()) {
				return response;
			}
		}

		if (payUser instanceof MerchantUser) {
			MerchantUser payUserMerchantUser = (MerchantUser) payUser;

			validatePayMerchantStatue(payUserMerchantUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserMerchantUser.getUserId();
			payUserName = CommonFun.getMerchantUserName(payUserMerchantUser);

			validatePayPassword(payUserMerchantUser.getUserId(), payUserMerchantUser.getPayPassword(), submitTradeTransferBankRequest.getPayPassword());
			if (isErrorResponse()) {
				return response;
			}
		}

		// payAppId
		validatePayAppId(submitTradeTransferBankRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}
		PayGateway payGateway = validatePayGateway(submitTradeTransferBankRequest.getPayAppId(), null, MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}
		// userBankBind
		Object userBankBind = validateUserBankBind(payUserId, submitTradeTransferBankRequest.getBankBindId());
		PayGateway recvGateway = validatePayGateway(submitTradeTransferBankRequest.getPayAppId(), userBankBind, MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_CHARGE);
		if (isErrorResponse()) {
			return response;
		}

		// orderNo
		validateOrderNo(submitTradeTransferBankRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(submitTradeTransferBankRequest.getTradeType(), MessageDef.TRADE_TYPE.TRANSFER_BANK);
		if (isErrorResponse()) {
			return response;
		}

		// amount
		validatePayAmount(submitTradeTransferBankRequest.getPayAmount());
		if (isErrorResponse()) {
			return response;
		}

		validatePayFee(submitTradeTransferBankRequest.getPayFee());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvAmount(submitTradeTransferBankRequest.getRecvAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvFee(submitTradeTransferBankRequest.getRecvFee());
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(submitTradeTransferBankRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(submitTradeTransferBankRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.transfer.bank"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(submitTradeTransferBankRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayBankId(null);
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(Double.valueOf(submitTradeTransferBankRequest.getPayAmount()));
		submitTradeInfo.setPayFee(0d);
		submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
		submitTradeInfo.setPayStartMoney(null);
		submitTradeInfo.setPayEndMoney(null);
		submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
		submitTradeInfo.setRecvUserId(recvUserId);
		submitTradeInfo.setRecvUserName(recvUserName);
		submitTradeInfo.setRecvBankId(recvGateway.getPayGatewayId());
		submitTradeInfo.setRecvBankName(null);
		if (userBankBind instanceof ConsumerUserBankCard) {
			submitTradeInfo.setRecvBankCardNo(((ConsumerUserBankCard) userBankBind).getCardNo());
		}
		if (userBankBind instanceof MerchantUserBankCard) {
			submitTradeInfo.setRecvBankCardNo(((MerchantUserBankCard) userBankBind).getCardNo());
		}
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(recvGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(Double.valueOf(submitTradeTransferBankRequest.getRecvAmount()));
		submitTradeInfo.setRecvFee(0d);
		submitTradeInfo.setRecvSumAmount(DoubleUtil.add(submitTradeInfo.getRecvAmount(), submitTradeInfo.getRecvFee()));
		submitTradeInfo.setRecvStartMoney(null);
		submitTradeInfo.setRecvEndMoney(null);
		submitTradeInfo.setRecvBorrowLoanFlag(MessageDef.BORROW_LOAN.LOAN);
		submitTradeInfo.setCallbackUrl(null);
		submitTradeInfo.setCreateTime(now);
		submitTradeInfo.setPayTime(null);
		submitTradeInfo.setEndTime(null);
		submitTradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
		submitTradeInfo.setRemark(null);

		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

		try {
			tradeInfoService.insert(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AllBankSubmitTransferBankPayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<SubmitTradeTransferBankResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		SubmitTradeTransferBankResponse submitTradeTransferBankResponse = new SubmitTradeTransferBankResponse();
		submitTradeTransferBankResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		return new ResponseRestEntity<SubmitTradeTransferBankResponse>(submitTradeTransferBankResponse, HttpRestStatus.OK);
	}
	
	


	

}
