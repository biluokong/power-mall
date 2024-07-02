package com.biluo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Member;
import com.biluo.model.Result;
import com.biluo.service.MemberService;
import com.biluo.util.AuthUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信小程序会员业务管理控制层
 */
@Api(tags = "微信小程序会员业务接口管理")
@RequestMapping("p/user")
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;

	/**
	 * 更新会员的头像和昵称
	 *
	 * @param member 会员对象
	 * @return
	 */

	@ApiOperation("更新会员的头像和昵称")
	@PutMapping("setUserInfo")
	public Result<String> modifyMemberInfo(@RequestBody Member member) {
		Boolean modified = memberService.modifyMemberInfoByOpenId(member);
		return modified ? Result.success() : Result.fail(BusinessEnum.MODIFY_WX_USER_INFO_FAIL);
	}

	/**
	 * 查询会员是否绑定手机号码
	 *
	 * @return
	 */
	@ApiOperation("查询会员是否绑定手机号码")
	@GetMapping("isBindPhone")
	public Result<Boolean> loadMemberIsBindPhone() {
		// 获取会员的openid
		String openId = AuthUtils.getMemberOpenId();
		// 根据会员openid查询会员详情
		Member member = memberService.lambdaQuery().eq(Member::getOpenId, openId).one();
		return Result.success(StringUtils.hasText(member.getUserMobile()));
	}

	//////////////////////// feign接口 ////////////////////////
	@GetMapping("getMemberListByOpenIds")
	public Result<List<Member>> getMemberListByOpenIds(@RequestParam List<String> openIds) {
		List<Member> list = memberService.lambdaQuery().in(Member::getOpenId, openIds).list();
		return Result.success(list);
	}

}
