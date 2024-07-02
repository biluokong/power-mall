package com.biluo.feign;

import com.biluo.domain.Prod;
import com.biluo.feign.sentinel.StoreProdFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 门店业务模块调用商品业务模块的feign接口
 */
@FeignClient(value = "product-service", fallback = StoreProdFeignSentinel.class)
public interface StoreProdFeign {
	@GetMapping("prod/prod/getProdListByIds")
	Result<List<Prod>> getProdListByIds(@RequestParam List<Long> prodIdList);
}
