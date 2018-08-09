package com.zbensoft.e.payment.api.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zbensoft.e.payment.api.common.RabbitmqDef;

@Configuration
public class ChargeErrorHandlingBookKeepingRabbitmqConfig {
	private String EXCHANGE = RabbitmqDef.CHARGE_ERROR_HANDLING_BOOKKEEPING.EXCHANGE;

	private String BOOKKEEPING_QUEUE = RabbitmqDef.CHARGE_ERROR_HANDLING_BOOKKEEPING.BOOKKEEPING_QUEUE;
	private String CDR_QUEUE = RabbitmqDef.CHARGE_ERROR_HANDLING_BOOKKEEPING.CDR_QUEUE;
	// 信道配置

	@Bean
	FanoutExchange chargeErrorHandlingBookKeepingFanoutExchange() {
		return new FanoutExchange(EXCHANGE);
	}

	@Bean
	public Queue chargeErrorHandlingBookKeepingQueue() {
		Queue queue = new Queue(BOOKKEEPING_QUEUE, true);
		return queue;
	}

	@Bean
	public Queue chargeErrorHandlingCDRQueue() {
		Queue queue = new Queue(CDR_QUEUE, true);
		return queue;
	}

	@Bean
	Binding bindingExchangeChargeErrorHandlingBookKeepingQueue(Queue chargeErrorHandlingBookKeepingQueue, FanoutExchange chargeErrorHandlingBookKeepingFanoutExchange) {
		return BindingBuilder.bind(chargeErrorHandlingBookKeepingQueue).to(chargeErrorHandlingBookKeepingFanoutExchange);
	}

	@Bean
	Binding bindingExchangeChargeErrorHandlingCDRQueue(Queue chargeErrorHandlingCDRQueue, FanoutExchange chargeErrorHandlingBookKeepingFanoutExchange) {
		return BindingBuilder.bind(chargeErrorHandlingCDRQueue).to(chargeErrorHandlingBookKeepingFanoutExchange);
	}

}
