package com.biluo.feign;

import com.biluo.domain.Sku;
import com.biluo.feign.sentinel.OrderProdFeignSentinel;
import com.biluo.model.ChangeStock;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 订单业务模块调用商品业务模块：feign接口
 */
@FeignClient(value = "product-service",fallback = OrderProdFeignSentinel.class)
public interface OrderProdFeign {

    @GetMapping("prod/prod/getSkuListBySkuIds")
    Result<List<Sku>> getSkuListBySkuIds(@RequestParam List<Long> skuIds);

    @PostMapping("prod/prod/changeProdAndSkuStock")
    Result changeProdAndSkuStock(@RequestBody ChangeStock changeStock);
}
