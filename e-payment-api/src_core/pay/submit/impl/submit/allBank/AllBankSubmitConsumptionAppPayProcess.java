package org.e.payment.core.pay.submit.impl.submit.allBank;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.SubmitTradeConsumptionAppRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeConsumptionAppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 消费
 * 
 * @author xieqiang
 *
 */
public class AllBankSubmitConsumptionAppPayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AllBankSubmitConsumptionAppPayProcess.class);

	@Override
	public ResponseRestEntity<SubmitTradeConsumptionAppResponse> payProcess(Object request) {

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

		MerchantUser mainRecvUser=null;
		String couponId = null;
		ConsumerCoupon consumerCouponRet = null;
		ConsumerFamilyCoupon consumerFamilyCouponRet = null;
		Date now = new Date(System.currentTimeMillis());

		validateRequestClass(request != null && request instanceof SubmitTradeConsumptionAppRequest);
		if (isErrorResponse()) {
			return response;
		}

		SubmitTradeConsumptionAppRequest submitTradeConsumptionAppRequest = (SubmitTradeConsumptionAppRequest) request;

		validateExistOrderNo(submitTradeConsumptionAppRequest.getOrderNo(), now);
		if (isErrorResponse()) {
			return response;
		}

		ConsumerUser payUser = validatePayUserConsumer(submitTradeConsumptionAppRequest.getPayUserId());
		if (isErrorResponse()) {
			return response;
		}
		payUserId = payUser.getUserId();
		payUserName = payUser.getUserName();
		
		
		MerchantEmployee recvEmployee=validateRecvUserEmployee(submitTradeConsumptionAppRequest.getRecvEmployeeId());//员工收款
		if (isErrorResponse()) {
			return response;
		}
		if(recvEmployee!=null){
			recvEmployeeUserId=recvEmployee.getEmployeeUserId();
			recvEmployeeUserName=CommonFun.getMerchantEmployeeUserName(recvEmployee);
		}
		
		MerchantUser recvUser = validateRecvUserMerchant(submitTradeConsumptionAppRequest.getRecvUserId());//
		if (isErrorResponse()) {
			return response;
		}
		recvUserId = recvUser.getUserId();
		recvUserName = CommonFun.getMerchantUserName(recvUser);
		
		if(StringUtils.isNotEmpty(recvUser.getHeadOfficeId())){
			mainRecvUser = validateRecvUserMerchant(recvUser.getHeadOfficeId());//
			if (isErrorResponse()) {
				return response;
			}
			recvMainUserId=mainRecvUser.getUserId();
			recvMainUserName=CommonFun.getMerchantUserName(mainRecvUser);
		}
		
		
		

		// payAppId
		validatePayAppId(submitTradeConsumptionAppRequest.getPayAppId());
		if (isErrorResponse()) {
			return response;
		}
		PayGateway payGateway = validatePayGateway(submitTradeConsumptionAppRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
		if (isErrorResponse()) {
			return response;
		}
		// orderNo
		validateOrderNo(submitTradeConsumptionAppRequest.getOrderNo());
		if (isErrorResponse()) {
			return response;
		}

		// tradeType
		validateTrade(submitTradeConsumptionAppRequest.getTradeType(), MessageDef.TRADE_TYPE.CONSUMPTION);
		if (isErrorResponse()) {
			return response;
		}

		// amount
		validatePayAmount(submitTradeConsumptionAppRequest.getPayAmount());
		if (isErrorResponse()) {
			return response;
		}

		validatePayFee(submitTradeConsumptionAppRequest.getPayFee());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvAmount(submitTradeConsumptionAppRequest.getRecvAmount());
		if (isErrorResponse()) {
			return response;
		}

		validateRecvFee(submitTradeConsumptionAppRequest.getRecvFee());
		if (isErrorResponse()) {
			return response;
		}

		if (validateIsClapStore(recvEmployee)) {

			// 使用券
			Object coupon = validateCoupon(submitTradeConsumptionAppRequest.getConsumerCouponId(), submitTradeConsumptionAppRequest.getConsumerUserClapId(), submitTradeConsumptionAppRequest.getFamilyId());
			if (isErrorResponse()) {
				return response;
			}

			if (coupon instanceof ConsumerCoupon) {
				consumerCouponRet = (ConsumerCoupon) coupon;
				couponId = consumerCouponRet.getCouponId();
			}
			if (coupon instanceof ConsumerFamilyCoupon) {
				consumerFamilyCouponRet = (ConsumerFamilyCoupon) coupon;
				couponId = consumerFamilyCouponRet.getCouponId();
			}

			validateCouponId(couponId, submitTradeConsumptionAppRequest.getPayAmount());
			if (isErrorResponse()) {
				return response;
			}

			validateConsumerUseStore(recvEmployee, payUser);
			if (isErrorResponse()) {
				return response;
			}
		}
//		MerchantEmployee merchantEmployee = validateEmployee(submitTradeConsumptionAppRequest.getRecvEmployeeId());
//		if (isErrorResponse()) {
//			return response;
//		}
		//buyer 支付密码校验
		 validatePayPassword(payUserId, payUser.getPayPassword(), submitTradeConsumptionAppRequest.getConsumerPayPassword());
		 if (isErrorResponse()) {
			 return response;
		 }

		TradeInfo submitTradeInfo = new TradeInfo();
		submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		submitTradeInfo.setParentTradeSeq(null);
		submitTradeInfo.setPayAppId(submitTradeConsumptionAppRequest.getPayAppId());
		submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());// 余额支付
		submitTradeInfo.setType(Integer.valueOf(submitTradeConsumptionAppRequest.getTradeType()));
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.PROCESSING);
		submitTradeInfo.setErrorCode(null);
		submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.UNCLOSE);
		// submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.payee"));
		submitTradeInfo.setConsumptionName(null);
		submitTradeInfo.setMerchantOrderNo(submitTradeConsumptionAppRequest.getOrderNo());
		submitTradeInfo.setPayUserId(payUserId);
		submitTradeInfo.setPayUserName(payUserName);
		submitTradeInfo.setPayEmployeeUserId(null);
		submitTradeInfo.setPayEmployeeUserName(null);
		submitTradeInfo.setPayBankId(null);
		submitTradeInfo.setPayBankName(null);
		submitTradeInfo.setPayBankCardNo(null);
		submitTradeInfo.setPayBankCardHolerName(null);
		submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setPayBankType(null);
		submitTradeInfo.setPayBankOrderNo(null);
		submitTradeInfo.setPayAmount(Double.valueOf(submitTradeConsumptionAppRequest.getPayAmount()));
		submitTradeInfo.setPayFee(0d);
		submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
		submitTradeInfo.setPayStartMoney(null);
		submitTradeInfo.setPayEndMoney(null);
		submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
		submitTradeInfo.setRecvUserId(recvUserId);
		submitTradeInfo.setRecvUserName(recvUserName);
		submitTradeInfo.setRecvEmployeeUserId(recvEmployeeUserId);
		submitTradeInfo.setRecvEmployeeUserName(recvEmployeeUserName);
		submitTradeInfo.setRecvMainUserId(recvMainUserId);
		submitTradeInfo.setRecvMainUserName(recvMainUserName);
		// submitTradeInfo.setRecvGatewayId(MessageDef.GATEWAY_ID.ACCOUNT_NUMBER);// 余额支付
		submitTradeInfo.setRecvGatewayId(payGateway.getPayGatewayId());// 余额支付
		submitTradeInfo.setRecvBankId(null);
		submitTradeInfo.setRecvBankName(null);
		submitTradeInfo.setRecvBankCardNo(null);
		submitTradeInfo.setRecvBankCardHolerName(null);
		submitTradeInfo.setRecvGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
		submitTradeInfo.setRecvBankType(null);
		submitTradeInfo.setRecvBankOrderNo(null);
		submitTradeInfo.setRecvAmount(Double.valueOf(submitTradeConsumptionAppRequest.getRecvAmount()));
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
		submitTradeInfo.setConsumerCouponId(submitTradeConsumptionAppRequest.getConsumerCouponId());

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

			if (consumerCouponRet != null) {
				consumerCouponRet.setTradeSeq(submitTradeInfo.getTradeSeq());
				consumerCouponRet.setStatus(MessageDef.COUPON_STATUS.USE);
				consumerCouponService.updateByPrimaryKey(consumerCouponRet);
			} else if (consumerFamilyCouponRet != null) {
				consumerFamilyCouponRet.setTradeSeq(submitTradeInfo.getTradeSeq());
				consumerFamilyCouponRet.setStatus(MessageDef.COUPON_STATUS.USE);
				consumerFamilyCouponService.updateByPrimaryKey(consumerFamilyCouponRet);
			}

		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AllBankSubmitConsumptionAppPayProcess tradeInfoService.insert", JSONObject.toJSONString(submitTradeInfo)));
			rollbackUserAccount(submitTradeInfo);
			log.error("", e);
			return new ResponseRestEntity<SubmitTradeConsumptionAppResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		sendToRabbitmq(submitTradeInfo);

		SubmitTradeConsumptionAppResponse submitTradeConsumptionAppResponse = new SubmitTradeConsumptionAppResponse();
		submitTradeConsumptionAppResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		submitTradeConsumptionAppResponse.setCreateTime(DateUtil.convertDateToString(submitTradeInfo.getCreateTime(), DateUtil.DATE_FORMAT_TWENTY_FOUR));
		return new ResponseRestEntity<SubmitTradeConsumptionAppResponse>(submitTradeConsumptionAppResponse, HttpRestStatus.OK);
	}
	


	

}
