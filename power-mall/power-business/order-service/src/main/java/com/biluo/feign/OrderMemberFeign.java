package com.biluo.feign;

import com.biluo.domain.MemberAddr;
import com.biluo.feign.sentinel.OrderMemberFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程调用会员服务的Feign接口
 */
@FeignClient(value = "member-service", fallback = OrderMemberFeignSentinel.class)
public interface OrderMemberFeign {
	@GetMapping("p/address/getMemberAddrById")
	Result<MemberAddr> getMemberAddrById(@RequestParam Long addrId);

	@GetMapping("admin/user/getNickNameByOpenId")
	Result<String> getNickNameByOpenId(@RequestParam String openId);

	@GetMapping("p/address/getMemberDefaultAddrByOpenId")
	Result<MemberAddr> getMemberDefaultAddrByOpenId(@RequestParam String openId);
}
