package com.zbensoft.e.payment.api.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zbensoft.e.payment.api.common.RabbitmqDef;

@Configuration
public class ReconciliationRabbitmqConfig {
	private String EXCHANGE = RabbitmqDef.RECONCILIATION.EXCHANGE;

	private String BOOKKEEPING_QUEUE = RabbitmqDef.RECONCILIATION.BOOKKEEPING_QUEUE;
	// 信道配置

	@Bean
	FanoutExchange reconciliationFanoutExchange() {
		return new FanoutExchange(EXCHANGE);
	}

	@Bean
	public Queue reconciliationBookKeepingQueue() {
		Queue queue = new Queue(BOOKKEEPING_QUEUE, true);
		return queue;
	}

	@Bean
	Binding bindingExchangeReconciliationBookKeeping(Queue reconciliationBookKeepingQueue, FanoutExchange reconciliationFanoutExchange) {
		return BindingBuilder.bind(reconciliationBookKeepingQueue).to(reconciliationFanoutExchange);
	}

}
