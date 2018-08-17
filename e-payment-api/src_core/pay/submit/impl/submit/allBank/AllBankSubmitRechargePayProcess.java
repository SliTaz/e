package org.e.payment.core.pay.submit.impl.submit.allBank;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.factory.BankInfoFactory;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * epay充值
 * 
 * @author xieqiang
 *
 */
public class AllBankSubmitRechargePayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankSubmitRechargePayProcess.class);

	@Override
	public ResponseRestEntity<SubmitTradeRechargeResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof SubmitTradeRechargeRequest);
		if (isErrorResponse()) {
			return response;
		}

		SubmitTradeRechargeRequest submitTradeRechargeRequest = (SubmitTradeRechargeRequest) request;

		validateExistOrderNo(submitTradeRechargeRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		Object payUser = validatePayUser(submitTradeRechargeRequest.getPayUserId());

		if (payUser instanceof ConsumerUser) {
			ConsumerUser payUserConsumerUser = (ConsumerUser) payUser;
			validatePayConsumerStatue(payUserConsumerUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserConsumerUser.getUserId();
			payUserName = payUserConsumerUser.getUserName();

			validatePayPassword(payUserConsumerUser.getUserId(), payUserConsumerUser.getPayPassword(), submitTradeRechargeRequest.getPayPassword());
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

			validatePayPassword(payUserMerchantUser.getUserId(), payUserMerchantUser.getPayPassword(), submitTradeRechargeRequest.getPayPassword());
			if (isErrorResponse()) {
				return response;
			}
		}
		Object recvUser = validateRecvUser(submitTradeRechargeRequest.getRecvUserId());

		if (recvUser instanceof ConsumerUser) {
			ConsumerUser recvUserConsumerUser = (ConsumerUser) recvUser;

			validateRecvConsumerStatue(recvUserConsumerUser);
			if (isErrorResponse()) {
				return response;
			}
			recvUserId = recvUserConsumerUser.getUserId();
			recvUserName = recvUserConsumerUser.getUserName();
		}

		if (recvUser instanceof MerchantUser) {
			MerchantUser recvUserrMerchantUser = (MerchantUser) recvUser;

			validateRecvMerchantStatue(recvUserrMerchantUser);
			if (isErrorResponse()) {
				return response;
			}
			recvUserId = recvUserrMerchantUser.getUserId();
			recvUserName = CommonFun.getMerchantUserName(recvUserrMerchantUser);
		}

		// payAppId
		validatePayAppId(submitTradeRechargeRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		// userBankBind
		Object userBankBind = validateUserBankBind(payUserId, submitTradeRechargeRequest.getBankBindId());
		if (isErrorResponse()) {
			return response;
		}
		PayGateway payGateway = validatePayGateway(submitTradeRechargeRequest.getPayAppId(), userBankBind, MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_RECHARGE);
		if (isErrorResponse()) {
			return response;
		}
		PayGateway recvGateway = validatePayGateway(submitTradeRechargeRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}

		// orderNo
		validateOrderNo(submitTradeRechargeRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(submitTradeRechargeRequest.getTradeType(), MessageDef.TRADE_TYPE.RECHARGE);
		if (isErrorResponse()) {
			return response;
		}

		// amount
		validatePayAmount(submitTradeRechargeRequest.getPayAmount());
		if (isErrorResponse()) {
			return response;
		}

		validatePayFee(submitTradeRechargeRequest.getPayFee());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvAmount(submitTradeRechargeRequest.getRecvAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvFee(submitTradeRechargeRequest.getRecvFee());
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(submitTradeRechargeRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(submitTradeRechargeRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.recharge"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(submitTradeRechargeRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayEmployeeUserId(null);
		submitTradeInfo.setPayEmployeeUserName(null);
		submitTradeInfo.setPayBankId(payGateway.getBankId());
		submitTradeInfo.setPayBankName(null);
		if (userBankBind instanceof ConsumerUserBankCard) {
			submitTradeInfo.setPayBankCardNo(((ConsumerUserBankCard) userBankBind).getCardNo());
		}
		if (userBankBind instanceof MerchantUserBankCard) {
			submitTradeInfo.setPayBankCardNo(((MerchantUserBankCard) userBankBind).getCardNo());
		}
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(Double.valueOf(submitTradeRechargeRequest.getPayAmount()));
		submitTradeInfo.setPayFee(0d);
		submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
		submitTradeInfo.setPayStartMoney(null);
		submitTradeInfo.setPayEndMoney(null);
		submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
		submitTradeInfo.setRecvUserId(recvUserId);
		submitTradeInfo.setRecvUserName(recvUserName);
		submitTradeInfo.setRecvEmployeeUserId(null);
		submitTradeInfo.setRecvEmployeeUserName(null);
		submitTradeInfo.setRecvGatewayId(recvGateway.getPayGatewayId());
		submitTradeInfo.setRecvBankId(null);
		submitTradeInfo.setRecvBankName(null);
		submitTradeInfo.setRecvBankCardNo(null);
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(recvGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(Double.valueOf(submitTradeRechargeRequest.getRecvAmount()));
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

		fraudParam.put("ip_address", "");
		fraudParam.put("bank_card_pay", submitTradeInfo.getPayBankCardNo());
		fraudParam.put("bank_card_recv", submitTradeInfo.getRecvBankCardNo());
		fraudParam.put("user_id_pay", submitTradeInfo.getPayUserId());
		fraudParam.put("user_id_recv", submitTradeInfo.getRecvUserId());

		processPayFee(submitTradeInfo);
		processRecvFee(submitTradeInfo);

		fraudProcess(fraudParam);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}

		processPayUserAccount(submitTradeInfo);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}
		processRecvUserAccount(submitTradeInfo);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}

		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

		try {
			tradeInfoService.insert(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AllBankSubmitRechargePayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<SubmitTradeRechargeResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		SubmitTradeRechargeResponse submitTradeRechargeResponse = new SubmitTradeRechargeResponse();
		submitTradeRechargeResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		return new ResponseRestEntity<SubmitTradeRechargeResponse>(submitTradeRechargeResponse, HttpRestStatus.OK);
	}
	

	

}
