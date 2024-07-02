package com.biluo.config;

import com.biluo.constant.QueueConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单队列配置
 */
@Configuration
public class OrderQueueConfig {

	/**
	 * 配置死信队列
	 *
	 * @return
	 */
	@Bean
	public Queue orderDeadQueue() {
		return new Queue(QueueConstants.ORDER_DEAD_QUEUE);
	}

	/**
	 * 配置交换机
	 *
	 * @return
	 */
	@Bean
	public DirectExchange orderDeadEx() {
		return new DirectExchange(QueueConstants.ORDER_DEAD_EX);
	}

	/**
	 * 配置延迟队列
	 *
	 * @return
	 */
	@Bean
	public Queue orderMsQueue() {
		// 配置延迟队列参数
		Map<String, Object> map = new HashMap<>();
		// 配置消息存活时长
		map.put("x-message-ttl", 60 * 1000);
		// 配置消息死后走的交换机
		map.put("x-dead-letter-exchange", QueueConstants.ORDER_DEAD_EX);
		// 消息死后走的路由key
		map.put("x-dead-letter-routing-key", QueueConstants.ORDER_DEAD_KEY);

		return new Queue(QueueConstants.ORDER_MS_QUEUE, true, false, false, map);
	}

	@Bean
	public Binding orderDeadBind() {
		return BindingBuilder
				.bind(orderDeadQueue())
				.to(orderDeadEx())
				.with(QueueConstants.ORDER_DEAD_KEY);
	}
}
