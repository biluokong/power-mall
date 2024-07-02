package com.biluo.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.QueueConstants;
import com.biluo.domain.*;
import com.biluo.ex.handler.BusinessException;
import com.biluo.feign.OrderBasketFeign;
import com.biluo.feign.OrderMemberFeign;
import com.biluo.feign.OrderProdFeign;
import com.biluo.mapper.OrderItemMapper;
import com.biluo.mapper.OrderMapper;
import com.biluo.model.*;
import com.biluo.service.OrderItemService;
import com.biluo.service.OrderService;
import com.biluo.util.AuthUtils;
import com.biluo.vo.OrderStatusCount;
import com.biluo.vo.OrderVo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
	private final OrderItemMapper orderItemMapper;
	private final OrderMemberFeign orderMemberFeign;
	private final OrderProdFeign orderProdFeign;
	private final OrderBasketFeign orderBasketFeign;
	private final Snowflake snowflake;
	private final OrderItemService orderItemService;
	private final RabbitTemplate rabbitTemplate;

	@Override
	public Page<Order> queryOrderPage(Page<Order> page, String orderNumber, Integer status, Date startTime, Date endTime) {
		// 多条件分页查询订单列表
		page = lambdaQuery()
				.eq(ObjectUtil.isNotNull(status), Order::getStatus, status)
				.between(ObjectUtil.isAllNotEmpty(startTime, endTime), Order::getCreateTime, startTime, endTime)
				.eq(StringUtils.hasText(orderNumber), Order::getOrderNumber, orderNumber)
				.orderByDesc(Order::getCreateTime).page(page);
		// 从订单分页对象中获取订单记录
		List<Order> orderList = page.getRecords();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(orderList)) {
			return page;
		}
		// 从订单记录集合中获取订单编号集合
		List<String> orderNumberList = orderList.stream().map(Order::getOrderNumber).collect(Collectors.toList());
		// 根据订单编号查询所有订单商品条目对象集合
		List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
				.in(OrderItem::getOrderNumber, orderNumberList)
		);
		// 循环遍历订单记录集合
		orderList.forEach(order -> {
			// 从订单商品条目对象集合中过滤出与当前订单记录的编号一致的商品条目对象集合
			List<OrderItem> itemList = orderItemList.stream()
					.filter(orderItem -> orderItem.getOrderNumber().equals(order.getOrderNumber()))
					.collect(Collectors.toList());
			order.setOrderItems(itemList);
		});
		return page;
	}

	@Override
	public Order queryOrderDetailByOrderNumber(Long orderNumber) {
		// 根据订单编号查询订单信息
		Order order = lambdaQuery().eq(Order::getOrderNumber, orderNumber).one();
		// 根据订单编号查询订单商品条目对象集合
		List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
				.eq(OrderItem::getOrderNumber, orderNumber));
		order.setOrderItems(orderItemList);
		// 从订单记录中获取订单收货地址标识
		Long addrOrderId = order.getAddrOrderId();
		// 远程调用：根据收货地址标识查询地址详情
		Result<MemberAddr> result = orderMemberFeign.getMemberAddrById(addrOrderId);
		// 判断结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_ADDRESS_FAIL);
		}
		// 获取数据
		MemberAddr memberAddr = result.getData();
		order.setUserAddrOrder(memberAddr);

		// 远程接口调用：根据会员openid查询会员昵称
		Result<String> result1 = orderMemberFeign.getNickNameByOpenId(order.getOpenId());
		if (result1.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_MEMBER_NICKNAME_FAIL);
		}
		// 获取数据
		String nickName = result1.getData();
		order.setNickName(nickName);

		return order;
	}

	@Override
	public OrderStatusCount queryMemberOrderStatusCount() {
		// 获取会员openid
		String openId = AuthUtils.getMemberOpenId();
		// 根据会员openid查询待支付订单数量
		Long unPay = lambdaQuery().eq(Order::getOpenId, openId).eq(Order::getStatus, 1).count();

		// 根据会员openid查询待发货订单数量
		Long payed = lambdaQuery().eq(Order::getOpenId, openId).eq(Order::getStatus, 2).count();

		// 根据会员openid查询待收货订单数量
		Long consignment = lambdaQuery().eq(Order::getOpenId, openId).eq(Order::getStatus, 3).count();
		return OrderStatusCount.builder()
				.unPay(unPay).payed(payed).consignment(consignment)
				.build();
	}

	@Override
	public Page<Order> queryMemberOrderPage(Long current, Long size, Long status) {
		// 获取会员openid
		String openId = AuthUtils.getMemberOpenId();
		// 创建订单分页对象
		Page<Order> page = new Page<>(current, size);
		// 分页查询会员订单列表
		page = lambdaQuery()
				.eq(Order::getOpenId, openId)
				.eq(0 != status, Order::getStatus, status)
				.eq(Order::getDeleteStatus, 0)
				.orderByDesc(Order::getCreateTime)
				.page(page);
		// 从订单分页对象中获取订单记录集合
		List<Order> orderList = page.getRecords();
		// 判断是否有值
		if (ObjectUtil.isEmpty(orderList)) {
			return page;
		}
		// 从订单集合中获取订单编号集合
		List<String> orderNumberList = orderList.stream().map(Order::getOrderNumber).collect(Collectors.toList());
		// 根据订单编号集合查询订单商品条目对象集合
		List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
				.in(OrderItem::getOrderNumber, orderNumberList)
		);
		// 循环遍历订单集合
		orderList.forEach(order -> {
			// 从所有商品条目对象集合中过滤出与当前订单的订单编号一致的商品条目对象集合
			List<OrderItem> itemList = orderItemList.stream()
					.filter(orderItem -> orderItem.getOrderNumber().equals(order.getOrderNumber()))
					.collect(Collectors.toList());
			order.setOrderItemDtos(itemList);
		});
		return page;
	}

	@Override
	public Order queryMemberOrderDetailByOrderNumber(String orderNumber) {
		// 根据订单编号查询订单信息
		Order order = lambdaQuery().eq(Order::getOrderNumber, orderNumber).one();
		// 判断订单是否存在
		if (ObjectUtil.isNull(order)) {
			throw new BusinessException(BusinessEnum.QUERY_ORDER_FAIL);
		}
		// 远程调用：查询订单收货地址对象
		Result<MemberAddr> result = orderMemberFeign.getMemberAddrById(order.getAddrOrderId());
		if (BusinessEnum.OPERATION_FALL.getCode() == result.getCode()) {
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_ADDRESS_FAIL);
		}
		// 获取收货地址信息
		MemberAddr memberAddr = result.getData();
		order.setUserAddrDto(memberAddr);
		// 根据订单编号查询订单商品条目对象集合
		List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
				.eq(OrderItem::getOrderNumber, orderNumber)
		);
		order.setOrderItemDtos(orderItemList);
		return order;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean receiptMemberOrder(String orderNumber) {
		// 创建订单对象
		Order order = new Order();
		order.setUpdateTime(new Date());
		order.setFinallyTime(new Date());
		order.setStatus(5);
		return lambdaUpdate().eq(Order::getOrderNumber, orderNumber).update(order);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeMemberOrderByOrderNumber(String orderNumber) {
		Order order = new Order();
		order.setUpdateTime(new Date());
		order.setDeleteStatus(1);
		return lambdaUpdate().eq(Order::getOrderNumber, orderNumber).update(order);
	}

	@Override
	public OrderVo queryMemberOrderConfirmVo(OrderConfirmParam orderConfirmParam) {
		// 创建订单确认页面对象
		OrderVo orderVo = new OrderVo();
		// 获取会员openId
		String openId = AuthUtils.getMemberOpenId();
		// 远程调用：根据会员openId查询会员默认收货地址
		Result<MemberAddr> result = orderMemberFeign.getMemberDefaultAddrByOpenId(openId);
		// 判断查询结果
		if (result.getCode() == BusinessEnum.PHONE_CODE_ERROR.getCode()) {
			log.error("远程调用：根据会员openId查询会员默认收货地址失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_MEMBER_DEFAULT_ADDR_FAIL);
		}
		// 获取数据
		MemberAddr memberAddr = result.getData();
		orderVo.setMemberAddr(memberAddr);
		// 从订单确认页面参数对象中获取购物车id集合
		List<Long> basketIds = orderConfirmParam.getBasketIds();
		// 判断订单确认页面请求来自哪
		if (ObjectUtil.isEmpty(basketIds)) {
			// 购物车id集合为空 -> 说明：请求来自于商品详情页面
			productToOrderConfirm(orderConfirmParam.getOrderItem(), orderVo);
		} else {
			// 购物车id集合不为空 -> 说明：请求来自于购物车页面
			orderVo.setSource(1);
			cartToOrderConfirm(basketIds, orderVo);
		}
		return orderVo;
	}

	/**
	 * 处理自于购物车页面的请求
	 * <p>
	 * 购物车id -> 购物车记录 -> 购物车商品数量
	 * -> 商品skuId -> 商品sku对象 -> 商品单价
	 * <p>
	 * 购物车商品数量和商品单价 -> 单个商品的总金额 -> 计算所有单个商品总金额的和
	 *
	 * @param basketIds
	 * @param orderVo
	 */
	private void cartToOrderConfirm(List<Long> basketIds, OrderVo orderVo) {
		// 判断购物车id是否有值
		if (ObjectUtil.isEmpty(basketIds)) {
			return;
		}
		// 远程调用：根据购物车id集合查询购物车对象集合
		Result<List<Basket>> result = orderBasketFeign.getBasketListByIds(basketIds);
		// 判断操作结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据购物车id集合查询购物车对象集合失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_BASKET_LIST_FAIL);
		}
		// 获取数据
		List<Basket> basketList = result.getData();
		// 从购物车对象集合中获取商品skuId集合
		List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
		// 远程调用：根据商品skuId集合查询商品sku对象集合
		Result<List<Sku>> skuResult = orderProdFeign.getSkuListBySkuIds(skuIdList);
		// 判断操作结果
		if (skuResult.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据商品skuId集合查询商品sku对象集合失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_SKU_LIST_FAIL);
		}
		// 获取数据
		List<Sku> skuList = skuResult.getData();

		// 创建订单店铺对象集合
		List<ShopOrder> shopOrderList = new ArrayList<>();

		// 将购物车记录集合按照店铺标识进行分组
		Map<Long, List<Basket>> allShopOrderMap = basketList.stream()
				.collect(Collectors.groupingBy(Basket::getShopId));
		// 创建所有单个商品总额集合
		List<BigDecimal> oneSkuTotalAmounts = new ArrayList<>();
		// 创建所有单个商品购买数量集合
		List<Integer> oneSkuCounts = new ArrayList<>();
		// 循环遍历所有订单店铺map集合
		allShopOrderMap.forEach((shopId, baskets) -> {
			// 创建订单店铺对象
			ShopOrder shopOrder = new ShopOrder();
			// 创建订单商品条目对象集合
			List<OrderItem> orderItemList = new ArrayList<>();
			// 循环遍历当前订单店铺对象中的购物车记录集合
			baskets.forEach(basket -> {
				// 商品购买数量
				Integer prodCount = basket.getProdCount();
				oneSkuCounts.add(prodCount);
				// 商品id
				Long prodId = basket.getProdId();
				// 获取商品skuId
				Long skuId = basket.getSkuId();

				// 创建订单商品条目对象
				OrderItem orderItem = new OrderItem();
				orderItem.setProdCount(prodCount);
				orderItem.setShopId(shopId);
				orderItem.setCommSts(0);
				orderItem.setProdId(prodId);
				orderItem.setSkuId(skuId);
				orderItem.setCreateTime(new Date());
				// 从商品sku对象集合中过滤出与当前购物车记录中的skuId一致的商品sku对象
				Sku sku1 = skuList.stream()
						.filter(sku -> sku.getSkuId().equals(skuId))
						.collect(Collectors.toList()).get(0);
				// 将商品sku1对象的属性值copy到订单商品条目对象中去
				BeanUtils.copyProperties(sku1, orderItem);
				// 计算单个商品总金额
				BigDecimal oneSkuTotalAmount = sku1.getPrice().multiply(new BigDecimal(prodCount));
				orderItem.setProductTotalAmount(oneSkuTotalAmount);
				oneSkuTotalAmounts.add(oneSkuTotalAmount);
				orderItemList.add(orderItem);
			});

			shopOrder.setShopId(shopId);
			shopOrder.setShopOrderItems(orderItemList);
			shopOrderList.add(shopOrder);
		});

		// 计算所有商品购买数量总和
		Integer allSkuCount = oneSkuCounts.stream().reduce(Integer::sum).get();
		orderVo.setTotalCount(allSkuCount);
		// 计算所有商品总金额
		BigDecimal allSkuTotalAmount = oneSkuTotalAmounts.stream().reduce(BigDecimal::add).get();
		orderVo.setTotal(allSkuTotalAmount);
		orderVo.setActualTotal(allSkuTotalAmount);
		// 运费
		if (allSkuTotalAmount.compareTo(new BigDecimal(99)) < 0) {
			orderVo.setTransfee(new BigDecimal(6));
			orderVo.setActualTotal(allSkuTotalAmount.add(new BigDecimal(6)));
		}
		orderVo.setShopCartOrders(shopOrderList);
	}

	/**
	 * 处理来自商品详情页面的请求
	 *
	 * @param orderItem
	 * @param orderVo
	 */
	private void productToOrderConfirm(OrderItem orderItem, OrderVo orderVo) {
		Long shopId = orderItem.getShopId();
		// 创建订单店铺对象集合
		List<ShopOrder> shopOrderList = new ArrayList<>();
		// 创建订单店铺对象
		ShopOrder shopOrder = new ShopOrder();
		// 创建订单商品条目对象集合
		List<OrderItem> orderItemList = new ArrayList<>();

		// 获取商品skuId
		Long skuId = orderItem.getSkuId();
		// 远程调用：根据商品skuId查询商品sku对象
		Result<List<Sku>> result = orderProdFeign.getSkuListBySkuIds(Collections.singletonList(skuId));
		// 判断操作结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据商品skuId查询商品sku对象 失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_SKU_FAIL);
		}
		// 获取数据
		List<Sku> skuList = result.getData();
		// 获取商品sku对象
		Sku sku = skuList.get(0);
		// 获取商品购买数量
		Integer prodCount = orderItem.getProdCount();
		// 计算单个商品总金额
		BigDecimal oneSkuTotalAmount = sku.getPrice().multiply(new BigDecimal(prodCount));


		// 给订单商品条目对象属性赋值
		orderItem.setShopId(shopId);
		orderItem.setCreateTime(new Date());
		orderItem.setCommSts(0);
		orderItem.setProductTotalAmount(oneSkuTotalAmount);
		// 将商品sku对象的属性值copy到订单商品条目对象中去
		BeanUtils.copyProperties(sku, orderItem);

		orderItemList.add(orderItem);
		shopOrder.setShopId(shopId);
		shopOrder.setShopOrderItems(orderItemList);
		shopOrderList.add(shopOrder);

		// 补充订单确认页面对象数据
		orderVo.setShopCartOrders(shopOrderList);
		orderVo.setTotal(oneSkuTotalAmount);
		orderVo.setTotalCount(prodCount);
		orderVo.setActualTotal(oneSkuTotalAmount);
		if (oneSkuTotalAmount.compareTo(new BigDecimal(99)) < 0) {
			orderVo.setTransfee(new BigDecimal(6));
			orderVo.setActualTotal(oneSkuTotalAmount.add(new BigDecimal(6)));
		}
	}

	/**
	 * 会员提交订单：<br>
	 * 1.判断订单请求的来源，如果请求来自购物车页面，需要将购买的商品在购物车中的记录删除掉<br>
	 * 2.修改商品prod和sku库存数量 -> 返回：修改商品prod和sku的库存数量值<br>
	 * 3.写订单（1.写订单表order 2.写订单商品条目表order_item）<br>
	 * 4.解决超时未支付问题（通过延迟队列和死信队列来处理超时未支付问题）
	 *
	 * @param orderVo
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String submitOrder(OrderVo orderVo) {
		// 获取会员openId
		String openId = AuthUtils.getMemberOpenId();
		// 获取订单请求来源标识
		Integer source = orderVo.getSource();
		// 判断请求来源
		if (1 == source) {
			// 说明：提交订单的请求来源于购物车页面 -> 删除会员购买商品在购物车中的记录
			clearMemberCheckedBasket(openId, orderVo);
		}
		// 修改商品prod和sku库存数量
		ChangeStock changeStock = changeProdAndSkuStock(orderVo);
		// 生成一个全局唯一的订单编号（使用雪花算法）
		String orderNumber = generateOrderNumber();
		// 写订单（写订单表order和订单商品条目表order_item）
		writeOrder(openId, orderNumber, orderVo);
		// 解决超时未支付问题，写延迟队列
		sendMsMsg(orderNumber, changeStock);

		return orderNumber;
	}

	/**
	 * 提交订单后发送消息，如果订单未支付，需要在订单超时后删除订单并释放库存（使用延迟队列和死信队列解决超时未支付问题）
	 *
	 * @param orderNumber
	 * @param changeStock
	 */
	private void sendMsMsg(String orderNumber, ChangeStock changeStock) {
		// 将数据存放到json对象中，并将json对象转换为json格式字符串
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("orderNumber", orderNumber);
		jsonObject.put("changeStock", changeStock);
		// 发送消息
		rabbitTemplate.convertAndSend(QueueConstants.ORDER_MS_QUEUE, jsonObject.toJSONString());
	}

	/**
	 * 把订单提交后的数据写入到数据库中的order和order_item表中
	 *
	 * @param openId
	 * @param orderNumber
	 * @param orderVo
	 */
	private void writeOrder(String openId, String orderNumber, OrderVo orderVo) {
		// 创建所有订单商品条目对象集合
		List<OrderItem> allOrderItemList = new ArrayList<>();
		// 获取订单店铺对象集合
		List<ShopOrder> shopOrderList = orderVo.getShopCartOrders();
		// 循环遍历订单店铺对象集合
		shopOrderList.forEach(shopOrder -> {
			// 获取店铺对象的订单商品条目对象集合
			List<OrderItem> orderItemList = shopOrder.getShopOrderItems();
			// 循环遍历订单商品条目对象集合
			orderItemList.forEach(orderItem -> {
				orderItem.setOrderNumber(orderNumber);
				orderItem.setCreateTime(new Date());
			});
			// 收集所有订单商品条目对象集合
			allOrderItemList.addAll(orderItemList);
		});
		// 写订单商品条目表
		boolean saved = orderItemService.saveBatch(allOrderItemList);
		if (saved) {
			// 写订单表order
			Order order = new Order();
			order.setOpenId(openId);
			order.setTotalMoney(orderVo.getTotal());
			order.setOrderNumber(orderNumber);
			order.setActualTotal(orderVo.getActualTotal());
			order.setRemarks(orderVo.getRemark());
			order.setStatus(1);
			order.setFreightAmount(orderVo.getTransfee());
			order.setAddrOrderId(orderVo.getMemberAddr().getAddrId());
			order.setProductNums(orderVo.getTotalCount());
			order.setCreateTime(new Date());
			order.setUpdateTime(new Date());
			order.setIsPayed(0);
			order.setDeleteStatus(0);
			order.setRefundSts(0);
			order.setReduceAmount(orderVo.getShopReduce());
			saved = save(order);
			if (!saved) {
				throw new BusinessException(BusinessEnum.ADD_ORDER_FAIL);
			}
		} else {
			throw new BusinessException(BusinessEnum.ADD_ORDER_ITEM_FAIL);
		}
	}

	/**
	 * 会员提交订单时，修改商品prod和sku的库存数量
	 *
	 * @param orderVo
	 * @return
	 */
	private ChangeStock changeProdAndSkuStock(OrderVo orderVo) {
		// 创建商品prod购买数量对象集合
		List<ProdChange> prodChangeList = new ArrayList<>();
		// 创建商品sku购买数量对象集合
		List<SkuChange> skuChangeList = new ArrayList<>();
		// 获取订单店铺对象集合
		List<ShopOrder> shopOrderList = orderVo.getShopCartOrders();
		// 循环遍历订单店铺对象集合
		shopOrderList.forEach(shopOrder -> {
			// 获取店铺的订单商品条目对象集合
			List<OrderItem> orderItemList = shopOrder.getShopOrderItems();
			// 循环订单商品条目对象集合
			orderItemList.forEach(orderItem -> {
				// 获取商品prodId
				Long prodId = orderItem.getProdId();
				// 获取商品skuId
				Long skuId = orderItem.getSkuId();
				// 获取商品购买数量
				Integer prodCount = orderItem.getProdCount() * -1;

				// 判断当前商品prodId是否在prodChangeList集合中出现过
				List<ProdChange> oneProdChange = prodChangeList.stream()
						.filter(prodChange -> prodChange.getProdId().equals(prodId))
						.collect(Collectors.toList());
				if (ObjectUtil.isEmpty(oneProdChange)) {
					// 说明：当前订单商品条目对象的商品prodId没有出现过
					// 创建商品prod购买数量对象
					ProdChange prodChange = new ProdChange(prodId, prodCount);
					// 创建商品sku购买数量对象
					SkuChange skuChange = new SkuChange(skuId, prodCount);

					prodChangeList.add(prodChange);
					skuChangeList.add(skuChange);
				} else {
					// 说明：当前订单商品条目对象的商品prodId在之前出现过
					// 获取之前商品prodChange
					ProdChange beforeProdChange = oneProdChange.get(0);
					// 计算商品prod一共购买的数量
					int finalCount = beforeProdChange.getCount() + prodCount;
					beforeProdChange.setCount(finalCount);
					// 创建商品sku购买数量对象
					SkuChange skuChange = new SkuChange(skuId, prodCount);
					skuChangeList.add(skuChange);
				}
			});
		});

		// 创建商品购买数量对象
		ChangeStock changeStock = new ChangeStock(prodChangeList, skuChangeList);
		// 远程调用：修改商品prod和sku库存数量
		Result<Boolean> result = orderProdFeign.changeProdAndSkuStock(changeStock);
		// 判断操作结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：修改商品prod和sku库存数量失败");
			throw new BusinessException(BusinessEnum.MODIFY_FEIGN_STOCK_FAIL);
		}
		return changeStock;
	}

	/**
	 * 会员提交订单：删除会员购买商品在购物车中的记录
	 *
	 * @param openId
	 * @param orderVo
	 */
	private void clearMemberCheckedBasket(String openId, OrderVo orderVo) {
		// 创建所有商品skuId集合
		List<Long> allSkuIds = new ArrayList<>();
		// 获取订单店铺对象集合
		List<ShopOrder> shopOrderList = orderVo.getShopCartOrders();
		// 循环遍历订单店铺对象集合
		shopOrderList.forEach(shopOrder -> {
			// 获取店铺对象的订单商品条目对象集合
			List<OrderItem> orderItemList = shopOrder.getShopOrderItems();
			// 从订单商品条目对象集合中获取商品skuId集合
			List<Long> skuIdList = orderItemList.stream()
					.map(OrderItem::getSkuId).collect(Collectors.toList());
			allSkuIds.addAll(skuIdList);
		});
		// 准备参数
		Map<String, Object> param = new HashMap<>();
		param.put("openId", openId);
		param.put("skuIdList", allSkuIds);
		// 远程调用：根据会员openId和商品skuId集合删除购物车记录
		Result<Boolean> result = orderBasketFeign.removeBasketByOpenIdAndSkuIds(param);
		// 判断操作结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据会员openId和商品skuId集合删除购物车记录失败");
			throw new BusinessException(BusinessEnum.REMOVE_CART_PROD_FAIL);
		}
	}


	/**
	 * 使用雪花算法生成订单编号
	 *
	 * @return
	 */
	private String generateOrderNumber() {
		return snowflake.nextIdStr();
	}

	/**
	 * 如果订单超时未支付，则回滚订单
	 *
	 * @param order
	 * @param changeStock
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void orderRollBack(Order order, ChangeStock changeStock) {
		// 修改订单信息
		order.setUpdateTime(new Date());
		order.setFinallyTime(new Date());
		order.setStatus(6);
		order.setCloseType(1);
		updateById(order);
		// 回滚商品prod和sku的库存数量（负负得正）
		List<ProdChange> prodChangeList = changeStock.getProdChangeList();
		prodChangeList.forEach(prodChange -> prodChange.setCount(prodChange.getCount() * -1));
		List<SkuChange> skuChangeList = changeStock.getSkuChangeList();
		skuChangeList.forEach(skuChange -> skuChange.setCount(skuChange.getCount() * -1));
		// 回滚商品prod和sku库存数量
		Result result = orderProdFeign.changeProdAndSkuStock(changeStock);
		if (BusinessEnum.OPERATION_FALL.getCode()== result.getCode()) {
			log.error("回滚商品prod和sku库存数量失败");
			throw new BusinessException(BusinessEnum.MODIFY_FEIGN_STOCK_FAIL);
		}
	}
}
