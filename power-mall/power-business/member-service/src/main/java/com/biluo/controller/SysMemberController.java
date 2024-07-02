package com.biluo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Member;
import com.biluo.model.Result;
import com.biluo.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台管理系统维护会员控制层
 */
@Api(tags = "后台管理系统会员接口管理")
@RequestMapping("admin/user")
@RestController
@RequiredArgsConstructor
public class SysMemberController {
	private final MemberService memberService;

	/**
	 * 多条件分页查询会员
	 *
	 * @param current  页码
	 * @param size     每页显示条件
	 * @param nickName 会员昵称
	 * @param status   会员状态
	 * @return
	 */

	@ApiOperation("多条件分页查询会员")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('admin:user:page')")
	public Result<Page<Member>> loadMemberPage(Long current, Long size,
											   @RequestParam(required = false) String nickName,
											   @RequestParam(required = false) Integer status) {
		// 创建会员分页对象
		Page<Member> page = new Page<>(current, size);
		// 多条件分页查询会员
		page = memberService.page(page, new LambdaQueryWrapper<Member>()
				.eq(ObjectUtil.isNotNull(status), Member::getStatus, status)
				.like(StringUtils.hasText(nickName), Member::getNickName, nickName)
				.orderByDesc(Member::getCreateTime)
		);
		return Result.success(page);
	}

	/**
	 * 根据标识查询会员信息
	 *
	 * @param id 会员id
	 * @return
	 */
	@ApiOperation("根据标识查询会员信息")
	@GetMapping("info/{id}")
	@PreAuthorize("hasAuthority('admin:user:info')")
	public Result<Member> loadMemberInfo(@PathVariable Long id) {
		Member member = memberService.lambdaQuery()
				.select(Member::getId, Member::getOpenId, Member::getPic, Member::getNickName, Member::getStatus)
				.eq(Member::getId, id).one();
		return Result.success(member);
	}

	/**
	 * 修改会员状态
	 *
	 * @param member 会员对象（id,status）
	 * @return
	 */
	@ApiOperation("修改会员状态")
	@PutMapping
	@PreAuthorize("hasAuthority('admin:user:update')")
	public Result modifyMemberStatus(@RequestBody Member member) {
		member.setUpdateTime(new Date());
		boolean updated = memberService.updateById(member);
		return updated ? Result.success() : Result.fail(BusinessEnum.MODIFY_MEMBER_STATUS_FAIL);
	}

	/**
	 * 批量删除会员
	 *
	 * @param ids 会员id集合
	 * @return
	 */
	@ApiOperation("批量删除会员")
	@DeleteMapping
	@PreAuthorize("hasAuthority('admin:user:delete')")
	public Result removeMembers(@RequestBody List<Integer> ids) {
		// 创建会员对象集合
		List<Member> memberList = new ArrayList<>();
		// 循环遍历会员id集合
		ids.forEach(id -> {
			Member member = new Member();
			member.setId(id);
			member.setStatus(-1);
			memberList.add(member);
		});
		boolean removed = memberService.updateBatchById(memberList);
		return removed ? Result.success() : Result.fail(BusinessEnum.REMOVE_MEMBER_FAIL);
	}

////////////////////////// feign 接口 ////////////////////////
    @GetMapping("getNickNameByOpenId")
    public Result<String> getNickNameByOpenId(@RequestParam String openId) {
        Member member = memberService.lambdaQuery()
                .select(Member::getNickName)
                .eq(Member::getOpenId, openId)
				.one();
        return Result.success(member.getNickName());
    }
}
