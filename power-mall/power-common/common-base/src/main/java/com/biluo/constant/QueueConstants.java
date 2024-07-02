package com.biluo.constant;

public interface QueueConstants {
	/**
	 * 死信交换机
	 */
	String ORDER_DEAD_EX = "order.dead.ex";

	/**
	 * 死信队列
	 */
	String ORDER_DEAD_QUEUE = "order.dead.queue";
	/**
	 * 死信队列绑定键
	 */
	String ORDER_DEAD_KEY = "order.dead.key";
	/**
	 * 订单消息队列
	 */
	String ORDER_MS_QUEUE = "order.ms.queue";
}
