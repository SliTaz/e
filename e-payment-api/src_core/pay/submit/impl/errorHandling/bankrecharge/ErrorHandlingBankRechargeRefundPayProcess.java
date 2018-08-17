package org.e.payment.core.pay.submit.impl.errorHandling.bankrecharge;

import java.util.Calendar;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankErrorRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.StatisticsUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 退款处理-银行充值,银行没有，epay有，所以退款
 * 
 * @author xieqiang
 *
 */
public class ErrorHandlingBankRechargeRefundPayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandlingBankRechargeRefundPayProcess.class);

	@Override
	public ResponseRestEntity<ErrorHandlingBankErrorResponse> payProcess(Object request) {

		validateRequestClass(request != null && request instanceof ErrorHandlingBankErrorRequest);
		if (isErrorResponse()) {
			return response;
		}

		ErrorHandlingBankErrorRequest errorHandlingBankErrorRequest = (ErrorHandlingBankErrorRequest) request;
		
		// 退款
		rollbackRecvUserAccountForBankRechargeRefund(errorHandlingBankErrorRequest.getTradeInfo());
		if (isErrorResponse()) {
			return response;
		}

		TradeInfo submitTradeInfo = errorHandlingBankErrorRequest.getTradeInfo();
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.FAIL);
		submitTradeInfo.setErrorCode(MessageDef.TRADE_ERROR_CODE.BANK_PROCESS_FAIL);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

		try {
			tradeInfoService.updateByPrimaryKey(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingBankErrorPayProcess tradeInfoService.updateByPrimaryKey", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<ErrorHandlingBankErrorResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		ErrorHandlingBankErrorResponse errorHandlingBankErrorResponse = new ErrorHandlingBankErrorResponse();
		errorHandlingBankErrorResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		// 
		StatisticsUtil.addStatisticsSuccLimitMonth(RedisDef.STATISTICS_LIMIT_MONTH.MONTHLY_BANK_RECHARGE, submitTradeInfo.getCreateTime(), submitTradeInfo.getPayUserId(),
				DoubleUtil.mul(-1d, submitTradeInfo.getPaySumAmount()));
		return new ResponseRestEntity<ErrorHandlingBankErrorResponse>(errorHandlingBankErrorResponse, HttpRestStatus.OK);
	}

}
