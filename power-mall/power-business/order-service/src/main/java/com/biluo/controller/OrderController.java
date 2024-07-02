package com.biluo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Order;
import com.biluo.model.OrderConfirmParam;
import com.biluo.model.Result;
import com.biluo.service.OrderService;
import com.biluo.vo.OrderStatusCount;
import com.biluo.vo.OrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 微信小程序订单业务控制层
 */
@Api(tags = "微信小程序订单接口管理")
@RequestMapping("p/myOrder")
@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	/**
	 * 查询会员订单各状态数量
	 *
	 * @return
	 */
	@ApiOperation("查询会员订单各状态数量")
	@GetMapping("orderCount")
	public Result<OrderStatusCount> loadMemberOrderStatusCount() {
		OrderStatusCount orderStatusCount = orderService.queryMemberOrderStatusCount();
		return Result.success(orderStatusCount);
	}

	/**
	 * 分页查询会员订单列表
	 *
	 * @param current 页码
	 * @param size    每页显示条数
	 * @param status  订单状态(0全部，1待支付，2待发货，3待收货)
	 * @return
	 */
	@ApiOperation("分页查询会员订单列表")
	@GetMapping("myOrder")
	public Result<Page<Order>> loadMemberOrderPage(Long current, Long size, Long status) {
		Page<Order> page = orderService.queryMemberOrderPage(current, size, status);
		return Result.success(page);
	}

	/**
	 * 根据订单编号查询订单详情
	 *
	 * @param orderNumber 订单编号
	 * @return
	 */
	@ApiOperation("根据订单编号查询订单详情")
	@GetMapping("orderDetail")
	public Result<Order> loadMemberOrderDetail(@RequestParam String orderNumber) {
		Order order = orderService.queryMemberOrderDetailByOrderNumber(orderNumber);
		return Result.success(order);
	}

	/**
	 * 会员确认收货
	 *
	 * @param orderNumber 订单编号
	 * @return
	 */
	@ApiOperation("会员确认收货")
	@PutMapping("receipt/{orderNumber}")
	public Result<String> receiptMemberOrder(@PathVariable String orderNumber) {
		Boolean receipted = orderService.receiptMemberOrder(orderNumber);
		return receipted ? Result.success() : Result.fail(BusinessEnum.MODIFY_ORDER_STATUS_FAIL);
	}

	/**
	 * 删除会员订单
	 *
	 * @param orderNumber
	 * @return
	 */
	@ApiOperation("删除会员订单")
	@DeleteMapping("{orderNumber}")
	public Result<String> removeMemberOrder(@PathVariable String orderNumber) {
		Boolean removed = orderService.removeMemberOrderByOrderNumber(orderNumber);
		return removed ? Result.success() : Result.fail(BusinessEnum.REMOVE_ORDER_FAIL);
	}


	/**
	 * 查询会员确认订单页面数据
	 *
	 * @param orderConfirmParam 订单确认页面参数对象（basketIds,orderItem）
	 * @return
	 */
	@ApiOperation("查询会员确认订单页面数据")
	@PostMapping("confirm")
	public Result<OrderVo> loadMemberOrderConfirmVo(@RequestBody OrderConfirmParam orderConfirmParam) {
		OrderVo orderVo = orderService.queryMemberOrderConfirmVo(orderConfirmParam);
		return Result.success(orderVo);
	}

	@ApiOperation("会员提交订单")
	@PostMapping("submit")
	public Result<String> submitOrder(@RequestBody OrderVo orderVo) {
		String orderNumber = orderService.submitOrder(orderVo);
		return Result.success(orderNumber);
	}
}
