package com.biluo.feign;

import com.biluo.domain.Prod;
import com.biluo.feign.sentinel.MemberProdFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 会员业务模块调用商品业务模块feign接口
 */
@FeignClient(value = "product-service",fallback = MemberProdFeignSentinel.class)
public interface MemberProdFeign {

    @GetMapping("prod/prod/getProdListByIds")
    public Result<List<Prod>> getProdListByIds(@RequestParam List<Long> prodIdList);
}
