package com.biluo.listener;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biluo.constant.QueueConstants;
import com.biluo.domain.Order;
import com.biluo.model.ChangeStock;
import com.biluo.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 订单死信队列监听
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderDeadListener {
	private final OrderService orderService;

	@RabbitListener(queues = QueueConstants.ORDER_DEAD_QUEUE)
	public void handlerOrderDeadMsg(Message message, Channel channel) {
		// 获取消息
		JSONObject jsonObject = JSONObject.parseObject(new String(message.getBody()));
		// 获取订单编号
		String orderNumber = jsonObject.getString("orderNumber");
		// 获取商品数量对象
		ChangeStock changeStock = jsonObject.getObject("changeStock", ChangeStock.class);

		// 根据订单编号查询订单
		Order order = orderService.lambdaQuery().eq(Order::getOrderNumber, orderNumber).one();

		// 判断订单是否存在
		if (ObjectUtil.isNull(order)) {
			log.error("订单编号{}无效", orderNumber);
			try {
				// 签收消息
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (IOException e) {
				log.error("消息签收失败：{}", e.getMessage());
				throw new RuntimeException(e);
			}
			return;
		}
		// 判断订单是否已支付
		if (1 == order.getIsPayed()) {  // 如果已经支付
			try {
				// 签收消息
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (IOException e) {
				log.error("消息签收失败：{}", e.getMessage());
				throw new RuntimeException(e);
			}
			return;
		}

		// 如果未支付，则调用第三方的订单查询接口，查询订单支付情况；如果订单已支付，签收消息结束，否则订单数据回滚
		// 由于这里没法使用微信的支付方法，所以这里先假设：当前订单的确没有支付
		try {
			// 订单回滚
			orderService.orderRollBack(order, changeStock);
			// 签收消息
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			log.error("消息签收失败：{}", e.getMessage());
			throw new RuntimeException(e);
		}

	}
}
