package com.biluo.feign.sentinel;

import com.biluo.domain.Basket;
import com.biluo.feign.OrderBasketFeign;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Component
@Slf4j
public class OrderBasketFeignSentinel implements OrderBasketFeign {
    @Override
    public Result<List<Basket>> getBasketListByIds(List<Long> ids) {
        log.error("远程接口调用：根据购物车id集合查询购物车对象集合");
        return null;
    }

    @Override
    public Result<Boolean> removeBasketByOpenIdAndSkuIds(Map<String, Object> param) {
        log.error("远程接口调用失败：根据会员openId和商品skuId集合删除购物车记录");
        return null;
    }
}
