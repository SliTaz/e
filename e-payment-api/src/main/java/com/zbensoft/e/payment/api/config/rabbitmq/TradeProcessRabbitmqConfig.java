package com.zbensoft.e.payment.api.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zbensoft.e.payment.api.common.RabbitmqDef;

@Configuration
public class TradeProcessRabbitmqConfig {
	private String EXCHANGE = RabbitmqDef.TRADE.EXCHANGE;

	private String BOOKKEEPING_QUEUE = RabbitmqDef.TRADE.BOOKKEEPING_QUEUE;
	private String CDR_QUEUE = RabbitmqDef.TRADE.CDR_QUEUE;
	// 信道配置

	@Bean
	FanoutExchange fanoutExchange() {
		return new FanoutExchange(EXCHANGE);
	}

	@Bean
	public Queue bookKeepingQueue() {
		Queue queue = new Queue(BOOKKEEPING_QUEUE, true);
		return queue;
	}

	@Bean
	public Queue CDRQueue() {
		Queue queue = new Queue(CDR_QUEUE, true);
		return queue;
	}

	@Bean
	Binding bindingExchangeBookKeeping(Queue bookKeepingQueue, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(bookKeepingQueue).to(fanoutExchange);
	}

	@Bean
	Binding bindingExchangeCDRQueue(Queue CDRQueue, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(CDRQueue).to(fanoutExchange);
	}

}
