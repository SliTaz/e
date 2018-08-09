package com.zbensoft.e.payment.api.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zbensoft.e.payment.api.common.RabbitmqDef;

@Configuration
public class ChargeReconciliationRabbitmqConfig {
	private String EXCHANGE = RabbitmqDef.CHARGE_RECONCILIATION.EXCHANGE;

	private String BOOKKEEPING_QUEUE = RabbitmqDef.CHARGE_RECONCILIATION.BOOKKEEPING_QUEUE;
	// 信道配置

	@Bean
	FanoutExchange chargeReconciliationFanoutExchange() {
		return new FanoutExchange(EXCHANGE);
	}

	@Bean
	public Queue chargeReconciliationBookKeepingQueue() {
		Queue queue = new Queue(BOOKKEEPING_QUEUE, true);
		return queue;
	}

	@Bean
	Binding bindingExchangeChargeReconciliationBookKeeping(Queue chargeReconciliationBookKeepingQueue, FanoutExchange chargeReconciliationFanoutExchange) {
		return BindingBuilder.bind(chargeReconciliationBookKeepingQueue).to(chargeReconciliationFanoutExchange);
	}

}
