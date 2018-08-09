package com.zbensoft.e.payment.api.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zbensoft.e.payment.api.common.RabbitmqDef;

@Configuration
public class ErrorHandlingRabbitmqConfig {
	private String EXCHANGE = RabbitmqDef.ERROR_HANDLING.EXCHANGE;

	private String BOOKKEEPING_QUEUE = RabbitmqDef.ERROR_HANDLING.ERRORHANDLING_QUEUE;
	// 信道配置

	@Bean
	FanoutExchange errorhandlingFanoutExchange() {
		return new FanoutExchange(EXCHANGE);
	}

	@Bean
	public Queue errorhandlingErrorhandlingQueue() {
		Queue queue = new Queue(BOOKKEEPING_QUEUE, true);
		return queue;
	}

	@Bean
	Binding bindingExchangeErrorhandlingBookKeepingQueue(Queue errorhandlingErrorhandlingQueue, FanoutExchange errorhandlingFanoutExchange) {
		return BindingBuilder.bind(errorhandlingErrorhandlingQueue).to(errorhandlingFanoutExchange);
	}

}
