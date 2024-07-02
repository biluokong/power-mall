package com.biluo.feign;

import com.biluo.domain.Basket;
import com.biluo.feign.sentinel.OrderBasketFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 订单业务模块调用购物车业务模块：feign接口
 */
@FeignClient(value = "cart-service",fallback = OrderBasketFeignSentinel.class)
public interface OrderBasketFeign {

    @GetMapping("p/shopCart/getBasketListByIds")
    Result<List<Basket>> getBasketListByIds(@RequestParam List<Long> ids);

    @DeleteMapping("p/shopCart/removeBasketByOpenIdAndSkuIds")
    Result<Boolean> removeBasketByOpenIdAndSkuIds(@RequestBody Map<String,Object> param);
}
