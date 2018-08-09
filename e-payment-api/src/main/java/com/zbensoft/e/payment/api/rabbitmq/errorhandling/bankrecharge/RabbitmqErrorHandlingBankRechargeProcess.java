package com.zbensoft.e.payment.api.rabbitmq.errorhandling.bankrecharge;

import org.e.payment.core.pay.errorHandling.ErrorHandlingProcess;
import org.e.payment.core.pay.errorHandling.ErrorHandlingProcessFactory;
import org.e.payment.core.pay.errorHandling.ErrorHandlingType;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.api.log.ERROR_HANDLING_LOG;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

/**
 * 银行充值差错处理
 * 
 * @author xieqiang
 *
 */
@Component
@RabbitListener(queues = RabbitmqDef.ERROR_HANDLING.ERRORHANDLING_QUEUE)
public class RabbitmqErrorHandlingBankRechargeProcess {

	@RabbitHandler
	public void process(ErrorHandlingVo errorHandlingVo) throws Exception {
		if(SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.COMMON_MAINTEN) == 1){
			throw new Exception("MAINTEN");
		}
		try {
			if (errorHandlingVo != null) {
				ERROR_HANDLING_LOG.INFO(String.format("Recv  =%s", errorHandlingVo.toString()));

				ErrorHandlingProcess errorHandlingProcess = ErrorHandlingProcessFactory.getInstance().get(ErrorHandlingType.BANK_RECHARGE);
				if (errorHandlingProcess == null) {
					ERROR_HANDLING_LOG.INFO(String.format("TradeInfo get errorHandlingProcess is null, %s", errorHandlingVo.toString()));
				}
				if (errorHandlingProcess.process(errorHandlingVo)) {
					ERROR_HANDLING_LOG.INFO(String.format("RabbitmqErrorHandlingReconciliationProcess succ  %s", errorHandlingVo.toString()));
				} else {
					ERROR_HANDLING_LOG.INFO(String.format("RabbitmqErrorHandlingReconciliationProcess fail  %s", errorHandlingVo.toString()));
				}
			} else {
				ERROR_HANDLING_LOG.INFO(String.format("RabbitmqErrorHandlingReconciliationProcess is null"));
			}
		} catch (Exception e) {
			BOOKKEEPING_LOG.ERROR("RabbitmqErrorHandlingReconciliationProcess exception", e);
		}
	}
}
