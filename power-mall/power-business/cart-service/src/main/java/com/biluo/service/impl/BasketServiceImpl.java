package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Basket;
import com.biluo.domain.Sku;
import com.biluo.ex.handler.BusinessException;
import com.biluo.feign.BasketProdFeign;
import com.biluo.mapper.BasketMapper;
import com.biluo.model.CartItem;
import com.biluo.model.Result;
import com.biluo.model.ShopCart;
import com.biluo.service.BasketService;
import com.biluo.util.AuthUtils;
import com.biluo.vo.CartTotalAmount;
import com.biluo.vo.CartVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService {
	private final BasketProdFeign basketProdFeign;


	/**
	 * 会员购物车中商品的数量计算方法：所有商品数量的和
	 *
	 * @param openId
	 * @return
	 */
	@Override
	public Integer queryMemberBasketProdCount(String openId) {
		List<Object> objs = baseMapper.selectObjs(new QueryWrapper<Basket>()
				.select("ifnull(sum(prod_count),0)")
				.eq("open_id", openId)
		);
		Object o = objs.get(0);
		return Integer.valueOf(o.toString());
	}

	@Override
	public CartVo queryMemberCartVo() {
		// 创建购物车对象
		CartVo cartVo = new CartVo();
		// 从容器中获取会员openId
		String openId = AuthUtils.getMemberOpenId();
		// 查询会员购物车记录
		List<Basket> basketList = lambdaQuery().eq(Basket::getOpenId, openId).list();
		// 判断购物车记录是否有值
		if (ObjectUtil.isEmpty(basketList)) {
			// 说明：当前会员购物车记录没有数据
			return cartVo;
		}
		// 从购物车记录集合中获取商品skuId集合
		List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
		// 远程调用：根据商品skuId集合查询商品sku对象集合
		Result<List<Sku>> result = basketProdFeign.getSkuListBySkuIds(skuIdList);
		// 判断操作结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据商品skuId集合查询商品sku对象集合失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_SKU_LIST_FAIL);
		}
		// 获取商品sku对象集合数据
		List<Sku> skuList = result.getData();
		// 创建购物车店铺对象集合
		List<ShopCart> shopCartList = new ArrayList<>();
		// 循环遍历，会员购物车记录集合
		basketList.forEach(basket -> {
			// 当前购物车记录所属的店铺标识
			Long shopId = basket.getShopId();
			// 获取购物车id
			Long basketId = basket.getBasketId();
			// 获取商品id
			Long prodId = basket.getProdId();
			// 获取商品skuId
			Long skuId = basket.getSkuId();
			// 获取购物车商品数量
			Integer prodCount = basket.getProdCount();

			// 判断当前购物车记录所属的店铺是否在购物车店铺对象集合中存在
			List<ShopCart> oneShopCart = shopCartList.stream()
					.filter(shopCart -> shopCart.getShopId().equals(shopId)).collect(Collectors.toList());
			// 判断是否有值
			if (ObjectUtil.isEmpty(oneShopCart)) {
				// 集合为空：说明当前购物车记录所属的店铺对象在购物车店铺对象集合中不存在 -> 创建购物车店铺对象
				// 创建购物车店铺对象
				ShopCart shopCart = new ShopCart();
				// 创建购物车商品条目对象集合
				List<CartItem> cartItemList = new ArrayList<>();
				// 创建购物车商品条目对象
				CartItem cartItem = new CartItem();

				// 给商品条目对象的属性赋值
				cartItem.setBasketId(basketId);
				cartItem.setProdId(prodId);
				cartItem.setSkuId(skuId);
				cartItem.setProdCount(prodCount);
				// 从商品sku对象集合中获取当前购物车记录所对应的商品sku对象
				Sku sku1 = skuList.stream()
						.filter(sku -> sku.getSkuId().equals(skuId))
						.collect(Collectors.toList()).get(0);
				// 将商品sku1对象的属性值copy到购物车商品条目对象属性中去
				BeanUtils.copyProperties(sku1, cartItem);

				cartItemList.add(cartItem);
				shopCart.setShopId(shopId);
				shopCart.setShopCartItems(cartItemList);
				shopCartList.add(shopCart);

			} else {
				// 集合不为空：说明当前购物车记录所属的店铺对象在购物车店铺对象集合中存在 ->
				// 不需要再创建购物车店铺对象,应该从购物车店铺对象集合中获取之前创建好的购物车店铺对象
				// 获取当前购物车记录属性的购物车店铺对象
				ShopCart shopCart = oneShopCart.get(0);
				// 获取购物车店铺对象之前的购物车商品条目对象集合
				List<CartItem> cartItemList = shopCart.getShopCartItems();
				// 将现在新的购物车商品条目对象添加到之前的购物车商品条目对象集合中去
				// 购物车商品条目对象
				CartItem cartItem = new CartItem();
				cartItem.setBasketId(basketId);
				cartItem.setProdId(prodId);
				cartItem.setSkuId(skuId);
				cartItem.setProdCount(prodCount);
				// 从商品sku对象集合中过滤出与当前购物车记录的商品skuId一致的商品sku对象
				Sku sku1 = skuList.stream()
						.filter(sku -> sku.getSkuId().equals(skuId))
						.collect(Collectors.toList()).get(0);
				// 将商品sku1对象的属性值copy到购物车商品条目对象中去
				BeanUtils.copyProperties(sku1, cartItem);
				cartItemList.add(cartItem);
				shopCart.setShopCartItems(cartItemList);
			}
		});
		cartVo.setShopCarts(shopCartList);
		return cartVo;
	}

	@Override
	public CartTotalAmount calculateMemberCheckedBasketTotalAmount(List<Long> basketIds) {
		// 创建购物车总金额对象
		CartTotalAmount cartTotalAmount = new CartTotalAmount();
		// 判断会员是否有选中的购物车记录
		if (ObjectUtil.isEmpty(basketIds)) {
			// 购物车id集合为空 -> 说明会员没有选中购物车记录 -> 购物车商品总金额为0
			return cartTotalAmount;
		}
		// 购物车id集合不为空 -> 说明会员有选中的购物车记录 -> 计算金额
		// 根据购物车id集合查询购物车对象集合
		List<Basket> basketList = listByIds(basketIds);
		// 从购物车对象集合中获取商品skuId集合
		List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
		// 远程调用：根据商品skuId集合查询商品sku对象集合
		Result<List<Sku>> result = basketProdFeign.getSkuListBySkuIds(skuIdList);
		// 判断查询结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据商品skuId集合查询商品sku对象集合失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_SKU_LIST_FAIL);
		}
		// 获取返回数据
		List<Sku> skuList = result.getData();
		// 创建所有单个商品总金额的集合
		List<BigDecimal> oneSkuTotalAmounts = new ArrayList<>();
		// 循环遍历购物车对象集合
		basketList.forEach(basket -> {
			// 获取购物车记录的商品skuId
			Long skuId = basket.getSkuId();
			// 获取购物车记录中商品购买的数量
			Integer prodCount = basket.getProdCount();
			// 从商品sku对象集合中获取与当前购物车记录的skuId一致的购物车记录对象
			Sku sku1 = skuList.stream()
					.filter(sku -> sku.getSkuId().equals(skuId))
					.collect(Collectors.toList()).get(0);
			// 获取商品单价
			BigDecimal price = sku1.getPrice();
			// 计算单个商品总金额
			BigDecimal oneSkuTotalAmount = price.multiply(new BigDecimal(prodCount));
			// 添加到单个商品总金额集合中
			oneSkuTotalAmounts.add(oneSkuTotalAmount);
		});
		// 计算所有单个商品总金额的和
		BigDecimal allSkuTotalAmount = oneSkuTotalAmounts.stream().reduce(BigDecimal::add).get();

		// 填充数据
		cartTotalAmount.setTotalMoney(allSkuTotalAmount);
		cartTotalAmount.setFinalMoney(allSkuTotalAmount);
		// 运费：商品总金额超过99元，免运费，如果小于99元，运费6元
		if (allSkuTotalAmount.compareTo(new BigDecimal(99)) < 0) {
			cartTotalAmount.setTransMoney(new BigDecimal(6));
			cartTotalAmount.setFinalMoney(allSkuTotalAmount.add(new BigDecimal(6)));
		}
		return cartTotalAmount;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean changeCartItem(Basket basket) {
		// 获取会员openid
		String openId = AuthUtils.getMemberOpenId();
		// 根据会员openId和商品skuId查询购物车记录
		Basket beforeBasket = lambdaQuery()
				.eq(Basket::getOpenId, openId)
				.eq(Basket::getSkuId, basket.getSkuId())
				.one();
		// 判断购物车记录是否有值
		if (ObjectUtil.isNotNull(beforeBasket)) {
			// 购物车记录不为空 -> 当前会员添加到购物车中的商品是存在的 -> 修改存在购物车中商品的数量
			// 计算商品最终数量
			int finalCount = beforeBasket.getProdCount() + basket.getProdCount();
			beforeBasket.setProdCount(finalCount);
			return updateById(beforeBasket);
		}

		// 购物车记录为空 -> 当前会员添加到购物车中的商品是不存在的 -> 添加商品到购物车记录
		basket.setCreateTime(new Date());
		basket.setOpenId(openId);
		return save(basket);
	}
}
