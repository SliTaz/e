package org.e.payment.core.pay.submit.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.BankInterfaceType;
import org.e.payment.core.pay.bankProcess.BankProcess;
import org.e.payment.core.pay.bankProcess.BankProcessErrorCode;
import org.e.payment.core.pay.bankProcess.BankProcessFactory;
import org.e.payment.core.pay.calcPrice.PayCalcPriceInt;
import org.e.payment.core.pay.fraud.FraudFactory;
import org.e.payment.core.pay.fraud.FraudResult;
import org.e.payment.core.pay.submit.SubmitPayProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.api.factory.PayAppFactory;
import com.zbensoft.e.payment.api.factory.PayAppGatewayFactory;
import com.zbensoft.e.payment.api.factory.PayGatewayFactory;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.api.log.SUBMIT_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserBankCardService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserBankCardService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserStoreTypeService;
import com.zbensoft.e.payment.api.service.api.PayCalcPriceService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.Coupon;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUserStoreType;
import com.zbensoft.e.payment.db.domain.PayApp;
import com.zbensoft.e.payment.db.domain.PayAppGateway;
import com.zbensoft.e.payment.db.domain.PayCalcPrice;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public abstract class AbsSubmitPayProcess implements SubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(AbsSubmitPayProcess.class);

	protected TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);

	protected ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);
	protected ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);

	protected MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);

	// protected PayAppGatewayService payAppGatewayService = SpringBeanUtil.getBean(PayAppGatewayService.class);

	// protected PayGatewayService payGatewayService = SpringBeanUtil.getBean(PayGatewayService.class);

	protected ConsumerUserBankCardService consumerUserBankCardService = SpringBeanUtil.getBean(ConsumerUserBankCardService.class);

	protected MerchantUserBankCardService merchantUserBankCardService = SpringBeanUtil.getBean(MerchantUserBankCardService.class);

	protected ConsumerCouponService consumerCouponService = SpringBeanUtil.getBean(ConsumerCouponService.class);

	protected ConsumerFamilyCouponService consumerFamilyCouponService = SpringBeanUtil.getBean(ConsumerFamilyCouponService.class);
	
	protected MerchantUserStoreTypeService merchantUserStoreTypeService = SpringBeanUtil.getBean(MerchantUserStoreTypeService.class);

	// protected PayAppService payAppService = SpringBeanUtil.getBean(PayAppService.class);

	protected LocaleMessageSourceService localeMessageSourceService = SpringBeanUtil.getBean(LocaleMessageSourceService.class);
	protected MerchantEmployeeService merchantEmployeeService = SpringBeanUtil.getBean(MerchantEmployeeService.class);

	protected PayCalcPriceService payCalcPriceService = SpringBeanUtil.getBean(PayCalcPriceService.class);
	protected CouponService couponService = SpringBeanUtil.getBean(CouponService.class);
	protected AmqpTemplate rabbitTemplate = SpringBeanUtil.getBean(AmqpTemplate.class);

	protected ResponseRestEntity response = null;
	Object request;
	private String redisKey_ORDER_NO = null;
	private boolean isPayedForPayUser = false;
	private boolean isPayedForRecvUser = false;

	// private Environment env = SpringBeanUtil.getBean(Environment.class);
	protected Map<String, String> fraudParam = new HashMap<>();

	public ResponseRestEntity<?> process(Object request) {
		SUBMIT_LOG.INFO(String.format("in %s", request.toString()));
		long s = System.currentTimeMillis();
		ResponseRestEntity<?> responseTmp = null;
		try {
			responseTmp = payProcess(request);
		} catch (Exception e) {
			log.error(String.format("usetime=%s,error %s", (System.currentTimeMillis() - s), request.toString()), e);
			SUBMIT_LOG.ERROR(String.format("usetime=%s,error %s", (System.currentTimeMillis() - s), request.toString()), e);
		}
		if (responseTmp != null) {
			if (responseTmp.getStatusCode() == HttpRestStatus.OK) {
				SUBMIT_LOG.INFO(String.format("usetime=%s,succ statusCode=%s(%s),%s,%s", (System.currentTimeMillis() - s), responseTmp.getStatusCode().getReasonPhrase(), responseTmp.getStatusCode(), request.toString(),
						responseTmp));
			} else {
				SUBMIT_LOG.INFO(String.format("usetime=%s,error statusCode=%s(%s),%s", (System.currentTimeMillis() - s), responseTmp.getStatusCode().getReasonPhrase(), responseTmp.getStatusCode(), request.toString()));
			}
		} else {
			SUBMIT_LOG.INFO(String.format("usetime=%s,AbsSubmitPayProcess get null response %s", (System.currentTimeMillis() - s), request.toString()));
		}
		if (responseTmp == null || (responseTmp.getStatusCode() != HttpRestStatus.OK
				&& (responseTmp.getStatusCode() != HttpRestStatus.PAY_ORDER_NO_SAME_SUBMIT || responseTmp.getStatusCode() != HttpRestStatus.PAY_ORDER_NO_CHECK_ERROR))) {
			if (redisKey_ORDER_NO != null) {
				RedisUtil.delete_key(redisKey_ORDER_NO);
			}
		}

		if (responseTmp == null) {
			responseTmp = new ResponseRestEntity<>(HttpRestStatus.PAY_ERROR, "最后返回为空");
		}
		return responseTmp;
	}

	public abstract ResponseRestEntity<?> payProcess(Object request);

	/**
	 * response不为空，并且=HttpRestStatus.OK，返回true
	 * 
	 * @param response
	 * @return
	 */
	protected boolean isErrorResponse() {
		if (response == null || (response != null && response.getStatusCode() == HttpRestStatus.OK)) {
			return false;
		}
		return true;
	}

	/**
	 * 校验请求是不是正确的类
	 * 
	 * @param isReauestClass
	 * @param localeMessageSourceService
	 * @return
	 */
	protected void validateRequestClass(boolean isReauestClass) {
		if (!isReauestClass) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_REQUEST_TYPE_ERROR, localeMessageSourceService.getMessage("epaymentpay.request.type.errror"));
		}
	}

	protected Object getUserByUserId(String userId) {
		if (userId == null || userId.isEmpty()) {
			return null;
		}
		Object payUser = null;
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {

			payUser = consumerUserService.selectByPrimaryKey(userId);
		}
		if (userId.startsWith(MessageDef.USER_TYPE.MERCHANT_STRING)) {
			payUser = merchantUserService.selectByPrimaryKey(userId);
		}
		return payUser;
	}

	protected void validatePayConsumerStatueWebservice(ConsumerUser payUserConsumerUser) {
		if (MessageDef.STATUS.ENABLE_INT != payUserConsumerUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.payer.disable"));
			return;
		}

		if (MessageDef.LOCKED.UNLOCKED != payUserConsumerUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.payer.locked"));
			return;
		}
		if (payUserConsumerUser.getIsActive() == null || MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE != payUserConsumerUser.getIsActive()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOTACTIVE, "付款用户未激活");
			return;
		}
	}

	protected void validatePayConsumerStatue(ConsumerUser payUserConsumerUser) {
		if (MessageDef.STATUS.ENABLE_INT != payUserConsumerUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.payer.disable"));
			return;
		}

		if (MessageDef.LOCKED.UNLOCKED != payUserConsumerUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.payer.locked"));
			return;
		}
		if (payUserConsumerUser.getIsActive() == null || MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE != payUserConsumerUser.getIsActive()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOTACTIVE, "付款用户未激活");
			return;
		}
		if (payUserConsumerUser.getIsDefaultPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != payUserConsumerUser.getIsDefaultPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DEFAULT_LOGIN_PASSWORD, "付款用户未修改登录密码");
			return;
		}
		if (payUserConsumerUser.getIsDefaultPayPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != payUserConsumerUser.getIsDefaultPayPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DEFAULT_PAY_PASSWORD, "付款用户未修改支付密码");
			return;
		}
	}

	protected void validatePayMerchantStatueWebservice(MerchantUser payUserMerchantUser) {

		if (MessageDef.STATUS.ENABLE_INT != payUserMerchantUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.payer.disable"));
			return;
		}

		if (MessageDef.LOCKED.UNLOCKED != payUserMerchantUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.payer.locked"));
			return;
		}
		if (payUserMerchantUser.getIsActive() == null || MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE != payUserMerchantUser.getIsActive()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOTACTIVE, "付款用户未激活");
			return;
		}
	}

	protected void validatePayMerchantStatue(MerchantUser payUserMerchantUser) {

		if (MessageDef.STATUS.ENABLE_INT != payUserMerchantUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.payer.disable"));
			return;
		}

		if (MessageDef.LOCKED.UNLOCKED != payUserMerchantUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.payer.locked"));
			return;
		}
		if (payUserMerchantUser.getIsActive() == null || MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE != payUserMerchantUser.getIsActive()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOTACTIVE, "付款用户未激活");
			return;
		}
		if (payUserMerchantUser.getIsDefaultPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != payUserMerchantUser.getIsDefaultPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DEFAULT_LOGIN_PASSWORD, "付款用户未修改登录密码");
			return;
		}
		if (payUserMerchantUser.getIsDefaultPayPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != payUserMerchantUser.getIsDefaultPayPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DEFAULT_PAY_PASSWORD, "付款用户未修改支付密码");
			return;
		}
	}

	protected void validateRecvConsumerStatue(ConsumerUser recvUserConsumerUser) {
		if (MessageDef.STATUS.ENABLE_INT != recvUserConsumerUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.recver.disable"));
			return;
		}

		if (MessageDef.LOCKED.UNLOCKED != recvUserConsumerUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.recver.locked"));
			return;
		}
		if (recvUserConsumerUser.getIsActive() == null || MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE != recvUserConsumerUser.getIsActive()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOTACTIVE, "收款用户未激活");
			return;
		}
		if (recvUserConsumerUser.getIsDefaultPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != recvUserConsumerUser.getIsDefaultPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_DEFAULT_LOGIN_PASSWORD, "收款用户未修改登录密码");
			return;
		}
		if (recvUserConsumerUser.getIsDefaultPayPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != recvUserConsumerUser.getIsDefaultPayPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_DEFAULT_PAY_PASSWORD, "收款用户未修改支付密码");
			return;
		}
	}

	protected void validateRecvMerchantStatue(MerchantUser recvUserrMerchantUser) {
		if (MessageDef.STATUS.ENABLE_INT != recvUserrMerchantUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.recver.disable"));
			return;
		}

		if (MessageDef.LOCKED.UNLOCKED != recvUserrMerchantUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.recver.locked"));
			return;
		}
		if (recvUserrMerchantUser.getIsActive() == null || MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE != recvUserrMerchantUser.getIsActive()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOTACTIVE, "收款用户未激活");
			return;
		}
		if (recvUserrMerchantUser.getIsDefaultPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != recvUserrMerchantUser.getIsDefaultPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_DEFAULT_LOGIN_PASSWORD, "收款用户未修改登录密码");
			return;
		}
		if (recvUserrMerchantUser.getIsDefaultPayPassword() == null || MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT != recvUserrMerchantUser.getIsDefaultPayPassword()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_DEFAULT_PAY_PASSWORD, "收款用户未修改支付密码");
			return;
		}
	}

	/**
	 * 获取用户绑定的银行卡
	 * 
	 * @param payUserId
	 * @param bankBindId
	 * @return
	 */
	protected Object getUserBankBind(String payUserId, String bankBindId) {
		if (payUserId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			return consumerUserBankCardService.selectByPrimaryKey(bankBindId);
		}

		if (payUserId.startsWith(MessageDef.USER_TYPE.MERCHANT_STRING)) {
			return merchantUserBankCardService.selectByPrimaryKey(bankBindId);
		}
		return null;
	}

	protected PayGateway getPayGateWayByBankBind(String payAppId, Object userBankBind, int getwayType) {
		PayGateway payGateway = null;
		if (userBankBind instanceof ConsumerUserBankCard) {
			payGateway = PayGatewayFactory.getInstance().get(((ConsumerUserBankCard) userBankBind).getBankId(), getwayType);
		}
		if (userBankBind instanceof MerchantUserBankCard) {
			payGateway = PayGatewayFactory.getInstance().get(((MerchantUserBankCard) userBankBind).getBankId(), getwayType);
		}
		if (payGateway != null) {
			PayAppGateway payAppGateway = PayAppGatewayFactory.getInstance().get(payAppId, payGateway.getPayGatewayId());
			if (payAppGateway != null) {
				return payGateway;
			}
		}
		return null;
	}

	protected void validatePayAppId(String payAppId) {
		if (payAppId == null || payAppId.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAYAPPID_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.payapp.no.notempty"));
			return;
		}
		PayApp payApp = PayAppFactory.getInstance().getById(payAppId);// payAppService.selectByPrimaryKey(payAppId);
		if (payApp == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAYAPPID_NOTEXIST, localeMessageSourceService.getMessage("epaymentpay.payapp.no.notexist"));
			return;
		}
		if (payApp.getStatus() != MessageDef.PAY_APP_STATUS.ENABLE_INT) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAYAPPID_NOTENABLE, localeMessageSourceService.getMessage("epaymentpay.payapp.no.notenable"));
		}
	}

	protected void validateOrderNo(String orderNo) {
		if (orderNo == null || orderNo.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_ORDERNO_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.pay.oder.notempty"));
			return;
		}
		if (orderNo.length() > 32) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_ORDERNO_LENGTH, localeMessageSourceService.getMessage("epaymentpay.pay.oder.length"));
		}
	}

	protected void validateTrade(String tradeType, int tradeTypeDef) {
		int tradeTypeInt = -1;
		try {
			tradeTypeInt = Integer.valueOf(tradeType);
		} catch (Exception e) {
		}
		if (tradeTypeDef != tradeTypeInt) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_TRADE_TYPE_NOT_EXIST, localeMessageSourceService.getMessage("epaymentpay.trade.type.notexist"));
		}
	}

	protected void validatePayAmount(String payAmountStr) {
		if (payAmountStr == null || payAmountStr.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_AMOUNT_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.paymony.notempty"));
			return;
		}

		if (payAmountStr.contains(".")) {
			String zAmount = payAmountStr.substring(payAmountStr.indexOf(".") + 1);
			if (zAmount.length() > 2) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_AMOUNT_LESS_THAN_TWO_DECIMAL, "epaymentpay.paymony.float.length");
				return;
			}
		}
		double payAmount = -1;
		try {
			payAmount = Double.valueOf(payAmountStr);
		} catch (Exception e) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_AMOUNT_MUST_DOUBLE, localeMessageSourceService.getMessage("epaymentpay.paymony.type.correct"));
			return;
		}

		if (payAmount <= 0) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_AMOUNT_GREATER_THEN_ZERO, localeMessageSourceService.getMessage("epaymentpay.paymony.zero"));
			return;
		}
	}

	protected void validatePayFee(String payFeeStr) {
		if (payFeeStr == null || payFeeStr.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_FEE_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.payfee.notempty"));
			return;
		}

		if (payFeeStr.contains(".")) {
			String zFEE = payFeeStr.substring(payFeeStr.indexOf(".") + 1);
			if (zFEE.length() > 2) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_FEE_LESS_THAN_TWO_DECIMAL, "epaymentpay.payfee.float.length");
				return;
			}
		}
		double payFEE = -1;
		try {
			payFEE = Double.valueOf(payFeeStr);
		} catch (Exception e) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_FEE_MUST_DOUBLE, localeMessageSourceService.getMessage("epaymentpay.payfee.type.correct"));
			return;
		}

		if (payFEE < 0) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_FEE_GREATER_THEN_OR_EQUEL_ZERO, localeMessageSourceService.getMessage("epaymentpay.payfee.zero"));
			return;
		}
	}

	protected void validateRecvAmount(String recvAmountStr) {
		if (recvAmountStr == null || recvAmountStr.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_AMOUNT_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.recvmony.notempty"));
			return;
		}

		if (recvAmountStr.contains(".")) {
			String zAmount = recvAmountStr.substring(recvAmountStr.indexOf(".") + 1);
			if (zAmount.length() > 2) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_AMOUNT_LESS_THAN_TWO_DECIMAL, "epaymentpay.recvmony.float.length");
				return;
			}
		}
		double recvAmount = -1;
		try {
			recvAmount = Double.valueOf(recvAmountStr);
		} catch (Exception e) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_AMOUNT_MUST_DOUBLE, localeMessageSourceService.getMessage("epaymentpay.recvmony.type.correct"));
			return;
		}

		if (recvAmount <= 0) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_AMOUNT_GREATER_THEN_ZERO, localeMessageSourceService.getMessage("epaymentpay.recvmony.zero"));
			return;
		}
	}

	protected void validateRecvFee(String payFeeStr) {
		if (payFeeStr == null || payFeeStr.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_FEE_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.recvfee.notempty"));
			return;
		}

		if (payFeeStr.contains(".")) {
			String zFEE = payFeeStr.substring(payFeeStr.indexOf(".") + 1);
			if (zFEE.length() > 2) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_FEE_LESS_THAN_TWO_DECIMAL, "epaymentpay.recvfee.float.length");
				return;
			}
		}
		double payFEE = -1;
		try {
			payFEE = Double.valueOf(payFeeStr);
		} catch (Exception e) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_FEE_MUST_DOUBLE, localeMessageSourceService.getMessage("epaymentpay.recvfee.type.correct"));
			return;
		}

		if (payFEE < 0) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_FEE_GREATER_THEN_OR_EQUEL_ZERO, localeMessageSourceService.getMessage("epaymentpay.recvfee.zero"));
			return;
		}
	}

	/**
	 * 校验用户绑定的银行卡
	 * 
	 * @param payUserId
	 * @param bankBindId
	 * @return
	 */
	protected Object validateUserBankBind(String payUserId, String bankBindId) {
		Object userBankBind = getUserBankBind(payUserId, bankBindId);

		if (userBankBind == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_USER_BIND_BANK_NOTEXIST, "bind bank not exist");
		}
		return userBankBind;
	}

	protected PayGateway validatePayGateway(String payAppId, Object userBankBind, int getwayType) {
		if (userBankBind == null) {
			return validatePayGateway(payAppId, getwayType);
		}
		PayGateway payGateway = getPayGateWayByBankBind(payAppId, userBankBind, getwayType);
		if (payGateway == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_GATEWAY_NOTEXIST, "pay gateway not exist");
		}
		return payGateway;
	}

	/**
	 * 校验app是否有支付类型的权限
	 * 
	 * @param payAppId
	 * @param getwayTypeId
	 * @return
	 */
	protected PayGateway validatePayGateway(String payAppId, int getwayTypeId) {
		PayGateway payGateway = PayGatewayFactory.getInstance().get(null, getwayTypeId);
		if (payGateway != null) {
			PayAppGateway payAppGateway = PayAppGatewayFactory.getInstance().get(payAppId, payGateway.getPayGatewayId());
			if (payAppGateway != null) {
				return payGateway;
			}
		}
		this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_GATEWAY_NOTEXIST, localeMessageSourceService.getMessage("epaymentpay.pay.netgateway.not.exist"));
		return payGateway;
	}

	protected Object validatePayUser(String payUserId) {
		Object payUser = getUserByUserId(payUserId);
		if (payUser == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.payer.notexist"));
		}
		return payUser;
	}

	protected Object validateRecvUser(String recvUserId) {
		Object recvUser = getUserByUserId(recvUserId);
		if (recvUser == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.recver.notexist"));
		}
		return recvUser;
	}

	protected MerchantUser validatePayUserMerchant(String payUserId) {
		MerchantUser payUser = null;
		if (payUserId != null && !payUserId.isEmpty()) {
			payUser = merchantUserService.selectByPrimaryKey(payUserId);
		}

		if (payUser == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.payer.notexist"));
			return payUser;
		}
		if (MessageDef.STATUS.ENABLE_INT != payUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.payer.disable"));
			return payUser;
		}

		if (MessageDef.LOCKED.UNLOCKED != payUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.payer.locked"));
			return payUser;
		}
		return payUser;
	}

	protected ConsumerUser validateRecvUserConsumer(String recvUserId) {

		ConsumerUser recvUser = null;

		if (recvUserId != null && !recvUserId.isEmpty()) {
			recvUser = consumerUserService.selectByPrimaryKey(recvUserId);
		}

		if (recvUser == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.recver.notexist"));
			return recvUser;
		}
		if (MessageDef.STATUS.ENABLE_INT != recvUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.recver.disable"));
			return recvUser;
		}

		if (MessageDef.LOCKED.UNLOCKED != recvUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.recver.locked"));
			return recvUser;
		}
		return recvUser;
	}

	protected ConsumerUser validatePayUserConsumer(String payUserId) {
		ConsumerUser payUser = null;
		if (payUserId != null && !payUserId.isEmpty()) {
			payUser = consumerUserService.selectByPrimaryKey(payUserId);
		}

		
		if (payUser == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.payer.notexist"));
			return payUser;
		}
		if (MessageDef.FIRST_LOGIN.NOT_FIRST!=payUser.getIsFirstLogin()) {//2018-03-31 修改，若没有注册过，则判为用户不存在
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.payer.notexist"));
			return payUser;
		}
		if (MessageDef.STATUS.ENABLE_INT != payUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.payer.disable"));
			return payUser;
		}

		if (MessageDef.LOCKED.UNLOCKED != payUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.payer.locked"));
			return payUser;
		}
		return payUser;
	}

	protected MerchantUser validateRecvUserMerchant(String recvUserId) {
		MerchantUser recvUser = null;
		if (recvUserId != null && !recvUserId.isEmpty()) {
			recvUser = merchantUserService.selectByPrimaryKey(recvUserId);
		}

		if (recvUser == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.recver.notexist"));
			return recvUser;
		}
		if (MessageDef.STATUS.ENABLE_INT != recvUser.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.recver.disable"));
			return recvUser;
		}

		if (MessageDef.LOCKED.UNLOCKED != recvUser.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.recver.locked"));
			return recvUser;
		}
		return recvUser;
	}

	protected Object validateCoupon(String consumerCouponId, String consumerUserClapId, String familyId) {
		Object coupon = null;
		if (consumerCouponId == null || consumerCouponId.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_ID_NOT_EMPTY, localeMessageSourceService.getMessage("epaymentpay.ticket.id.notnull"));
			return coupon;
		}
		if ((consumerUserClapId == null || consumerUserClapId.isEmpty()) && (familyId == null || familyId.isEmpty())) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_USER_FAMILY_NOT_EMPTY, localeMessageSourceService.getMessage("epaymentpay.ticket.family.and.userid.empty"));
			return coupon;
		}

		//券不合法
		if(!consumerCouponId.startsWith(MessageDef.COUPON_TYPE_PREFIX.CONSUMER_COUPON) && !consumerCouponId.startsWith(MessageDef.COUPON_TYPE_PREFIX.FAMILY_COUPON)){
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_ILLEGAL,"This coupon is illegal");
		}

		// 个人券
		if (consumerCouponId.startsWith(MessageDef.COUPON_TYPE_PREFIX.CONSUMER_COUPON)) {
			ConsumerCoupon consumerCouponRet = null;
			ConsumerCoupon consumerCoupon = new ConsumerCoupon();
			consumerCoupon.setConsumerCouponId(consumerCouponId);
			consumerCoupon.setConsumerUserClapId(consumerUserClapId);
			consumerCouponRet = consumerCouponService.selectByPrimaryKey(consumerCoupon);
			if (consumerCouponRet == null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_USER_NOT_EMPTY, localeMessageSourceService.getMessage("epaymentpay.ticket.user.not.exist"));
			}
			return consumerCouponRet;
		}

		//家庭券
		if (consumerCouponId.startsWith(MessageDef.COUPON_TYPE_PREFIX.FAMILY_COUPON)) {
			ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
			consumerFamilyCoupon.setConsumerFamilyCouponId(consumerCouponId);
			consumerFamilyCoupon.setFamilyId(familyId);
			ConsumerFamilyCoupon consumerFamilyCouponRet =  consumerFamilyCouponRet = consumerFamilyCouponService.selectByPrimaryKey(consumerFamilyCoupon);
			if (consumerFamilyCouponRet == null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_FAMILY_NOT_EMPTY, localeMessageSourceService.getMessage("epaymentpay.ticket.family.not.exist"));
			}
			return  consumerFamilyCouponRet;
		}

		return null;
	}

	protected void validateCouponId(String couponId, String payAmount) {
		if (couponId == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_ID_NOT_EMPTY, localeMessageSourceService.getMessage("epaymentpay.ticket.id.notnull"));
			return;
		}
		Coupon coupon = couponService.selectByPrimaryKey(couponId);
		Date now = Calendar.getInstance().getTime();
		if (!(coupon.getUserStartTime().before(now) && now.before(coupon.getUserEndTime()))) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_EXPIRED, "券过期");
			return;
		}
		//2017-12-18 delete by wangchenyang 局方变更需求，要求不校验coupne金额
		if (coupon.getAmount() != null && !coupon.getAmount().equals(Double.valueOf(payAmount))) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_COUPON_AMOUNT_NOT_MATCH, "付款金额和券金额不匹配");
			return;
		}
	}

	protected void validateBankCard(String bankCard, String bankUserName, String phoneNumber) {
		if ((bankCard != null && !bankCard.isEmpty()) || (bankUserName != null && !bankUserName.isEmpty()) || (phoneNumber != null && !phoneNumber.isEmpty())) {
			if (bankCard == null || bankCard.isEmpty()) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_BANK_CARD_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.bankcard.notempty"));
				return;
			}
			if (bankUserName == null || bankUserName.isEmpty()) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_BANK_USERNAME_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.bankusername.notempty"));
				return;
			}
			if (phoneNumber == null || phoneNumber.isEmpty()) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PHONENUMBER_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.phonenumber.notempty"));
				return;
			}
		}
	}

	protected MerchantEmployee validateEmployee(String employeeId) {
		MerchantEmployee merchantEmployee = null;
		if (employeeId != null && !employeeId.isEmpty()) {
			merchantEmployee = merchantEmployeeService.selectByPrimaryKey(employeeId);
			if (merchantEmployee == null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_MERCHANT_EMPLOYEE_NOT_EXIST, "员工不存在");
				return merchantEmployee;
			}
		}
		return merchantEmployee;
	}

	protected void validatePayPassword(String userId, String dbPayPassword, String checkPayPassword) {
		if (checkPayPassword == null || checkPayPassword.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_PASSWORD_NOTEMPTY, "支付密码不能为空");
			return;
		}
		if (checkPayPassword.length() != 18) {

			// 支付密码输入错误次数校验
			if (RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(userId, Calendar.getInstance().getTime())) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日支付密码错误次数限制");
				return;
			}

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (!encoder.matches(checkPayPassword, dbPayPassword)) {

				int count = RedisUtil.increment_ERROR_PAY_PASSWORD_COUNT(userId, Calendar.getInstance().getTime());
				this.response = new ResponseRestEntity<>(CommonFun.errorPayPassword(count), "密码错误，还可以尝试x次");
			}
		}
	}

//	protected void validateConsumerUseStore(MerchantUser merchantUser, ConsumerUser consumerUser) {
//		if (merchantUser != null && consumerUser != null && merchantUser.getClapStoreNo() != null) {
//			List<ConsumerUserClap> list = consumerUserClapService.selectByUserId(consumerUser.getUserId());
//			if (list != null && list.size() == 1) {
//				ConsumerUserClap consumerUserClap = list.get(0);
//				if (merchantUser.getClapStoreNo() != null && merchantUser.getClapStoreNo().equals(consumerUserClap.getClapStoreNo())) {
//					return;
//				}
//			}
//		}
//		this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_CONSUMER_NOTIN_MERCHANT_STORE, "用户不在商户的消费范围内");
//
//	}
	
	protected void validateConsumerUseStore(MerchantEmployee merchantEmployee, ConsumerUser consumerUser) {
		
		if (merchantEmployee != null &&consumerUser != null) {
				List<ConsumerUserClap> list = consumerUserClapService.selectByUserId(consumerUser.getUserId());
			if (list != null && list.size() == 1) {
				ConsumerUserClap consumerUserClap = list.get(0);
				if (consumerUserClap.getClapStoreNo() != null) {
					//1:对于superEmployee要判断用户是否属于该Provider
					if (merchantEmployee.getEmployeeType() != null && merchantEmployee.getEmployeeType() == MessageDef.EMPLOYEE_TYPE.SUPERE_EMPLOYEE) {
						MerchantEmployee employeeWithClapStoreNo = merchantEmployeeService.seletRifByClapStoreNo(consumerUserClap.getClapStoreNo());
						if (employeeWithClapStoreNo != null && employeeWithClapStoreNo.getRif() != null && employeeWithClapStoreNo.getRif().equals(merchantEmployee.getRif())) {
							return;
						}
					} else {//2对于clapstore，判断ClapStoreNo和用户ClapStoreNo是否一直
						if (merchantEmployee.getClapStoreNo() != null && merchantEmployee.getClapStoreNo().equals(consumerUserClap.getClapStoreNo())) {
							return;
						}
					}
				}
			}
			
		}
		this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_CONSUMER_NOTIN_MERCHANT_STORE, "用户不在商户的消费范围内");

	}

	protected Object validateRecvUserForTransfer(String recvUserName) {

		Object recvUser = null;

		ConsumerUserClap consumerUserClapIdNumber = consumerUserClapService.selectByIdNumber(recvUserName);
		if (consumerUserClapIdNumber != null) {
			ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(consumerUserClapIdNumber.getUserId());
			if (consumerUser != null) {
				recvUser = consumerUser;
			}
		}

		ConsumerUserClap consumerUserClapClapId = consumerUserClapService.selectByClapId(recvUserName);
		if (consumerUserClapClapId != null) {
			ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(consumerUserClapClapId.getUserId());
			if (consumerUser != null) {
				if (recvUser != null) {
					this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USERNAME_NOT_UNIQUE, "收款用户名重复");
					return null;
				}
				recvUser = consumerUser;
			}
		}
		MerchantEmployee merchantEmployeeSer =new MerchantEmployee();
		merchantEmployeeSer.setClapStoreNo(recvUserName);
		List<MerchantEmployee> merchantEmployeeList = merchantEmployeeService.seletCalpBySelective(merchantEmployeeSer);
		if (merchantEmployeeList != null && merchantEmployeeList.size() > 0 && merchantEmployeeList.get(0).getUserId() != null) {
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(merchantEmployeeList.get(0).getUserId());
			if (merchantUser != null) {
				if (recvUser != null) {
					this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USERNAME_NOT_UNIQUE, "收款用户名重复");
					return null;
				}
				recvUser = merchantUser;
			}
		}
		if (recvUser == null) {

			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOEXIST, "收款用户名不存在");
		}
		return null;
	}

	protected void sendToRabbitmq(TradeInfo submitTradeInfo) {
		try {
			rabbitTemplate.convertAndSend(RabbitmqDef.TRADE.EXCHANGE, null, submitTradeInfo);
			SUBMIT_LOG.INFO(String.format("send Trade info =%s", submitTradeInfo.toString()));
			BOOKKEEPING_LOG.INFO(String.format("send Trade info =%s", submitTradeInfo.toString()));
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "AbsSubmitPayProcess sendToRabbitmq", JSONObject.toJSONString(submitTradeInfo)));
			log.error(String.format("sendToRabbitmq error info =%s", submitTradeInfo.toString()), e);
			SUBMIT_LOG.INFO(String.format("sendToRabbitmq error info =%s", submitTradeInfo.toString()));
			SUBMIT_LOG.ERROR(String.format("sendToRabbitmq error info =%s", submitTradeInfo.toString()), e);
			BOOKKEEPING_LOG.INFO(String.format("sendToRabbitmq error info =%s", submitTradeInfo.toString()));
			BOOKKEEPING_LOG.ERROR(String.format("sendToRabbitmq error info =%s", submitTradeInfo.toString()), e);
		}
	}

	/**
	 * 判断是否为重复提交
	 * 
	 * @param orderNo
	 * @param now
	 */
	protected void validateExistOrderNo(String orderNo, Date now) {
		validateExistOrderNo(null, orderNo, now);
	}

	/**
	 * 判断是否为重复提交,每个银行内部不重复
	 * 
	 * @param orderNo
	 * @param now
	 */
	protected void validateExistOrderNo(String payBankId, String orderNo, Date now) {
		redisKey_ORDER_NO = RedisUtil.key_ORDER_NO(payBankId, orderNo, now);
		if (RedisUtil.hasKey(redisKey_ORDER_NO)) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_ORDER_NO_SAME_SUBMIT, "重复提交orderNo");
			return;
		} else {
			TradeInfo tradeInfoForSelectbyOrderNo = new TradeInfo();
			tradeInfoForSelectbyOrderNo.setMerchantOrderNo(orderNo);

			// Date yesterday = DateUtils.addDays(now, -1);
			tradeInfoForSelectbyOrderNo.setCreateTimeStartSer(DateUtil.convertDateToString(now, "yyyy-MM-dd 00:00:00"));
			tradeInfoForSelectbyOrderNo.setCreateTimeEndSer(DateUtil.convertDateToString(now, "yyyy-MM-dd 23:59:59"));
			// tradeInfoForSelectbyOrderNo.setCreateTimeStartSer(DateUtil.convertDateToFormatString(yesterday));
			// tradeInfoForSelectbyOrderNo.setCreateTimeEndSer(DateUtil.convertDateToFormatString(now));
			tradeInfoForSelectbyOrderNo.setPayBankId(payBankId);
			// tradeInfoForSelectbyOrderNo.setCreateTime(date);
			TradeInfo tradeInfo = null;
			List<TradeInfo> list = null;
			try {
				list = tradeInfoService.selectbyOrderNoInDayForValidateExist(tradeInfoForSelectbyOrderNo);
			} catch (Exception e) {
				log.warn("selectbyOrderNoInDayForValidateExist error", e);
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_ORDER_NO_CHECK_ERROR, "orderNo查询数据库出错，不影响整体业务");
				return;
			}
			if (list != null && list.size() == 1) {
				tradeInfo = list.get(0);
			}
			if (tradeInfo != null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_ORDER_NO_SAME_SUBMIT, "重复提交orderNo");
				return;
			}
		}
		RedisUtil.set_ORDER_NO(redisKey_ORDER_NO, orderNo, now);
	}

	/**
	 * 付款用户支付
	 * 
	 * @param tradeInfo
	 * @return
	 */
	protected BankProcessErrorCode processPayUserAccount(TradeInfo tradeInfo) {
		tradeInfo.setPayTime(Calendar.getInstance().getTime());
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			return processPayUserAccount_ACCOUNT_NUMBER(tradeInfo);
		} else if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			return processPayUserAccount_QUICK_PAYMENT(tradeInfo);
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_GATEWAY_NOTEXIST, "支付网关不存在");
			return null;
		}
	}

	private boolean isGetWayTypeAccountAmount(int getwayType) {
		switch (getwayType) {
		// case MessageDef.TRADE_PAY_GETWAY_TYPE.CONSUMPTION:
		// return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER:
			return true;
		default:
			break;
		}
		return false;
	}

	private boolean isGetWayTypeBankTran(int getwayType) {
		switch (getwayType) {
		case MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_RECHARGE:
			return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_REVERSE:
			return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_RECHARGE:
			return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_CHARGE:
			return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * 付款用户-快捷支付
	 * 
	 * @param tradeInfo
	 * @return
	 */
	private BankProcessErrorCode processPayUserAccount_QUICK_PAYMENT(TradeInfo tradeInfo) {
		// BankProcess tradeProcess = BankProcessFactory.getInstance().get(tradeInfo.getPayBankId(), BankInterfaceType.RECHARGE);
		BankProcess tradeProcess = BankProcessFactory.getInstance().get(BankInterfaceType.RECHARGE);
		if (tradeProcess == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_NOT_SUPPORT, "调用银行接口不支持");
			return null;
		}
		boolean resultBool = tradeProcess.process(tradeInfo);
		if (resultBool) {
			isPayedForPayUser = true;
			return BankProcessErrorCode.SUCC;
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_FAIL, "调用银行接口失败");
		}
		return null;
	}

	/**
	 * 付款用户-余额支付
	 * 
	 * @param tradeInfo
	 * @return
	 */
	private BankProcessErrorCode processPayUserAccount_ACCOUNT_NUMBER(TradeInfo tradeInfo) {

		BankProcessErrorCode tradeProcessErrorCode = BankProcessErrorCode.SUCC;
		// 余额到余额
		double payAmount = tradeInfo.getPaySumAmount();
		String payUserId = tradeInfo.getPayUserId();

		tradeProcessErrorCode = RedisUtil.increment_pay_ACCOUNT_AMOUNT(consumerUserService, payUserId, payAmount, tradeInfo);
		if (tradeProcessErrorCode == BankProcessErrorCode.SUCC) {
			isPayedForPayUser = true;
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_ACCOUNT_AMOUNT_NOT_ENOUGH, "用户余额不足");
		}

		return tradeProcessErrorCode;
	}

	/**
	 * 收款用户-收款
	 * 
	 * @param tradeInfo
	 * @return
	 */
	protected BankProcessErrorCode processRecvUserAccount(TradeInfo tradeInfo) {
		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			return processRecvUserAccount_ACCOUNT_NUMBER(tradeInfo);
		} else if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			return processRecvUserAccount_QUICK_PAYMENT(tradeInfo);
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_GATEWAY_NOTEXIST, "支付网关不存在");
			return null;
		}
	}

	/**
	 * 收款用户-快捷支付
	 * 
	 * @param tradeInfo
	 * @return
	 */
	private BankProcessErrorCode processRecvUserAccount_QUICK_PAYMENT(TradeInfo tradeInfo) {
		// BankProcess tradeProcess = BankProcessFactory.getInstance().get(tradeInfo.getRecvBankId(), BankInterfaceType.CHARGE);
		BankProcess tradeProcess = BankProcessFactory.getInstance().get(BankInterfaceType.CHARGE);
		if (tradeProcess == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_NOT_SUPPORT, "调用银行接口不支持");
			return null;
		}
		boolean resultBool = tradeProcess.process(tradeInfo);
		if (resultBool) {
			isPayedForRecvUser = true;
			return BankProcessErrorCode.SUCC;
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_FAIL, "调用银行接口失败");
		}
		return null;
	}

	/**
	 * 收款用户-余额支付
	 * 
	 * @param tradeInfo
	 * @return
	 */
	private BankProcessErrorCode processRecvUserAccount_ACCOUNT_NUMBER(TradeInfo tradeInfo) {

		BankProcessErrorCode tradeProcessErrorCode = BankProcessErrorCode.SUCC;
		// 到余额
		double recvAmount = tradeInfo.getRecvSumAmount();
		String recvUserId = tradeInfo.getRecvUserId();

		tradeProcessErrorCode = RedisUtil.increment_recv_ACCOUNT_AMOUNT(consumerUserService,merchantUserService, recvUserId, recvAmount, tradeInfo);

		isPayedForRecvUser = true;
		return tradeProcessErrorCode;
	}

	protected void rollbackUserAccount(TradeInfo tradeInfo) {
		if (isPayedForPayUser) {
			rollbackPayUserAccount(tradeInfo);
		}
		if (isPayedForRecvUser) {
			rollbackRecvUserAccount(tradeInfo);
		}

	}

	private void rollbackPayUserAccount(TradeInfo tradeInfo) {
		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			rollbackPayUserAccount_ACCOUNT_NUMBER(tradeInfo);
		} else if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			rollbackPayUserAccount_QUICK_PAYMENT(tradeInfo);
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_GATEWAY_NOTEXIST, "支付网关不存在");
		}
	}

	private void rollbackPayUserAccount_QUICK_PAYMENT(TradeInfo tradeInfo) {
		try {
			// BankProcess tradeProcess = BankProcessFactory.getInstance().get(tradeInfo.getPayBankId(), BankInterfaceType.REVERSE);
			BankProcess tradeProcess = BankProcessFactory.getInstance().get(BankInterfaceType.REVERSE);
			if (tradeProcess == null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_NOT_SUPPORT, "调用银行接口不支持");
				return;
			}
			boolean resultBool = tradeProcess.process(tradeInfo);
			if (resultBool) {
			} else {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_FAIL, "调用银行接口失败");
			}
		} catch (Exception e) {
			log.error(String.format("rollbackRecvUserAccount_QUICK_PAYMENT error,%s", tradeInfo.toString()), e);
			SUBMIT_LOG.INFO(String.format("rollbackRecvUserAccount_QUICK_PAYMENT error,%s", tradeInfo.toString()));
			SUBMIT_LOG.ERROR(String.format("rollbackRecvUserAccount_QUICK_PAYMENT error,%s", tradeInfo.toString()), e);
		}
	}

	private void rollbackPayUserAccount_ACCOUNT_NUMBER(TradeInfo tradeInfo) {
		try {
			// 余额
			double payAmount = tradeInfo.getPaySumAmount();
			String payUserId = tradeInfo.getPayUserId();

			RedisUtil.increment_rollback_pay_ACCOUNT_AMOUNT(consumerUserService, payUserId, payAmount, tradeInfo);

		} catch (Exception e) {
			log.error(String.format("rollbackRecvUserAccount_ACCOUNT_NUMBER error,%s", tradeInfo.toString()), e);
			SUBMIT_LOG.INFO(String.format("rollbackRecvUserAccount_ACCOUNT_NUMBER error,%s", tradeInfo.toString()));
			SUBMIT_LOG.ERROR(String.format("rollbackRecvUserAccount_ACCOUNT_NUMBER error,%s", tradeInfo.toString()), e);
		}
	}

	/**
	 * 银行充值退还，把recvuser的recvSumAmount进行退还
	 * 
	 * @param tradeInfo
	 */
	protected void rollbackRecvUserAccountForBankRechargeRefund(TradeInfo tradeInfo) {
		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			rollbackRecvUserAccount_ACCOUNT_NUMBER(tradeInfo);
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_GATEWAY_NOTEXIST, "支付网关不存在");
		}
	}

	/**
	 * 提现退还，把payuser的paySumAmount进行退还
	 * 
	 * @param tradeInfo
	 */
	protected void rollbackUserAccountForChargeRefund(TradeInfo tradeInfo) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			rollbackPayUserAccount_ACCOUNT_NUMBER(tradeInfo);
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_GATEWAY_NOTEXIST, "支付网关不存在");
		}
	}

	private void rollbackRecvUserAccount(TradeInfo tradeInfo) {
		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			rollbackRecvUserAccount_ACCOUNT_NUMBER(tradeInfo);
		} else if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			rollbackRecvUserAccount_QUICK_PAYMENT(tradeInfo);
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_GATEWAY_NOTEXIST, "支付网关不存在");
		}
	}

	private void rollbackRecvUserAccount_QUICK_PAYMENT(TradeInfo tradeInfo) {
		try {
			// BankProcess tradeProcess = BankProcessFactory.getInstance().get(tradeInfo.getPayBankId(), BankInterfaceType.REVERSE);
			BankProcess tradeProcess = BankProcessFactory.getInstance().get(BankInterfaceType.REVERSE);
			if (tradeProcess == null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_NOT_SUPPORT, "调用银行接口不支持");
				return;
			}
			boolean resultBool = tradeProcess.process(tradeInfo);
			if (resultBool) {
			} else {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_CALL_BANK_INTERFACE_FAIL, "调用银行接口失败");
			}
		} catch (Exception e) {
			log.error(String.format("rollbackRecvUserAccount_QUICK_PAYMENT error,%s", tradeInfo.toString()), e);
			SUBMIT_LOG.INFO(String.format("rollbackRecvUserAccount_QUICK_PAYMENT error,%s", tradeInfo.toString()));
			SUBMIT_LOG.ERROR(String.format("rollbackRecvUserAccount_QUICK_PAYMENT error,%s", tradeInfo.toString()), e);
		}
	}

	private void rollbackRecvUserAccount_ACCOUNT_NUMBER(TradeInfo tradeInfo) {
		try {
			// 到余额
			double recvAmount = tradeInfo.getRecvSumAmount();
			String recvUserId = tradeInfo.getRecvUserId();

			RedisUtil.increment_rollback_recv_ACCOUNT_AMOUNT(consumerUserService, recvUserId, recvAmount, tradeInfo);

		} catch (Exception e) {
			log.error(String.format("rollbackRecvUserAccount_ACCOUNT_NUMBER error,%s", tradeInfo.toString()), e);
			SUBMIT_LOG.INFO(String.format("rollbackRecvUserAccount_ACCOUNT_NUMBER error,%s", tradeInfo.toString()));
			SUBMIT_LOG.ERROR(String.format("rollbackRecvUserAccount_ACCOUNT_NUMBER error,%s", tradeInfo.toString()), e);
		}
	}

	protected PayGateway validateWebserviceBankId(String payAppId, String bankId, int getwayType) {
		if (bankId == null || bankId.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_BANKID_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.pay.netgateway.not.exist"));
			return null;
		}
		PayGateway payGateway = PayGatewayFactory.getInstance().get(bankId, getwayType);
		if (payGateway != null) {
			PayAppGateway payAppGateway = PayAppGatewayFactory.getInstance().get(payAppId, payGateway.getPayGatewayId());
			if (payAppGateway != null) {
				return payGateway;
			}
		}
		this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_GATEWAY_NOTEXIST, localeMessageSourceService.getMessage("epaymentpay.pay.netgateway.not.exist"));
		return null;
	}

	protected Object validateWebserviceVid(String vid, String patrimonyCardCode, String interfaceVersion) {
		if (vid == null || vid.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_VID_NOT_EMPTY, "VID不能为空");
			return null;
		}
		Object payUser = null;
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(vid);
		if (consumerUserClap != null) {
			if ("2.0".equals(interfaceVersion)) {
				if (patrimonyCardCode == null || !patrimonyCardCode.equals(consumerUserClap.getClapNo())) {
					this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PATRIMONY_CARD_CODE_NOT_MATCH, "Patrimony Card Code no not match,interface v2");
					return null;
				}
			}
			payUser = consumerUserService.selectByPrimaryKey(consumerUserClap.getUserId());
		} else {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_VID_NOT_EXIST, "VID不存在");
		}
		return payUser;
	}

	protected void validateWebserviceAmount(Double amount) {
		if (amount == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_AMOUNT_NOTEMPTY, localeMessageSourceService.getMessage("epaymentpay.paymony.notempty"));
			return;
		}

		if (amount <= 0) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_AMOUNT_GREATER_THEN_ZERO, localeMessageSourceService.getMessage("epaymentpay.paymony.zero"));
			return;
		}
	}

	protected void validateWebservicePaymentTime(Date paymentTime) {
		if (paymentTime == null) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_PAYMENTTIME_NOT_EMPTY, localeMessageSourceService.getMessage("epaymentpay.paymony.notempty"));
		}
	}

	protected TradeInfo validateWebserviceReverseRefNo(String payBankId, String reverseRefNo, Date date) {
		if (reverseRefNo == null || reverseRefNo.isEmpty()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_REVERSE_REFNO_NOT_EMPTY, "reverseRefNo不能为空");
			return null;
		}
		try {
			TradeInfo tradeInfoForSelectbyOrderNo = new TradeInfo();
			tradeInfoForSelectbyOrderNo.setMerchantOrderNo(reverseRefNo);

			// Date yesterday = DateUtils.addDays(date, -1);
			tradeInfoForSelectbyOrderNo.setCreateTimeStartSer(DateUtil.convertDateToString(date, "yyyy-MM-dd 00:00:00"));
			tradeInfoForSelectbyOrderNo.setCreateTimeEndSer(DateUtil.convertDateToString(date, "yyyy-MM-dd 23:59:59"));
			tradeInfoForSelectbyOrderNo.setPayBankId(payBankId);
			// tradeInfoForSelectbyOrderNo.setCreateTime(date);
			TradeInfo tradeInfo = null;
			List<TradeInfo> list = tradeInfoService.selectbyOrderNoInDay(tradeInfoForSelectbyOrderNo);
			if (list != null && list.size() == 1) {
				tradeInfo = list.get(0);
			}
			if (tradeInfo == null) {
				this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_REVERSE_REFNO_NOT_EXIST, "reverseRefNo不存在");
			} else {
				if (MessageDef.TRADE_HAVE_REFUND.YES == tradeInfo.getHaveRefund()) {
					this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_REVERSE_REFNO_ISREVERSEED, "reverseRefNo已经取消");
				} else if (MessageDef.TRADE_TYPE.BANK_RECHARGE != tradeInfo.getType()) {
					this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_REVERSE_REFNO_ONLY_BANK_RECHARGE, "reverseRefNo只能取消银行充值");
				}
			}
			return tradeInfo;
		} catch (Exception e) {
			log.error("", e);
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_WEBSERVICE_REVERSE_REFNO_SELECT_ERROR, "reverseRefNo查询错误");
		}
		return null;
	}

	protected void processPayFee(TradeInfo submitTradeInfo) {
		try {
			PayCalcPrice payCalcPrice = new PayCalcPrice();
			payCalcPrice.setPayGatewayId(submitTradeInfo.getPayGatewayId());
			payCalcPrice.setStatus(MessageDef.PAY_CALC_PRICE_STATUS.ENABLE_INT);
			List<PayCalcPrice> payCalcPriceList = payCalcPriceService.selectPage(payCalcPrice);
			if (payCalcPriceList != null && payCalcPriceList.size() > 0) {
				PayCalcPrice payCalcPriceTmp = payCalcPriceList.get(0);
				if (payCalcPriceTmp != null) {
					String handleCalss = payCalcPriceTmp.getHandleClass();
					if (StringUtils.isNotEmpty(handleCalss)) {
						PayCalcPriceInt payCalcPriceInt = (PayCalcPriceInt) Class.forName(handleCalss).newInstance();
						payCalcPriceInt.calcPricePay(submitTradeInfo);
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	protected void processRecvFee(TradeInfo submitTradeInfo) {
		try {
			PayCalcPrice payCalcPrice = new PayCalcPrice();
			payCalcPrice.setPayGatewayId(submitTradeInfo.getRecvGatewayId());
			payCalcPrice.setStatus(MessageDef.PAY_CALC_PRICE_STATUS.ENABLE_INT);
			List<PayCalcPrice> payCalcPriceList = payCalcPriceService.selectPage(payCalcPrice);
			if (payCalcPriceList != null && payCalcPriceList.size() > 0) {
				PayCalcPrice payCalcPriceTmp = payCalcPriceList.get(0);
				if (payCalcPriceTmp != null) {
					String handleCalss = payCalcPriceTmp.getHandleClass();
					if (StringUtils.isNotEmpty(handleCalss)) {
						PayCalcPriceInt payCalcPriceInt = (PayCalcPriceInt) Class.forName(handleCalss).newInstance();
						payCalcPriceInt.calcPriceRecv(submitTradeInfo);
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	protected void fraudProcess(Map<String, String> fraudParam) {
		FraudResult fraudResult = FraudFactory.getInstance().process(fraudParam);
		if (fraudResult == FraudResult.WARNING) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_FRAUD_WARNING, "风险管理警告此交易");
		}
		if (fraudResult == FraudResult.ERROR) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_FRAUD_WARNING, "风险管理终止此交易");
		}

	}

	protected void validateIsLimitDay(String bankRecharge, String vid, Double amount) {
		if (StatisticsUtil.isLimitDay(bankRecharge, vid, amount)) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE, "银行充值超出限制");
		}
	}

	protected void validateIsLimitMonth(String bankRecharge, String vid, Double amount) {
		if (StatisticsUtil.isLimitMonth(bankRecharge, vid, amount)) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE, "银行充值超出限制");
		}
	}
	
	
	
	/**
	 * 校验收款员工
	 * @param recvEmployeeUserId
	 * @return
	 */
	protected MerchantEmployee validateRecvUserEmployee(String recvEmployeeUserId) {
		MerchantEmployee recvEmployee = null;
		if (recvEmployeeUserId != null && !recvEmployeeUserId.isEmpty()) {
			recvEmployee = merchantEmployeeService.selectByPrimaryKey(recvEmployeeUserId);
		}

		if (recvEmployee == null) {
			return recvEmployee;
		}
		if (MessageDef.STATUS.ENABLE_INT != recvEmployee.getStatus()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.recver.disable"));
			return recvEmployee;
		}

		if (MessageDef.LOCKED.UNLOCKED != recvEmployee.getIsLocked()) {
			this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.recver.locked"));
			return recvEmployee;
		}
		return recvEmployee;
	}
	
	
	/**
	 * 校验员工是否为clap店主或clap超级用户
	 * @param recvEmployee
	 * @return
	 */
	protected boolean validateIsClapStore(MerchantEmployee recvEmployee) {
		if(recvEmployee!=null&&recvEmployee.getIsClap()==MessageDef.CLAP.IS_CLAP){
				return true;
		}
		return false;
	}
	
	
	protected boolean validatePayMerchantIsOffice(MerchantUser merchantUser) {
		if(merchantUser!=null&&merchantUser.getUserId()!=null){
			List<MerchantUserStoreType> merchantUserStoreTypeList=merchantUserStoreTypeService.selectByUserId(merchantUser.getUserId());
			if(merchantUserStoreTypeList!=null&&merchantUserStoreTypeList.size()>0){
				if(merchantUserStoreTypeList.size()==1&&merchantUserStoreTypeList.get(0).getHeadFficeId()!=null){
					merchantUser.setHeadOfficeId(merchantUserStoreTypeList.get(0).getHeadFficeId());
					merchantUser.setStoreType(merchantUserStoreTypeList.get(0).getStoreType());
					merchantUser.setPriovideType(merchantUserStoreTypeList.get(0).getPriovideType());
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	
}
