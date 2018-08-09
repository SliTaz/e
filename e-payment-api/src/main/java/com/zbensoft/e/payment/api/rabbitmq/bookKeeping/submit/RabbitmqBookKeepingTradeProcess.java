package com.zbensoft.e.payment.api.rabbitmq.bookKeeping.submit;

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
 * 用户提交的交易，记账
 * 
 * @author xieqiang
 *
 */
@Component
@RabbitListener(queues = RabbitmqDef.TRADE.BOOKKEEPING_QUEUE)
public class RabbitmqBookKeepingTradeProcess {

	private static final Logger log = LoggerFactory.getLogger(RabbitmqBookKeepingTradeProcess.class);

	@RabbitHandler
	public void process(TradeInfo tradeInfo) throws Exception {
		if (SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.COMMON_MAINTEN) == 1) {
			throw new Exception("MAINTEN");
		}
		try {
			if (tradeInfo != null) {
				BOOKKEEPING_LOG.INFO(String.format("Recv Trade info =%s", tradeInfo.toString()));
				BookKeepingProcess bookKeepingProcess = BookKeepingProcessFactory.getInstance().get(BookKeepingType.TRADE);
				if (bookKeepingProcess == null) {
					BOOKKEEPING_LOG.INFO(String.format("TradeInfo get TradeProcess is null, Trade info =%s", tradeInfo.toString()));
				}
				if (bookKeepingProcess.process(tradeInfo)) {
					BOOKKEEPING_LOG.INFO(String.format("RabbitmqBookKeepingTradeProcess succ trade info = %s", tradeInfo.toString()));
				} else {
					BOOKKEEPING_LOG.INFO(String.format("RabbitmqBookKeepingTradeProcess fail trade info = %s", tradeInfo.toString()));
				}
			} else {
				BOOKKEEPING_LOG.INFO(String.format("RabbitmqBookKeepingTradeProcess is null"));
			}
		} catch (Exception e) {
			log.error("RabbitmqBookKeepingTradeProcess exception", e);
			BOOKKEEPING_LOG.ERROR("RabbitmqBookKeepingTradeProcess exception", e);
		}
	}
}
