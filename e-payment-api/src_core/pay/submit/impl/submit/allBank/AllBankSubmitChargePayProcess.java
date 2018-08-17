package org.e.payment.core.pay.submit.impl.submit.allBank;

import java.util.Date;
import java.util.List;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.SubmitTradeWithdrawCashRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeWithdrawCashResponse;
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
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUserStoreType;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 提现
 * 
 * @author xieqiang
 *
 */
public class AllBankSubmitChargePayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankSubmitChargePayProcess.class);

	@Override
	public ResponseRestEntity<SubmitTradeWithdrawCashResponse> payProcess(Object request) {

		String payUserId = null;
		String payUserName = null;
		String recvUserId = null;
		String recvUserName = null;
		String payMainUserId=null;
		String payMainUserName=null;
		String recvMainUserId=null;
		String recvMainUserName=null;
		String payEmployeeUserId=null;
		String payEmployeeUserName=null;
		String recvEmployeeUserId=null;
		String recvEmployeeUserName=null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof SubmitTradeWithdrawCashRequest);
		if (isErrorResponse()) {
			return response;
		}

		SubmitTradeWithdrawCashRequest submitTradeWithdrawCashRequest = (SubmitTradeWithdrawCashRequest) request;

		validateExistOrderNo(submitTradeWithdrawCashRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		Object payUser = validatePayUser(submitTradeWithdrawCashRequest.getPayUserId());

		if (payUser instanceof ConsumerUser) {
			ConsumerUser payUserConsumerUser = (ConsumerUser) payUser;
			validatePayConsumerStatue(payUserConsumerUser);
			if (isErrorResponse()) {
				return response;
			}
			payUserId = payUserConsumerUser.getUserId();
			payUserName = payUserConsumerUser.getUserName();

			validatePayPassword(payUserConsumerUser.getUserId(), payUserConsumerUser.getPayPassword(), submitTradeWithdrawCashRequest.getPayPassword());
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

			
			validatePayPassword(payUserMerchantUser.getUserId(), payUserMerchantUser.getPayPassword(), submitTradeWithdrawCashRequest.getPayPassword());
			if (isErrorResponse()) {
				return response;
			}
			
			if(validatePayMerchantIsOffice(payUserMerchantUser)){
				MerchantUser payMainUser= validatePayUserMerchant(payUserMerchantUser.getHeadOfficeId());
				payMainUserId=payMainUser.getUserId();
				payMainUserName=CommonFun.getMerchantUserName(payMainUser);
			}
			
		}
		Object recvUser = validateRecvUser(submitTradeWithdrawCashRequest.getRecvUserId());

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
		// userBankBind
		Object userBankBind = validateUserBankBind(payUserId, submitTradeWithdrawCashRequest.getBankBindId());
		if (isErrorResponse()) {
			return response;
		}

		// payAppId
		validatePayAppId(submitTradeWithdrawCashRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}

		PayGateway payGateway = validatePayGateway(submitTradeWithdrawCashRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}

		PayGateway recvGateway = validatePayGateway(submitTradeWithdrawCashRequest.getPayAppId(), userBankBind, MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_CHARGE);
		if (isErrorResponse()) {
			return response;
		}
		// orderNo
		validateOrderNo(submitTradeWithdrawCashRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(submitTradeWithdrawCashRequest.getTradeType(), MessageDef.TRADE_TYPE.CHARGE);
		if (isErrorResponse()) {
			return response;
		}

		// amount
		validatePayAmount(submitTradeWithdrawCashRequest.getPayAmount());
		if (isErrorResponse()) {
			return response;
		}

		validatePayFee(submitTradeWithdrawCashRequest.getPayFee());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvAmount(submitTradeWithdrawCashRequest.getRecvAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvFee(submitTradeWithdrawCashRequest.getRecvFee());
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(submitTradeWithdrawCashRequest.getPayAppId());
		// 是余额支付的网关
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());
		submitTradeInfo.setType(Integer.valueOf(submitTradeWithdrawCashRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.withdraw"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(submitTradeWithdrawCashRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setRecvEmployeeUserId(null);
		submitTradeInfo.setRecvEmployeeUserName(null);
		submitTradeInfo.setPayBankId(null);
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayBankCardHolerName(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(Double.valueOf(submitTradeWithdrawCashRequest.getPayAmount()));
		submitTradeInfo.setPayFee(0d);
		submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
		submitTradeInfo.setPayStartMoney(null);
		submitTradeInfo.setPayEndMoney(null);
		submitTradeInfo.setPayMainUserId(payMainUserId);//add bye yang
		submitTradeInfo.setPayMainUserName(payMainUserName);//add bye yang
		submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
		submitTradeInfo.setRecvUserId(recvUserId);
		submitTradeInfo.setRecvUserName(recvUserName);
		submitTradeInfo.setRecvEmployeeUserId(null);
		submitTradeInfo.setRecvEmployeeUserName(null);
		submitTradeInfo.setRecvGatewayId(recvGateway.getPayGatewayId());
		
		if (userBankBind instanceof ConsumerUserBankCard) {
			submitTradeInfo.setRecvBankId(((ConsumerUserBankCard) userBankBind).getBankId());
			submitTradeInfo.setRecvBankName(null);
			BankInfo recvBankinfo=BankInfoFactory.getInstance().get(submitTradeInfo.getRecvBankId());
			if(recvBankinfo!=null&&recvBankinfo.getName()!=null){
				submitTradeInfo.setRecvBankName(recvBankinfo.getName());
			}
			submitTradeInfo.setRecvBankCardNo(((ConsumerUserBankCard) userBankBind).getCardNo());
			submitTradeInfo.setRecvBankCardHolerName(((ConsumerUserBankCard) userBankBind).getHolerName());
		}
		if (userBankBind instanceof MerchantUserBankCard) {
			submitTradeInfo.setRecvBankId(((MerchantUserBankCard) userBankBind).getBankId());
			submitTradeInfo.setRecvBankName(null);
			BankInfo recvBankinfo=BankInfoFactory.getInstance().get(submitTradeInfo.getRecvBankId());
			if(recvBankinfo!=null&&recvBankinfo.getName()!=null){
				submitTradeInfo.setRecvBankName(recvBankinfo.getName());
			}
			submitTradeInfo.setRecvBankCardNo(((MerchantUserBankCard) userBankBind).getCardNo());
			submitTradeInfo.setRecvBankCardHolerName(((MerchantUserBankCard) userBankBind).getHolerName());
		}
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(recvGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(Double.valueOf(submitTradeWithdrawCashRequest.getRecvAmount()));
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

		// submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
		// submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

		try {
			tradeInfoService.insert(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AllBankSubmitChargePayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<SubmitTradeWithdrawCashResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		SubmitTradeWithdrawCashResponse submitTradeWithdrawCashResponse = new SubmitTradeWithdrawCashResponse();
		submitTradeWithdrawCashResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		submitTradeWithdrawCashResponse.setCreateTime(DateUtil.convertDateToString(submitTradeInfo.getCreateTime(), DateUtil.DATE_FORMAT_TWENTY_FOUR));

		return new ResponseRestEntity<SubmitTradeWithdrawCashResponse>(submitTradeWithdrawCashResponse, HttpRestStatus.OK);
	}
	
	

}
