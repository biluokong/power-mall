package com.biluo.feign;

import com.biluo.domain.Member;
import com.biluo.feign.sentinel.ProdMemberFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品业务模块调用会员业务模块：feign接口
 */
@FeignClient(value = "member-service",fallback = ProdMemberFeignSentinel.class)
public interface ProdMemberFeign {

    @GetMapping("p/user/getMemberListByOpenIds")
    Result<List<Member>> getMemberListByOpenIds(@RequestParam List<String> openIds);
}
