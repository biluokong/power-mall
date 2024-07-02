package com.biluo.feign.sentinel;

import com.biluo.domain.MemberAddr;
import com.biluo.feign.OrderMemberFeign;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderMemberFeignSentinel implements OrderMemberFeign {

	@Override
	public Result<MemberAddr> getMemberAddrById(Long addrId) {
		log.error("调用远程服务获取收货地址失败");
		return null;
	}

	@Override
	public Result<String> getNickNameByOpenId(String openId) {
		log.error("调用远程服务获取会员昵称失败");
		return null;
	}

	@Override
	public Result<MemberAddr> getMemberDefaultAddrByOpenId(String openId) {
		log.error("调用远程服务获取默认收货地址失败");
		return null;
	}
}
