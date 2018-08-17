package org.e.payment.core.pay.submit.impl.errorHandling.charge;

import java.util.Calendar;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankChargeErrorRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankChargeErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 补款处理-银行提现，银行有，epay有，但是银行提现返回失败，所以补款
 * 
 * @author wangchenyang
 *
 */
public class ErrorHandlingChargeRefundPayProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandlingChargeRefundPayProcess.class);

	@Override
	public ResponseRestEntity<ErrorHandlingBankChargeErrorResponse> payProcess(Object request) {

		validateRequestClass(request != null && request instanceof ErrorHandlingBankChargeErrorRequest);
		if (isErrorResponse()) {
			return response;
		}

		ErrorHandlingBankChargeErrorRequest errorHandlingBankChargeErrorRequest = (ErrorHandlingBankChargeErrorRequest) request;

		rollbackUserAccountForChargeRefund(errorHandlingBankChargeErrorRequest.getTradeInfo());
		if (isErrorResponse()) {
			return response;
		}
		
		//更新交易数据
		TradeInfo submitTradeInfo = errorHandlingBankChargeErrorRequest.getTradeInfo();
		submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.FAIL);
		submitTradeInfo.setErrorCode(MessageDef.TRADE_ERROR_CODE.CHARGE_EPAY_RESULT_FAILED);
		submitTradeInfo.setEndTime(Calendar.getInstance().getTime());
		submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.CLOSE);
		
		try {
			tradeInfoService.updateByPrimaryKey(submitTradeInfo);
		} catch (Exception e) {
			MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingChargeRefundPayProcess tradeInfoService.updateByPrimaryKey", JSONObject.toJSONString(submitTradeInfo)));
			log.error("", e);
			return new ResponseRestEntity<ErrorHandlingBankChargeErrorResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
		}

		ErrorHandlingBankChargeErrorResponse errorHandlingBankErrorResponse = new ErrorHandlingBankChargeErrorResponse();
		errorHandlingBankErrorResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
		
		return new ResponseRestEntity<ErrorHandlingBankChargeErrorResponse>(errorHandlingBankErrorResponse, HttpRestStatus.OK);
	}

}
