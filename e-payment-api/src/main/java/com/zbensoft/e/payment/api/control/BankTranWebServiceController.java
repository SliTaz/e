package com.zbensoft.e.payment.api.control;

import org.e.payment.core.pay.ProcessType;
import org.e.payment.core.pay.submit.SubmitPayProcess;
import org.e.payment.core.pay.submit.SubmitPayProcessFactory;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceRechargeRequest;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceRechargeResponse;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceReverseRequest;
import org.e.payment.core.pay.submit.vo.BankTranWebserviceReverseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.api.service.api.BankTranWebserviceService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.vo.webservice.bankTran.BankTranWebServiceRechargeControllerRequest;
import com.zbensoft.e.payment.api.vo.webservice.bankTran.BankTranWebServiceRechargeControllerResponse;
import com.zbensoft.e.payment.api.vo.webservice.bankTran.BankTranWebServiceReverseControllerRequest;
import com.zbensoft.e.payment.api.vo.webservice.bankTran.BankTranWebServiceReverseControllerResponse;
import com.zbensoft.e.payment.common.cxf.SOAPResultCode;

import io.swagger.annotations.ApiOperation;

/**
 * 提供给webservice调用，只能使用内网调用
 * 
 * @author xieqiang
 *
 */
@RequestMapping(value = "/webservice/bankTran")
@RestController
public class BankTranWebServiceController {

	private static final Logger log = LoggerFactory.getLogger(BankTranWebServiceController.class);

	@Autowired
	BankTranWebserviceService bankTranWebserviceService;

	@Autowired
	ConsumerUserClapService consumerUserClapService;

	@ApiOperation(value = "do process webservice bankTran recharge", notes = "")
	@RequestMapping(value = "/recharge", method = RequestMethod.POST)
	public ResponseRestEntity<BankTranWebServiceRechargeControllerResponse> recharge(@RequestBody BankTranWebServiceRechargeControllerRequest bankTranWebServiceRechargeControllerRequest) {
		try {
			StatisticsUtil.addStatistics(RedisDef.STATISTICS.BANK_RECHARGE, bankTranWebServiceRechargeControllerRequest.getBankId());
		} catch (Exception e) {
			log.warn("", e);
			return new ResponseRestEntity<>(HttpRestStatus.PAY_STATISTICS_EROOR);
		}

		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.WEBSERVICE_BANK_RECHARGE);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		BankTranWebserviceRechargeRequest bankTranWebServiceRechargeRequest = new BankTranWebserviceRechargeRequest();
		// bankTranWebServiceRechargeRequest.setPayAppId("10005");
		bankTranWebServiceRechargeRequest.setBankId(bankTranWebServiceRechargeControllerRequest.getBankId());
		bankTranWebServiceRechargeRequest.setOrderNo(bankTranWebServiceRechargeControllerRequest.getRefNo());
		bankTranWebServiceRechargeRequest.setTradeType("" + MessageDef.TRADE_TYPE.BANK_RECHARGE);
		bankTranWebServiceRechargeRequest.setIpAddress(bankTranWebServiceRechargeControllerRequest.getIpAddress());
		bankTranWebServiceRechargeRequest.setVid(bankTranWebServiceRechargeControllerRequest.getVid());
		bankTranWebServiceRechargeRequest.setAmount(bankTranWebServiceRechargeControllerRequest.getAmount());
		bankTranWebServiceRechargeRequest.setPaymentTime(bankTranWebServiceRechargeControllerRequest.getPaymentTime());
		bankTranWebServiceRechargeRequest.setPatrimonyCardCode(bankTranWebServiceRechargeControllerRequest.getPatrimonyCardCode());
		bankTranWebServiceRechargeRequest.setInterfaceVersion(bankTranWebServiceRechargeControllerRequest.getInterfaceVersion());

		ResponseRestEntity<BankTranWebserviceRechargeResponse> reponse = (ResponseRestEntity<BankTranWebserviceRechargeResponse>) submitPayProcess.process(bankTranWebServiceRechargeRequest);
		BankTranWebServiceRechargeControllerResponse bankTranWebServiceRechargeControllerResponse = new BankTranWebServiceRechargeControllerResponse();

		bankTranWebServiceRechargeControllerResponse.setResultCode(doResultCode(reponse.getStatusCode()));

		if (reponse.getStatusCode() == HttpRestStatus.OK) {
			try {
				StatisticsUtil.addStatisticsSucc(RedisDef.STATISTICS.BANK_RECHARGE, bankTranWebServiceRechargeControllerRequest.getBankId());
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		return new ResponseRestEntity<BankTranWebServiceRechargeControllerResponse>(bankTranWebServiceRechargeControllerResponse, HttpRestStatus.OK);
	}

	@ApiOperation(value = "do process webservice bankTran charge", notes = "")
	@RequestMapping(value = "/reverse", method = RequestMethod.POST)
	public ResponseRestEntity<BankTranWebServiceReverseControllerResponse> reverse(@RequestBody BankTranWebServiceReverseControllerRequest bankTranWebServiceReverseControllerRequest) {
		try {
			StatisticsUtil.addStatistics(RedisDef.STATISTICS.BANK_REVERSE, bankTranWebServiceReverseControllerRequest.getBankId());
		} catch (Exception e) {
			log.warn("", e);
			return new ResponseRestEntity<>(HttpRestStatus.PAY_STATISTICS_EROOR);
		}
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.WEBSERVICE_BANK_REVERSE);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		BankTranWebserviceReverseRequest bankTranWebserviceReverseRequest = new BankTranWebserviceReverseRequest();
		// bankTranWebServiceRechargeRequest.setPayAppId("10005");
		bankTranWebserviceReverseRequest.setBankId(bankTranWebServiceReverseControllerRequest.getBankId());
		bankTranWebserviceReverseRequest.setOrderNo(bankTranWebServiceReverseControllerRequest.getRefNo());
		bankTranWebserviceReverseRequest.setTradeType("" + MessageDef.TRADE_TYPE.BANK_REVERSE);
		bankTranWebserviceReverseRequest.setIpAddress(bankTranWebServiceReverseControllerRequest.getIpAddress());
		bankTranWebserviceReverseRequest.setReverseRefNo(bankTranWebServiceReverseControllerRequest.getReverseRefNo());

		ResponseRestEntity<BankTranWebserviceReverseResponse> reponse = (ResponseRestEntity<BankTranWebserviceReverseResponse>) submitPayProcess.process(bankTranWebserviceReverseRequest);
		BankTranWebServiceReverseControllerResponse bankTranWebServiceReverseControllerResponse = new BankTranWebServiceReverseControllerResponse();

		bankTranWebServiceReverseControllerResponse.setResultCode(doResultCode(reponse.getStatusCode()));
		if (reponse.getStatusCode() == HttpRestStatus.OK) {
			try {
				StatisticsUtil.addStatisticsSucc(RedisDef.STATISTICS.BANK_REVERSE, bankTranWebserviceReverseRequest.getBankId());
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		return new ResponseRestEntity<BankTranWebServiceReverseControllerResponse>(bankTranWebServiceReverseControllerResponse, HttpRestStatus.OK);
	}

	private SOAPResultCode doResultCode(HttpRestStatus httpRestStatus) {
		switch (httpRestStatus) {
		case PAY_ORDER_NO_SAME_SUBMIT:// 重复提交
			return SOAPResultCode.REFERENCE_NUMBER_IS_SAME_DURING_CONFIG_TIME;
		case PAY_PAYAPPID_NOTEMPTY:// 授权通道
			return SOAPResultCode.PAY_APP_NOT_AUTH;
		case PAY_PAYAPPID_NOTEXIST:// 授权通道
			return SOAPResultCode.PAY_APP_NOT_AUTH;
		case PAY_PAYAPPID_NOTENABLE:// 授权通道
			return SOAPResultCode.PAY_APP_NOT_AUTH;
		case PAY_ORDERNO_NOTEMPTY:// 订单号不能为空
			return SOAPResultCode.REFERENCE_NUMBER_IS_EMPTY;
		case PAY_ORDERNO_LENGTH:// 订单号长度超长
			return SOAPResultCode.REFERENCE_NUMBER_LENGTH;
		case PAY_TRADE_TYPE_NOT_EXIST:// 交易类型不存在
			return SOAPResultCode.PAY_TRADE_TYPE_NOT_EXIST;
		case PAY_GATEWAY_NOTEXIST:// 交易网关不存在
			return SOAPResultCode.PAY_GATEWAY_NOTEXIST;
		case PAY_WEBSERVICE_VID_NOT_EMPTY:// vid不能为空
			return SOAPResultCode.VID_IS_EMPTY;
		case PAY_WEBSERVICE_VID_NOT_EXIST:// vid不存在
			return SOAPResultCode.VID_IS_NOTEXIST;
		case PAY_PAY_USER_DISABLE:// 用户不可用
			return SOAPResultCode.PAY_PAY_USER_DISABLE;
		case PAY_PAY_USER_LOCKED:// 用户不可用
			return SOAPResultCode.PAY_PAY_USER_LOCKED;
		case PAY_PAY_AMOUNT_NOTEMPTY:// 金额不能为空
			return SOAPResultCode.AMOUNT_IS_EMPTY;
		case PAY_PAY_AMOUNT_GREATER_THEN_ZERO:// 金额必须大于0
			return SOAPResultCode.PAY_PAY_AMOUNT_GREATER_THEN_ZERO;
		case PAY_WEBSERVICE_PAYMENTTIME_NOT_EMPTY:// 支付时间不能为空
			return SOAPResultCode.PAYMENT_TIME_IS_EMPTY;
		case PAY_PAY_GATEWAY_NOTEXIST:// 交易网关不存在
			return SOAPResultCode.PAY_GATEWAY_NOTEXIST;
		case PAY_ERROR:// 交易失败
			return SOAPResultCode.PAY_ERROR;
		case PAY_WEBSERVICE_REVERSE_REFNO_NOT_EMPTY:// 取消订单号为空
			return SOAPResultCode.REVERSE_REFERENCE_NUMBER_IS_EMPTY;
		case PAY_WEBSERVICE_REVERSE_REFNO_NOT_EXIST:// 取消订单号不存在
			return SOAPResultCode.REVERSE_REFERENCE_NUMBER_NOTEXIST;
		case PAY_WEBSERVICE_REVERSE_REFNO_SELECT_ERROR:// 取消订单号查询出错
			return SOAPResultCode.REVERSE_REFERENCE_NUMBER_NOTEXIST;
		case PAY_PAY_USER_NOEXIST:// 支付用户不存在
			return SOAPResultCode.PAY_PAY_USER_NOEXIST;
		case PAY_ACCOUNT_AMOUNT_NOT_ENOUGH:// 余额不足
			return SOAPResultCode.PAY_ACCOUNT_AMOUNT_NOT_ENOUGH;
		case PAY_WEBSERVICE_REVERSE_REFNO_ISREVERSEED:// 已取消
			return SOAPResultCode.PAY_WEBSERVICE_REVERSE_REFNO_ISREVERSEED;
		case PAY_WEBSERVICE_REVERSE_REFNO_ONLY_BANK_RECHARGE:// 只能取消银行充值
			return SOAPResultCode.PAY_WEBSERVICE_REVERSE_REFNO_ONLY_BANK_RECHARGE;
		case PAY_PAY_USER_NOTACTIVE:// 用户没有激活
			return SOAPResultCode.PAY_PAY_USER_NOTACTIVE;
		case PAY_PAY_USER_DEFAULT_LOGIN_PASSWORD:// 用户修改登录密码
			return SOAPResultCode.PAY_PAY_USER_DEFAULT_LOGIN_PASSWORD;
		case PAY_PAY_USER_DEFAULT_PAY_PASSWORD:// 用户没有修改支付密码
			return SOAPResultCode.PAY_PAY_USER_DEFAULT_PAY_PASSWORD;
		case PAY_WEBSERVICE_BANKID_NOTEMPTY:// 银行id不能为空
			return SOAPResultCode.BANK_ID_NOTEMPTY;
		case PAY_FRAUD_WARNING:// 风控警告
			return SOAPResultCode.FRAUD_WARNING;
		case PAY_FRAUD_ERROR:// 风控错误
			return SOAPResultCode.FRAUD_ERROR;
		case PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE:// 超出充值每日限额
			return SOAPResultCode.PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE;
		case PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE:// 超出充值每月限额
			return SOAPResultCode.PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE;
		case PAY_ORDER_NO_CHECK_ERROR:// 查询orderNo出错
			return SOAPResultCode.PAY_ORDER_NO_CHECK_ERROR;
		case PAY_PATRIMONY_CARD_CODE_NOT_MATCH:// patrimony card code 没有匹配上
			return SOAPResultCode.PAY_PATRIMONY_CARD_CODE_NOT_MATCH;

		case OK:// 成功
			return SOAPResultCode.OK;
		default:
			return SOAPResultCode.PROCESS_RETURN_ERROR;
		}
	}
}