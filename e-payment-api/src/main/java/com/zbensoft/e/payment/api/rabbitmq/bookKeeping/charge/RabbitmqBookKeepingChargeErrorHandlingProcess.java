package com.zbensoft.e.payment.api.rabbitmq.bookKeeping.charge;

import org.e.payment.core.pay.bookKeeping.BookKeepingProcess;
import org.e.payment.core.pay.bookKeeping.BookKeepingProcessFactory;
import org.e.payment.core.pay.bookKeeping.BookKeepingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 银行提现差错处理-记账
 * @author Wangchenyang
 *
 */
@Component
@RabbitListener(queues = RabbitmqDef.CHARGE_ERROR_HANDLING_BOOKKEEPING.BOOKKEEPING_QUEUE)
public class RabbitmqBookKeepingChargeErrorHandlingProcess {
	
	private static final Logger log = LoggerFactory.getLogger(RabbitmqBookKeepingChargeErrorHandlingProcess.class);

	@RabbitHandler
	public void process(TradeInfo tradeInfo) throws Exception {
		if(SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.COMMON_MAINTEN) == 1){
			throw new Exception("MAINTEN");
		}
		try {
			if (tradeInfo != null) {
				BOOKKEEPING_LOG.INFO(String.format("Recv  =%s", tradeInfo.toString()));

				BookKeepingProcess bookKeepingProcess = BookKeepingProcessFactory.getInstance().get(BookKeepingType.CHARGE_ERROR_HANDLING);
				if (bookKeepingProcess == null) {
					BOOKKEEPING_LOG.INFO(String.format("TradeInfo get TradeProcess is null, %s", tradeInfo.toString()));
				}
				if (bookKeepingProcess.process(tradeInfo)) {
					BOOKKEEPING_LOG.INFO(String.format("RabbitmqBookKeepingChargeErrorHandlingProcess succ  %s", tradeInfo.toString()));
				} else {
					BOOKKEEPING_LOG.INFO(String.format("RabbitmqBookKeepingChargeErrorHandlingProcess fail  %s", tradeInfo.toString()));
				}
			} else {
				BOOKKEEPING_LOG.INFO(String.format("RabbitmqBookKeepingChargeErrorHandlingProcess is null"));
			}
		} catch (Exception e) {
			log.error("RabbitmqBookKeepingChargeErrorHandlingProcess exception", e);
			BOOKKEEPING_LOG.ERROR("RabbitmqBookKeepingChargeErrorHandlingProcess exception", e);
		}
	}
}
