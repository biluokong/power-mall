package com.biluo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.SysRole;
import com.biluo.model.Result;
import com.biluo.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("系统角色接口管理")
@RestController
@RequestMapping("sys/role")
@RequiredArgsConstructor
public class SysRoleController {
	private final SysRoleService sysRoleService;

	@ApiOperation("查询所有角色")
	@GetMapping("list")
	@PreAuthorize("hasAuthority('sys:role:list')")
	public Result<List<SysRole>> loadSysRoleList() {
		List<SysRole> sysRoles = sysRoleService.querySysRoleList();
		return Result.success(sysRoles);
	}

	@ApiOperation("多条件分页查询角色列表")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('sys:role:page')")
	public Result<Page<SysRole>> loadSysRolePage(Long current, Long size,
												 @RequestParam(required = false) String roleName) {
		Page<SysRole> page = new Page<>(current, size);
		page = sysRoleService.page(page, new LambdaQueryWrapper<SysRole>()
				.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
				.orderByDesc(SysRole::getCreateTime));
		return Result.success(page);
	}

	@ApiOperation("新增角色")
	@PostMapping
	@PreAuthorize("hasAuthority('sys:role:save')")
	public Result saveSysRole(@RequestBody SysRole sysRole) {
		sysRoleService.saveSysRole(sysRole);
		return Result.success();
	}

	@ApiOperation("根据标识查询角色信息")
	@GetMapping("info/{roleId}")
	@PreAuthorize("hasAuthority('sys:role:info')")
	public Result<SysRole> loadSysRoleInfo(@PathVariable Long roleId) {
		SysRole sysRole = sysRoleService.querySysRoleInfoByRoleId(roleId);
		return Result.success(sysRole);
	}

	@ApiOperation("修改角色信息")
	@PutMapping
	@PreAuthorize("hasAuthority('sys:role:update')")
	public Result modifySysRole(@RequestBody SysRole sysRole) {
		sysRoleService.modifySysRole(sysRole);
		return Result.success();
	}

	@ApiOperation("批量/单个删除角色")
	@DeleteMapping
	@PreAuthorize("hasAuthority('sys:role:delete')")
	public Result<String> removeSysRole(@RequestBody List<Long> roleIdList) {
		sysRoleService.removeSysRoleListByIds(roleIdList);
		return Result.success();
	}
}
