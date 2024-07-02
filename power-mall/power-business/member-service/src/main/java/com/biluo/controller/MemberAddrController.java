package com.biluo.controller;

import com.biluo.constant.BusinessEnum;
import com.biluo.domain.MemberAddr;
import com.biluo.model.Result;
import com.biluo.service.MemberAddrService;
import com.biluo.util.AuthUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员收货地址管理控制层
 */
@Api(tags = "会员收货地址接口管理")
@RequestMapping("p/address")
@RestController
@RequiredArgsConstructor
public class MemberAddrController {
	private final MemberAddrService memberAddrService;

	/**
	 * 查询会员所有收货地址
	 *
	 * @return
	 */
	@ApiOperation("查询会员所有收货地址")
	@GetMapping("list")
	public Result<List<MemberAddr>> loadMemberAddrList() {
		String openId = AuthUtils.getMemberOpenId();
		List<MemberAddr> memberAddrs = memberAddrService.queryMemberAddrListByOpenId(openId);
		return Result.success(memberAddrs);
	}

	/**
	 * 新增会员收货地址
	 *
	 * @param memberAddr 会员收货地址对象
	 * @return
	 */
	@ApiOperation("新增会员收货地址")
	@PostMapping
	public Result saveMemberAddr(@RequestBody MemberAddr memberAddr) {
		String openId = AuthUtils.getMemberOpenId();
		Boolean saved = memberAddrService.saveMemberAddr(memberAddr, openId);
		return saved ? Result.success() : Result.fail(BusinessEnum.ADD_MEMBER_ADDR_FAIL);
	}

	/**
	 * 查询会员收货地址详情
	 *
	 * @param addrId 会员地址id
	 * @return
	 */
	@ApiOperation("查询会员收货地址详情")
	@GetMapping("addrInfo/{addrId}")
	public Result<MemberAddr> loadMemberAddrInfo(@PathVariable Long addrId) {
		MemberAddr memberAddr = memberAddrService.getById(addrId);
		return Result.success(memberAddr);
	}

	/**
	 * 修改会员收货地址信息
	 *
	 * @param memberAddr 收货地址对象
	 * @return
	 */
	@ApiOperation("修改会员收货地址信息")
	@PutMapping
	public Result modifyMemberAddrInfo(@RequestBody MemberAddr memberAddr) {
		String openId = AuthUtils.getMemberOpenId();
		Boolean modified = memberAddrService.modifyMemberAddrInfo(memberAddr, openId);
		return modified ? Result.success() : Result.fail(BusinessEnum.MODIFY_MEMBER_ADDR_FAIL);
	}

	/**
	 * 删除会员收货地址
	 *
	 * @param addrId 收货地址id
	 * @return
	 */
	@ApiOperation("删除会员收货地址")
	@DeleteMapping("deleteAddr/{addrId}")
	public Result removeMemberAddr(@PathVariable Long addrId) {
		String openId = AuthUtils.getMemberOpenId();
		Boolean removed = memberAddrService.removeMemberAddrById(addrId, openId);
		return removed ? Result.success() : Result.fail(BusinessEnum.REMOVE_MEMBER_ADDR_FAIL);
	}

	/**
	 * 会员设置默认收货地址
	 *
	 * @param newAddrId 新默认收货地址对象
	 * @return
	 */
	@ApiOperation("会员设置默认收货地址")
	@PutMapping("defaultAddr/{newAddrId}")
	public Result modifyMemberDefaultAddr(@PathVariable Long newAddrId) {
		String openId = AuthUtils.getMemberOpenId();
		Boolean modified = memberAddrService.modifyMemberDefaultAddr(openId, newAddrId);
		return modified ? Result.success() : Result.fail(BusinessEnum.MODIFY_MEMBER_DEFAULT_ADDR_FAIL);
	}


	////////////////////// feign 接口 /////////////////////////////
	@GetMapping("getMemberAddrById")
	public Result<MemberAddr> getMemberAddrById(@RequestParam Long addrId) {
		MemberAddr memberAddr = memberAddrService.getById(addrId);
		return Result.success(memberAddr);
	}

	@GetMapping("getMemberDefaultAddrByOpenId")
	public Result<MemberAddr> getMemberDefaultAddrByOpenId(@RequestParam String openId) {
		MemberAddr memberAddr = memberAddrService.lambdaQuery()
				.eq(MemberAddr::getOpenId, openId)
				.eq(MemberAddr::getCommonAddr, 1).one();
		return Result.success(memberAddr);
	}
}
