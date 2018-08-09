package com.zbensoft.e.payment.api.rabbitmq.cdr.bankrecharge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.CDR_LOG;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 只有差错才写cdr
 * @author xieqiang
 *
 */
@Component
@RabbitListener(queues = RabbitmqDef.ERROR_HANDLING_BOOKKEEPING.CDR_QUEUE)
public class RabbitmqCDRBankRechargeErrorHandlingProcess {

	private static final Logger log = LoggerFactory.getLogger(RabbitmqCDRBankRechargeErrorHandlingProcess.class);

	@RabbitHandler
	public void process(TradeInfo tradeInfo) throws Exception {
		if(SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.COMMON_MAINTEN) == 1){
			throw new Exception("MAINTEN");
		}
		try {
			if (tradeInfo != null) {
				CDR_LOG.INFO(tradeInfo.toCDR());
			} else {
				log.info(String.format("RabbitmqCDRErrorHandlingProcess is null"));
			}
		} catch (Exception e) {
			log.error("RabbitmqCDRErrorHandlingProcess exception", e);
		}
	}
}
