package org.e.payment.core.pay.submit.impl.errorHandling.charge;

import java.util.Calendar;

import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankChargeErrorRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankChargeErrorResponse;
import org.e.payment.core.pay.submit.vo.ErrorHandlingChargeRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingChargeResponse;
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
public class ErrorHandlingChargeEpayErrorProcess extends AbsSubmitPayProcess {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandlingChargeEpayErrorProcess.class);

	@Override
	public ResponseRestEntity<ErrorHandlingChargeResponse> payProcess(Object request) {

		validateRequestClass(request != null && request instanceof ErrorHandlingBankChargeErrorRequest);
		if (isErrorResponse()) {
			return response;
		}

		ErrorHandlingChargeRequest errorHandlingChargeRequest = (ErrorHandlingChargeRequest) request;
		

		ErrorHandlingChargeResponse errorHandlingChargeResponse = new ErrorHandlingChargeResponse();
		
		return new ResponseRestEntity<ErrorHandlingChargeResponse>(errorHandlingChargeResponse, HttpRestStatus.OK);
	}

}
