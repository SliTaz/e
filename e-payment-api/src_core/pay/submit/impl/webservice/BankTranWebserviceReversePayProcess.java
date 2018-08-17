package org.e.payment.core.pay.submit.impl.webservice;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceReverseRequest;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceReverseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.api.log.SUBMIT_LOG;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class BankTranWebserviceReversePayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(BankTranWebserviceReversePayProcess.class);

	@Override
	public ResponseRestEntity<BankTranWebserviceReverseResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof BankTranWebserviceReverseRequest);
		if (isErrorResponse()) {
			return response;
		}

		BankTranWebserviceReverseRequest bankTranWebservcieReverseRequest = (BankTranWebserviceReverseRequest) request;

		validateExistOrderNo(bankTranWebservcieReverseRequest.getBankId(), bankTranWebservcieReverseRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		// payAppId
		validatePayAppId(bankTranWebservcieReverseRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		// orderNo
		validateOrderNo(bankTranWebservcieReverseRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(bankTranWebservcieReverseRequest.getTradeType(), MessageDef.TRADE_TYPE.BANK_REVERSE);
		if (isErrorResponse()) {
			return response;
		}

		// PayGateway recvGateway = validateWebserviceIpaddress(bankTranWebservcieReverseRequest.getIpAddress());
		// if (isErrorResponse()) {
		// return response;
		// }

		PayGateway payGateway = validatePayGateway(bankTranWebservcieReverseRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}

		PayGateway recvGateway = validateWebserviceBankId(bankTranWebservcieReverseRequest.getPayAppId(), bankTranWebservcieReverseRequest.getBankId(), MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_REVERSE);
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo tradeInfoReverseRefNo = validateWebserviceReverseRefNo(bankTranWebservcieReverseRequest.getBankId(), bankTranWebservcieReverseRequest.getReverseRefNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		Object payUser = validatePayUser(tradeInfoReverseRefNo.getPayUserId());
		if (isErrorResponse()) {
			return response;
		}

		if (payUser instanceof ConsumerUser) {
			ConsumerUser payUserConsumerUser = (ConsumerUser) payUser;
			validatePayConsumerStatueWebservice(payUserConsumerUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserConsumerUser.getUserId();
			payUserName = payUserConsumerUser.getUserName();
			recvUserId = payUserConsumerUser.getUserId();
			recvUserName = payUserConsumerUser.getUserName();
		}

		if (payUser instanceof MerchantUser) {
			MerchantUser payUserMerchantUser = (MerchantUser) payUser;

			validatePayMerchantStatueWebservice(payUserMerchantUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserMerchantUser.getUserId();
			payUserName = CommonFun.getMerchantUserName(payUserMerchantUser);
			recvUserId = payUserMerchantUser.getUserId();
			recvUserName = CommonFun.getMerchantUserName(payUserMerchantUser);

		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(bankTranWebservcieReverseRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(bankTranWebservcieReverseRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.recharge"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(bankTranWebservcieReverseRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayEmployeeUserId(null);
		submitTradeInfo.setPayEmployeeUserName(null);
		submitTradeInfo.setPayBankId(null);
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(tradeInfoReverseRefNo.getPaySumAmount());
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
		submitTradeInfo.setRecvBankId(recvGateway.getBankId());
		submitTradeInfo.setRecvBankName(null);
		submitTradeInfo.setRecvBankCardNo(null);
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(recvGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(tradeInfoReverseRefNo.getRecvSumAmount());
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

		processPayFee(submitTradeInfo);
		processPayUserAccount(submitTradeInfo);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}

		submitTradeInfo.setPayTime(Calendar.getInstance().getTime());
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());
		submitTradeInfo.setParentTradeSeq(tradeInfoReverseRefNo.getTradeSeq());

		tradeInfoReverseRefNo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.YES);

		try {
			tradeInfoService.updateByPrimaryKeySelective(tradeInfoReverseRefNo);
		} catch (Exception e) {
			log.error(String.format("BankTranWebserviceReversePayProcess update failed", tradeInfoReverseRefNo.toString()), e);
			SUBMIT_LOG.INFO(String.format("BankTranWebserviceReversePayProcess update failed", tradeInfoReverseRefNo.toString()));
			// return new ResponseRestEntity<BankTranWebserviceRechargeResponse>(HttpRestStatus.PAY_UPDATE_ERROR, "更新失败");
		}

		try {
			tradeInfoService.insert(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "BankTranWebserviceReversePayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<BankTranWebserviceReverseResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		BankTranWebserviceReverseResponse bankTranWebserviceReverseResponse = new BankTranWebserviceReverseResponse();
		bankTranWebserviceReverseResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(submitTradeInfo.getPayUserId());
		if (consumerUserClap != null) {
			StatisticsUtil.addStatisticsSuccLimitMonth(RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE, submitTradeInfo.getCreateTime(), recvUserId, DoubleUtil.mul(-1d, submitTradeInfo.getPaySumAmount()));
		}
		return new ResponseRestEntity<BankTranWebserviceReverseResponse>(bankTranWebserviceReverseResponse, HttpRestStatus.OK);
	}
	
	

}
