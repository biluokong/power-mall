package com.biluo.feign;

import com.biluo.domain.Sku;
import com.biluo.feign.sentinel.BasketProdFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 购物车业务模块调用商品业务模块：feign接口
 */
@FeignClient(value = "product-service",fallback = BasketProdFeignSentinel.class)
public interface BasketProdFeign {

    @GetMapping("prod/prod/getSkuListBySkuIds")
    Result<List<Sku>> getSkuListBySkuIds(@RequestParam List<Long> skuIds);
}
