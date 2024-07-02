package com.biluo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.SysUser;
import com.biluo.model.Result;
import com.biluo.service.SysUserService;
import com.biluo.util.AuthUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("系统用户接口管理")
@RestController
@RequestMapping("sys/user")
@RequiredArgsConstructor
public class SysUserController {
	private final SysUserService sysUserService;

	@ApiOperation("获取个人信息")
	@GetMapping("info")
	public Result<SysUser> loadSysUserInfo() {
		SysUser sysUser = sysUserService.getById(AuthUtils.getLoginUserId());
		return Result.success(sysUser);
	}

	@ApiOperation("分页查询系统管理员")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('sys:user:page')")
	public Result<Page<SysUser>> loadSysUserPage(Long current, Long size, @RequestParam(required = false) String username) {
		Page<SysUser> page = new Page<>(current, size);
		page = sysUserService.page(page, new LambdaQueryWrapper<SysUser>()
				.like(StringUtils.hasText(username), SysUser::getUsername, username)
				.orderByDesc(SysUser::getCreateTime));
		return Result.success(page);
	}

	@ApiOperation("新增系统管理员")
	@PostMapping
	@PreAuthorize("hasAuthority('sys:user:save')")
	public Result<Boolean> saveSysUser(@RequestBody SysUser sysUser) {
		sysUserService.saveSysUser(sysUser);
		return Result.success();
	}

	@ApiOperation("根据标识查询系统管理员信息")
	@GetMapping("info/{id}")
	@PreAuthorize("hasAuthority('sys:user:info')")
	public Result<SysUser> loadSysUserInfo(@PathVariable Long id) {
		SysUser sysUser = sysUserService.querySysUserInfoByUserId(id);
		return Result.success(sysUser);
	}

	@ApiOperation("修改管理员信息")
	@PutMapping
	@PreAuthorize("hasAuthority('sys:user:update')")
	public Result modifySysUserInfo(@RequestBody SysUser sysUser) {
		sysUserService.modifySysUserInfo(sysUser);
		return Result.success();
	}

	@ApiOperation("批量/单个删除管理员")
	@DeleteMapping("{userIds}")
	@PreAuthorize("hasAuthority('sys:user:delete')")
	public Result<Boolean> removeSysUsers(@PathVariable List<Long> userIds) {
		sysUserService.removeSysUserListByUserIds(userIds);
		return Result.success();
	}
}
