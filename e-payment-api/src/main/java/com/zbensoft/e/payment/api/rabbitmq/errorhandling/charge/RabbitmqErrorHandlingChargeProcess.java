package com.zbensoft.e.payment.api.rabbitmq.errorhandling.charge;

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
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingChargeVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

@Component
@RabbitListener(queues = RabbitmqDef.CHARGE_ERROR_HANDLING.ERRORHANDLING_QUEUE)
public class RabbitmqErrorHandlingChargeProcess {

	@RabbitHandler
	public void process(ErrorHandlingChargeVo errorHandlingChargeVo) throws Exception {
		if(SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.COMMON_MAINTEN) == 1){
			throw new Exception("MAINTEN");
		}
		try {
			if (errorHandlingChargeVo != null) {
				ERROR_HANDLING_LOG.INFO(String.format("Recv  =%s", errorHandlingChargeVo.toString()));

				
				ErrorHandlingProcess errorHandlingProcess = ErrorHandlingProcessFactory.getInstance().get(ErrorHandlingType.CHARGE);
				if (errorHandlingProcess.process(errorHandlingChargeVo)) {
					ERROR_HANDLING_LOG.INFO(String.format("RabbitmqErrorHandlingChargeReconciliationProcess succ  %s", errorHandlingChargeVo.toString()));
				} else {
					ERROR_HANDLING_LOG.INFO(String.format("RabbitmqErrorHandlingChargeReconciliationProcess fail  %s", errorHandlingChargeVo.toString()));
				}
			} else {
				ERROR_HANDLING_LOG.INFO(String.format("RabbitmqErrorHandlingChargeReconciliationProcess is null"));
			}
		} catch (Exception e) {
			BOOKKEEPING_LOG.ERROR("RabbitmqErrorHandlingChargeReconciliationProcess exception", e);
		}
	}
}
