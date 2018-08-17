package org.e.payment.core.pay.submit.impl.submit.allBank;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferResponse;
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
 * 转账
 * 
 * @author xieqiang
 *
 */
public class AllBankSubmitTransferPayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankSubmitTransferPayProcess.class);

	@Override
	public ResponseRestEntity<SubmitTradeTransferResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof SubmitTradeTransferRequest);
		if (isErrorResponse()) {
			return response;
		}

		SubmitTradeTransferRequest submitTradeTransferRequest = (SubmitTradeTransferRequest) request;

		validateExistOrderNo(submitTradeTransferRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		Object payUser = validatePayUser(submitTradeTransferRequest.getPayUserId());

		if (payUser instanceof ConsumerUser) {
			ConsumerUser payUserConsumerUser = (ConsumerUser) payUser;
			validatePayConsumerStatue(payUserConsumerUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserConsumerUser.getUserId();
			payUserName = payUserConsumerUser.getUserName();

			validatePayPassword(payUserConsumerUser.getUserId(), payUserConsumerUser.getPayPassword(), submitTradeTransferRequest.getPayPassword());
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

			validatePayPassword(payUserMerchantUser.getUserId(), payUserMerchantUser.getPayPassword(), submitTradeTransferRequest.getPayPassword());
			if (isErrorResponse()) {
				return response;
			}
		}

		Object recvUser = validateRecvUserForTransfer(submitTradeTransferRequest.getRecvUserName());

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
		validatePayAppId(submitTradeTransferRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		// payGateway
		PayGateway payGateway = validatePayGateway(submitTradeTransferRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);

		// orderNo
		validateOrderNo(submitTradeTransferRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(submitTradeTransferRequest.getTradeType(), MessageDef.TRADE_TYPE.TRANSFER);
		if (isErrorResponse()) {
			return response;
		}

		// amount
		validatePayAmount(submitTradeTransferRequest.getPayAmount());
		if (isErrorResponse()) {
			return response;
		}

		validatePayFee(submitTradeTransferRequest.getPayFee());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvAmount(submitTradeTransferRequest.getRecvAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvFee(submitTradeTransferRequest.getRecvFee());
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(submitTradeTransferRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(submitTradeTransferRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.transfer"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(submitTradeTransferRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayEmployeeUserId(null);
		submitTradeInfo.setPayEmployeeUserName(null);
		submitTradeInfo.setPayBankId(null);
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(Double.valueOf(submitTradeTransferRequest.getPayAmount()));
		submitTradeInfo.setPayFee(0d);
		submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
		submitTradeInfo.setPayStartMoney(null);
		submitTradeInfo.setPayEndMoney(null);
		submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
		submitTradeInfo.setRecvUserId(recvUserId);
		submitTradeInfo.setRecvUserName(recvUserName);
		submitTradeInfo.setRecvEmployeeUserId(null);
		submitTradeInfo.setRecvEmployeeUserName(null);
		submitTradeInfo.setRecvGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setRecvBankId(null);
		submitTradeInfo.setRecvBankName(null);
		submitTradeInfo.setRecvBankCardNo(null);
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(Double.valueOf(submitTradeTransferRequest.getRecvAmount()));
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
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AllBankSubmitTransferPayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<SubmitTradeTransferResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		SubmitTradeTransferResponse submitTradeTransferResponse = new SubmitTradeTransferResponse();
		submitTradeTransferResponse.setTradeSeq(submitTradeInfo.getTradeSeq());

		return new ResponseRestEntity<SubmitTradeTransferResponse>(submitTradeTransferResponse, HttpRestStatus.OK);
	}
	
	



}
