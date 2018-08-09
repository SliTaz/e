package com.zbensoft.e.payment.api.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zbensoft.e.payment.api.common.RabbitmqDef;

@Configuration
public class ErrorHandlingBookKeepingRabbitmqConfig {
	private String EXCHANGE = RabbitmqDef.ERROR_HANDLING_BOOKKEEPING.EXCHANGE;

	private String BOOKKEEPING_QUEUE = RabbitmqDef.ERROR_HANDLING_BOOKKEEPING.BOOKKEEPING_QUEUE;
	private String CDR_QUEUE = RabbitmqDef.ERROR_HANDLING_BOOKKEEPING.CDR_QUEUE;
	// 信道配置

	@Bean
	FanoutExchange errorHandlingBookKeepingFanoutExchange() {
		return new FanoutExchange(EXCHANGE);
	}

	@Bean
	public Queue errorHandlingBookKeepingQueue() {
		Queue queue = new Queue(BOOKKEEPING_QUEUE, true);
		return queue;
	}

	@Bean
	public Queue errorHandlingCDRQueue() {
		Queue queue = new Queue(CDR_QUEUE, true);
		return queue;
	}

	@Bean
	Binding bindingExchangeErrorHandlingBookKeepingQueue(Queue errorHandlingBookKeepingQueue, FanoutExchange errorHandlingBookKeepingFanoutExchange) {
		return BindingBuilder.bind(errorHandlingBookKeepingQueue).to(errorHandlingBookKeepingFanoutExchange);
	}

	@Bean
	Binding bindingExchangeErrorHandlingCDRQueue(Queue errorHandlingCDRQueue, FanoutExchange errorHandlingBookKeepingFanoutExchange) {
		return BindingBuilder.bind(errorHandlingCDRQueue).to(errorHandlingBookKeepingFanoutExchange);
	}

}
