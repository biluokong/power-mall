package com.biluo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Basket;
import com.biluo.model.Result;
import com.biluo.service.BasketService;
import com.biluo.util.AuthUtils;
import com.biluo.vo.CartTotalAmount;
import com.biluo.vo.CartVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车业务控制层
 */
@Api(tags = "购物车业务接口管理")
@RequestMapping("p/shopCart")
@RestController
@RequiredArgsConstructor
public class BasketController {
	private final BasketService basketService;


	/**
	 * 查询会员购物车中商品数量
	 *
	 * @return
	 */
	@ApiOperation("查询会员购物车中商品数量")
	@GetMapping("prodCount")
	public Result<Integer> loadMemberBasketProdCount() {
		String openId = AuthUtils.getMemberOpenId();
		Integer count = basketService.queryMemberBasketProdCount(openId);
		return Result.success(count);
	}

	/**
	 * 查询会员购物车页面数据
	 *
	 * @return
	 */
	@ApiOperation("查询会员购物车页面数据")
	@GetMapping("info")
	public Result<CartVo> loadMemberCartVo() {
		CartVo cartVo = basketService.queryMemberCartVo();
		return Result.success(cartVo);
	}

	/**
	 * 计算会员选中购物车中商品的金额
	 *
	 * @param basketIds 选中购物车记录id集合
	 * @return
	 */
	@ApiOperation("计算会员选中购物车中商品的金额")
	@PostMapping("totalPay")
	public Result<CartTotalAmount> calculateMemberCheckedBasketTotalAmount(@RequestBody List<Long> basketIds) {
		CartTotalAmount cartTotalAmount = basketService.calculateMemberCheckedBasketTotalAmount(basketIds);
		return Result.success(cartTotalAmount);
	}

	/**
	 * 添加商品到购物车或修改商品在购物车中的数量
	 *
	 * @param basket 购物车对象(shopId,prodId,skuId,prodCount)
	 * @return
	 */
	@ApiOperation("添加商品到购物车或修改商品在购物车中的数量")
	@PostMapping("changeItem")
	public Result changeCartItem(@RequestBody Basket basket) {
		Boolean changed = basketService.changeCartItem(basket);
		return changed ? Result.success() : Result.fail(BusinessEnum.MODIFY_CART_NUM_FAIL);
	}

	/**
	 * 删除会员选中的购物车记录
	 *
	 * @param basketIds 选中购物车记录id集合
	 * @return
	 */
	@ApiOperation("删除会员选中的购物车记录")
	@DeleteMapping("deleteItem")
	public Result removeMemberCheckedBasket(@RequestBody List<Long> basketIds) {
		boolean removed = basketService.removeBatchByIds(basketIds);
		return removed ? Result.success() : Result.fail(BusinessEnum.REMOVE_CART_PROD_FAIL);
	}

	///////////////////////////// feign 接口 ///////////////////////////////////
	@GetMapping("getBasketListByIds")
	public Result<List<Basket>> getBasketListByIds(@RequestParam List<Long> ids) {
		List<Basket> basketList = basketService.listByIds(ids);
		return Result.success(basketList);
	}

	@DeleteMapping("removeBasketByOpenIdAndSkuIds")
	public Result<Boolean> removeBasketByOpenIdAndSkuIds(@RequestBody Map<String, Object> param) {
		// 获取会员openId
		String openId = (String) param.get("openId");
		// 获取商品skuId集合
		List<Long> skuIdList = (List<Long>) param.get("skuIdList");
		// 根据会员openId和商品skuId集合删除购物车记录
		boolean removed = basketService.lambdaUpdate()
				.eq(Basket::getOpenId, openId)
				.in(Basket::getSkuId, skuIdList)
				.remove();
		return Result.success(removed);
	}
}
