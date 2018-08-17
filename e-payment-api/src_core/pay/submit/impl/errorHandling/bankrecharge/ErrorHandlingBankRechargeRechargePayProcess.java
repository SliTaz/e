package org.e.payment.core.pay.submit.impl.errorHandling.bankrecharge;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.ErrorHandlingRechargeRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingRechargeResponse;
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
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 补款处理-银行充值，银行有，我们没有，所以补款
 * 
 * @author xieqiang
 *
 */
public class ErrorHandlingBankRechargeRechargePayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandlingBankRechargeRechargePayProcess.class);

	@Override
	public ResponseRestEntity<ErrorHandlingRechargeResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		// Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof ErrorHandlingRechargeRequest);
		if (isErrorResponse()) {
			return response;
		}

		ErrorHandlingRechargeRequest errorHandlingRechargeRequest = (ErrorHandlingRechargeRequest) request;

		// validateExistOrderNo(errorHandlingRechargeRequest.getRefNo(), now);
		// if (isErrorResponse()) {
		// return response;
		// }

		// payAppId
		validatePayAppId(errorHandlingRechargeRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		// orderNo
		validateOrderNo(errorHandlingRechargeRequest.getRefNo());
		if (isErrorResponse()) {
			return response;
		}

		PayGateway payGateway = validateWebserviceBankId(errorHandlingRechargeRequest.getPayAppId(), errorHandlingRechargeRequest.getBankId(), MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_RECHARGE);
		if (isErrorResponse()) {
			return response;
		}

		PayGateway recvGateway = validatePayGateway(errorHandlingRechargeRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}

		Object payUser = validateWebserviceVid(errorHandlingRechargeRequest.getVid(), null, null);
		if (isErrorResponse()) {
			return response;
		}

		if (payUser instanceof ConsumerUser) {
			ConsumerUser payUserConsumerUser = (ConsumerUser) payUser;
			//2017-12-20 对账不校验用户状态  by Wangchenyang
			//validatePayConsumerStatue(payUserConsumerUser);
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
			//2017-12-20 对账不校验用户状态 by Wangchenyang
			//validatePayMerchantStatue(payUserMerchantUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserMerchantUser.getUserId();
			payUserName = CommonFun.getMerchantUserName(payUserMerchantUser);
			recvUserId = payUserMerchantUser.getUserId();
			recvUserName = CommonFun.getMerchantUserName(payUserMerchantUser);

		}
		Double amount = DoubleUtil.divide(Double.valueOf(errorHandlingRechargeRequest.getTransactionAmount()), 100d);
		validateWebserviceAmount(amount);
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(errorHandlingRechargeRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(MessageDef.TRADE_TYPE.BANK_RECHARGE);
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.recharge"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(errorHandlingRechargeRequest.getRefNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayEmployeeUserId(null);
		submitTradeInfo.setPayEmployeeUserName(null);
		submitTradeInfo.setPayBankId(payGateway.getBankId());
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(amount);
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
		submitTradeInfo.setRecvAmount(amount);
		submitTradeInfo.setRecvFee(0d);
		submitTradeInfo.setRecvSumAmount(DoubleUtil.add(submitTradeInfo.getRecvAmount(), submitTradeInfo.getRecvFee()));
		submitTradeInfo.setRecvStartMoney(null);
		submitTradeInfo.setRecvEndMoney(null);
		submitTradeInfo.setRecvBorrowLoanFlag(MessageDef.BORROW_LOAN.LOAN);
		submitTradeInfo.setCallbackUrl(null);
		submitTradeInfo.setCreateTime(new Date(System.currentTimeMillis()));
		submitTradeInfo.setPayTime(null);
		submitTradeInfo.setEndTime(null);
		submitTradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
		submitTradeInfo.setRemark(null);

		processRecvFee(submitTradeInfo);

		processRecvUserAccount(submitTradeInfo);
		if (isErrorResponse()) {
			rollbackUserAccount(submitTradeInfo);
			return response;
		}

		submitTradeInfo.setCreateTime(DateUtil.convertStringToDate(errorHandlingRechargeRequest.getTransactionDate(), DateUtil.DATE_FORMAT_SEVENTEEN));
		submitTradeInfo.setPayTime(DateUtil.convertStringToDate(errorHandlingRechargeRequest.getTransactionDate(), DateUtil.DATE_FORMAT_SEVENTEEN));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

		try {
			tradeInfoService.insert(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingRechargePayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<ErrorHandlingRechargeResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		ErrorHandlingRechargeResponse errorHandlingRechargeResponse = new ErrorHandlingRechargeResponse();
		errorHandlingRechargeResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		errorHandlingRechargeResponse.setTradeInfo(submitTradeInfo);

		// 补款
		StatisticsUtil.addStatisticsSuccLimitMonth(RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE, submitTradeInfo.getCreateTime(), recvUserId, amount);
		return new ResponseRestEntity<ErrorHandlingRechargeResponse>(errorHandlingRechargeResponse, HttpRestStatus.OK);
	}


}
