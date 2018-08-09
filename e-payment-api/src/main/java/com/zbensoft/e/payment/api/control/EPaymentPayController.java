package com.zbensoft.e.payment.api.control;

import javax.annotation.Resource;

import org.e.payment.core.pay.ProcessType;
import org.e.payment.core.pay.submit.SubmitPayProcess;
import org.e.payment.core.pay.submit.SubmitPayProcessFactory;
import org.e.payment.core.pay.submit.vo.GetPayGateWayRequest;
import org.e.payment.core.pay.submit.vo.GetPayGateWayResponse;
import org.e.payment.core.pay.submit.vo.SubmitTradeConsumptionAppRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeConsumptionAppResponse;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeAppRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeAppResponse;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeRechargeResponse;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferBankRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferBankResponse;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeTransferResponse;
import org.e.payment.core.pay.submit.vo.SubmitTradeWithdrawCashRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeWithdrawCashResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.api.service.api.ConsumerCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserBankCardService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserBankCardService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.PayAppGatewayService;
import com.zbensoft.e.payment.api.service.api.PayGatewayService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/epayment")
@RestController
public class EPaymentPayController {

	private static final Logger log = LoggerFactory.getLogger(EPaymentPayController.class);

	@Autowired
	TradeInfoService tradeInfoService;

	@Autowired
	ConsumerUserService consumerUserService;

	@Autowired
	MerchantUserService merchantUserService;

	@Autowired
	PayAppGatewayService payAppGatewayService;

	@Autowired
	PayGatewayService payGatewayService;

	@Autowired
	ConsumerUserBankCardService consumerUserBankCardService;

	@Autowired
	MerchantUserBankCardService merchantUserBankCardService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@Autowired
	ConsumerCouponService consumerCouponService;
	@Autowired
	ConsumerFamilyCouponService consumerFamilyCouponService;

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "submit trade Recharge", notes = "")
	@RequestMapping(value = "/submittradeRecharge", method = RequestMethod.POST)
	public ResponseRestEntity<SubmitTradeRechargeResponse> submitTradeRecharge(@RequestBody SubmitTradeRechargeRequest submitTradeRechargeRequest) {
		try {
			StatisticsUtil.addStatistics(RedisDef.STATISTICS.RECHARGE, null);
		} catch (Exception e) {
			log.warn("", e);
			return new ResponseRestEntity<>(HttpRestStatus.PAY_STATISTICS_EROOR);
		}
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.RECHARGE);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		ResponseRestEntity<SubmitTradeRechargeResponse> response = (ResponseRestEntity<SubmitTradeRechargeResponse>) submitPayProcess.process(submitTradeRechargeRequest);
		if (response != null && response.getStatusCode() == HttpRestStatus.OK) {
			try {
				StatisticsUtil.addStatisticsSucc(RedisDef.STATISTICS.RECHARGE, null);
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "submit trade Withdraw Cash", notes = "")
	@RequestMapping(value = "/submittradeWithdrawCash", method = RequestMethod.POST)
	public ResponseRestEntity<SubmitTradeWithdrawCashResponse> submittradeWithdrawCash(@RequestBody SubmitTradeWithdrawCashRequest submitTradeWithdrawCashRequest) {
		try {
			StatisticsUtil.addStatistics(RedisDef.STATISTICS.CHARGE, null);
		} catch (Exception e) {
			log.warn("", e);
			return new ResponseRestEntity<>(HttpRestStatus.PAY_STATISTICS_EROOR);
		}

		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.CHARGE);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		ResponseRestEntity<SubmitTradeWithdrawCashResponse> response = (ResponseRestEntity<SubmitTradeWithdrawCashResponse>) submitPayProcess.process(submitTradeWithdrawCashRequest);
		if (response != null && response.getStatusCode() == HttpRestStatus.OK) {
			try {
				StatisticsUtil.addStatisticsSucc(RedisDef.STATISTICS.CHARGE, null);
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "submit trade Withdraw Cash", notes = "")
	@RequestMapping(value = "/submittradeTransfer", method = RequestMethod.POST)
	public ResponseRestEntity<SubmitTradeTransferResponse> submittradeTransfer(@RequestBody SubmitTradeTransferRequest submitTradeTransferRequest) {
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.TRANSFER);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		return (ResponseRestEntity<SubmitTradeTransferResponse>) submitPayProcess.process(submitTradeTransferRequest);
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "submit trade Withdraw Cash", notes = "")
	@RequestMapping(value = "/submittradeTransferBank", method = RequestMethod.POST)
	public ResponseRestEntity<SubmitTradeTransferBankResponse> submittradeTransferBank(@RequestBody SubmitTradeTransferBankRequest submitTradeTransferBankRequest) {
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.TRANSFER_BANK);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		return (ResponseRestEntity<SubmitTradeTransferBankResponse>) submitPayProcess.process(submitTradeTransferBankRequest);
	}

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "submit trade Recharge App", notes = "")
	@RequestMapping(value = "/submittradeRechargeApp", method = RequestMethod.POST)
	public ResponseRestEntity<SubmitTradeRechargeAppResponse> submitTradeRechargeApp(@RequestBody SubmitTradeRechargeAppRequest submitTradeRechargeAppRequest) {
		try {
			StatisticsUtil.addStatistics(RedisDef.STATISTICS.RECHARGE, null);
		} catch (Exception e) {
			log.warn("", e);
			return new ResponseRestEntity<>(HttpRestStatus.PAY_STATISTICS_EROOR);
		}
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.RECHARGE_APP);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		ResponseRestEntity<SubmitTradeRechargeAppResponse> response = (ResponseRestEntity<SubmitTradeRechargeAppResponse>) submitPayProcess.process(submitTradeRechargeAppRequest);
		if (response != null && response.getStatusCode() == HttpRestStatus.OK) {
			try {
				StatisticsUtil.addStatisticsSucc(RedisDef.STATISTICS.RECHARGE, null);
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		return response;
	}

	@PreAuthorize("hasRole('MERCHANT')")
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "submit trade Withdraw Cash", notes = "")
	@RequestMapping(value = "/submittradeConsumptionApp", method = RequestMethod.POST)
	public ResponseRestEntity<SubmitTradeConsumptionAppResponse> submittradeConsumptionApp(@RequestBody SubmitTradeConsumptionAppRequest submitTradeConsumptionAppRequest) {
		try {
			StatisticsUtil.addStatistics(RedisDef.STATISTICS.CONSUMPTION, null);
		} catch (Exception e) {
			log.warn("", e);
			return new ResponseRestEntity<>(HttpRestStatus.PAY_STATISTICS_EROOR);
		}
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.CONSUMPTION_APP);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		ResponseRestEntity<SubmitTradeConsumptionAppResponse> response = (ResponseRestEntity<SubmitTradeConsumptionAppResponse>) submitPayProcess.process(submitTradeConsumptionAppRequest);
		if (response != null && response.getStatusCode() == HttpRestStatus.OK) {
			try {
				StatisticsUtil.addStatisticsSucc(RedisDef.STATISTICS.CONSUMPTION, null);
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		return response;
	}
	@PreAuthorize("hasRole('CONSUMER') or hasRole('MERCHANT')")
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "crete trade", notes = "")
	@RequestMapping(value = "/getPayGateWay", method = RequestMethod.POST)
	public ResponseRestEntity<GetPayGateWayResponse> getPayGateWay(@RequestBody GetPayGateWayRequest getPayGateWayRequest) {
		SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.GATEWAY);
		if (submitPayProcess == null) {
			return new ResponseRestEntity<>(HttpRestStatus.PAY_NOT_SUPPORT_TYPE);
		}
		return (ResponseRestEntity<GetPayGateWayResponse>) submitPayProcess.process(getPayGateWayRequest);
	}

}