package org.e.payment.core.pay.submit.impl.webservice;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceRechargeRequest;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceRechargeResponse;
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
import com.zbensoft.e.payment.api.factory.BankInfoFactory;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class BankTranWebserviceRechargePayProcess extends AbsSubmitPayProcess {
	private static final Logger log = LoggerFactory.getLogger(BankTranWebserviceRechargePayProcess.class);

	@Override
	public ResponseRestEntity<BankTranWebserviceRechargeResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof BankTranWebserviceRechargeRequest);
		if (isErrorResponse()) {
			return response;
		}

		BankTranWebserviceRechargeRequest bankTranWebservcieRechargeRequest = (BankTranWebserviceRechargeRequest) request;

		validateExistOrderNo(bankTranWebservcieRechargeRequest.getBankId(), bankTranWebservcieRechargeRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		// payAppId
		validatePayAppId(bankTranWebservcieRechargeRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		// orderNo
		validateOrderNo(bankTranWebservcieRechargeRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(bankTranWebservcieRechargeRequest.getTradeType(), MessageDef.TRADE_TYPE.BANK_RECHARGE);
		if (isErrorResponse()) {
			return response;
		}

		// PayGateway payGateway = validateWebserviceIpaddress(bankTranWebservcieRechargeRequest.getIpAddress());
		// if (isErrorResponse()) {
		// return response;
		// }

		PayGateway payGateway = validateWebserviceBankId(bankTranWebservcieRechargeRequest.getPayAppId(), bankTranWebservcieRechargeRequest.getBankId(), MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_RECHARGE);
		if (isErrorResponse()) {
			return response;
		}

		PayGateway recvGateway = validatePayGateway(bankTranWebservcieRechargeRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}
		Object payUser = validateWebserviceVid(bankTranWebservcieRechargeRequest.getVid(), bankTranWebservcieRechargeRequest.getPatrimonyCardCode(), bankTranWebservcieRechargeRequest.getInterfaceVersion());
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

		validateWebserviceAmount(bankTranWebservcieRechargeRequest.getAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateWebservicePaymentTime(bankTranWebservcieRechargeRequest.getPaymentTime());
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(bankTranWebservcieRechargeRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(bankTranWebservcieRechargeRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.recharge"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(bankTranWebservcieRechargeRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayEmployeeUserId(null);
		submitTradeInfo.setPayEmployeeUserName(null);
		submitTradeInfo.setPayBankId(payGateway.getBankId());
		submitTradeInfo.setPayBankName(null);
		BankInfo payBankinfo=BankInfoFactory.getInstance().get(payGateway.getBankId());
		if(payBankinfo!=null&&payBankinfo.getName()!=null){
			submitTradeInfo.setPayBankName(payBankinfo.getName());
		}
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(bankTranWebservcieRechargeRequest.getAmount());
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
		BankInfo recvBankinfo=BankInfoFactory.getInstance().get(recvGateway.getPayGatewayId());
		if(recvBankinfo!=null&&recvBankinfo.getName()!=null){
			submitTradeInfo.setRecvBankName(recvBankinfo.getName());
		}
		submitTradeInfo.setRecvBankCardNo(null);
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(recvGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(bankTranWebservcieRechargeRequest.getAmount());
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

		processRecvFee(submitTradeInfo);

		fraudProcess(fraudParam);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}

		validateIsLimitDay(RedisDef.STATISTICS_LIMIT_DAY.BANK_RECHARGE, recvUserId, bankTranWebservcieRechargeRequest.getAmount());
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}

		validateIsLimitMonth(RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE, recvUserId, bankTranWebservcieRechargeRequest.getAmount());
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}
		processRecvUserAccount(submitTradeInfo);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}
		submitTradeInfo.setPayTime(bankTranWebservcieRechargeRequest.getPaymentTime());
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

		try {
			tradeInfoService.insert(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "BankTranWebserviceRechargePayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<BankTranWebserviceRechargeResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		BankTranWebserviceRechargeResponse bankTranWebserviceRechargeResponse = new BankTranWebserviceRechargeResponse();
		bankTranWebserviceRechargeResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		StatisticsUtil.addStatisticsSuccLimitMonth(RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE, submitTradeInfo.getCreateTime(), recvUserId, bankTranWebservcieRechargeRequest.getAmount());
		return new ResponseRestEntity<BankTranWebserviceRechargeResponse>(bankTranWebserviceRechargeResponse, HttpRestStatus.OK);
	}


	

}
