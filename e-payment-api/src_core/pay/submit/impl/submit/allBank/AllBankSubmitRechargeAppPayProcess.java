package org.e.payment.core.pay.submit.impl.submit.allBank;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeAppRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeAppResponse;
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
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 商户给用户充值
 * 
 * @author xieqiang
 *
 */
public class AllBankSubmitRechargeAppPayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankSubmitRechargeAppPayProcess.class);

	@Override
	public ResponseRestEntity<SubmitTradeRechargeAppResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof SubmitTradeRechargeAppRequest);
		if (isErrorResponse()) {
			return response;
		}

		SubmitTradeRechargeAppRequest submitTradeRechargeAppRequest = (SubmitTradeRechargeAppRequest) request;

		validateExistOrderNo(submitTradeRechargeAppRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		MerchantUser payUser = validatePayUserMerchant(submitTradeRechargeAppRequest.getPayUserId());
		if (isErrorResponse()) {
			return response;
		}
		payUserId = payUser.getUserId();
		payUserName = CommonFun.getMerchantUserName(payUser);

		ConsumerUser recvUser = null;

		recvUser = validateRecvUserConsumer(submitTradeRechargeAppRequest.getRecvUserId());
		if (isErrorResponse()) {
			return response;
		}

		recvUserId = recvUser.getUserId();
		recvUserName = recvUser.getUserName();

		// payAppId
		validatePayAppId(submitTradeRechargeAppRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		PayGateway payGateway = validatePayGateway(submitTradeRechargeAppRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}
		// orderNo
		validateOrderNo(submitTradeRechargeAppRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(submitTradeRechargeAppRequest.getTradeType(), MessageDef.TRADE_TYPE.RECHARGE);
		if (isErrorResponse()) {
			return response;
		}

		// amount
		validatePayAmount(submitTradeRechargeAppRequest.getPayAmount());
		if (isErrorResponse()) {
			return response;
		}

		validatePayFee(submitTradeRechargeAppRequest.getPayFee());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvAmount(submitTradeRechargeAppRequest.getRecvAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvFee(submitTradeRechargeAppRequest.getRecvFee());
		if (isErrorResponse()) {
			return response;
		}

		validateBankCard(submitTradeRechargeAppRequest.getBankCard(), submitTradeRechargeAppRequest.getBankUserName(), submitTradeRechargeAppRequest.getPhoneNumber());
		if (isErrorResponse()) {
			return response;
		}

		MerchantEmployee merchantEmployee = validateEmployee(submitTradeRechargeAppRequest.getPayEmployeeId());
		if (isErrorResponse()) {
			return response;
		}

		validatePayPassword(payUser.getUserId(), payUser.getPayPassword(), submitTradeRechargeAppRequest.getMerchantPayPassword());
		if (isErrorResponse()) {
			return response;
		}
		validateConsumerUseStore(merchantEmployee, recvUser);
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(submitTradeRechargeAppRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(submitTradeRechargeAppRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.recharge"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(submitTradeRechargeAppRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		if (merchantEmployee == null) {
			submitTradeInfo.setPayEmployeeUserId(null);
			submitTradeInfo.setPayEmployeeUserName(null);
		} else {
			submitTradeInfo.setPayEmployeeUserId(merchantEmployee.getEmployeeUserId());
			submitTradeInfo.setPayEmployeeUserName(CommonFun.getMerchantEmployeeUserName(merchantEmployee));
		}
		submitTradeInfo.setPayBankId(null);
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(Double.valueOf(submitTradeRechargeAppRequest.getPayAmount()));
		submitTradeInfo.setPayFee(0d);
		submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
		submitTradeInfo.setPayStartMoney(null);
		submitTradeInfo.setPayEndMoney(null);
		submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
		submitTradeInfo.setRecvUserId(recvUserId);
		submitTradeInfo.setRecvUserName(recvUserName);
		submitTradeInfo.setRecvEmployeeUserId(null);
		submitTradeInfo.setRecvEmployeeUserName(null);
		submitTradeInfo.setRecvGatewayId(payGateway.getPayGatewayId());//
		submitTradeInfo.setRecvBankId(null);
		submitTradeInfo.setRecvBankName(null);
		submitTradeInfo.setRecvBankCardNo(null);
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(Double.valueOf(submitTradeRechargeAppRequest.getRecvAmount()));
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
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AllBankSubmitRechargeAppPayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<SubmitTradeRechargeAppResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		SubmitTradeRechargeAppResponse submitTradeRechargeAppResponse = new SubmitTradeRechargeAppResponse();
		submitTradeRechargeAppResponse.setTradeSeq(submitTradeInfo.getTradeSeq());

		return new ResponseRestEntity<SubmitTradeRechargeAppResponse>(submitTradeRechargeAppResponse, HttpRestStatus.OK);
	}
	


}
