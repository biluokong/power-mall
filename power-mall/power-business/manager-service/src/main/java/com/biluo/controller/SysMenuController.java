package com.biluo.controller;

import com.biluo.domain.SysMenu;
import com.biluo.model.Result;
import com.biluo.service.SysMenuService;
import com.biluo.util.AuthUtils;
import com.biluo.vo.MenuAndAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 系统权限控制接口
 */
@Api("系统权限接口管理")
@RestController
@RequestMapping("sys/menu")
@RequiredArgsConstructor
public class SysMenuController {
	private final SysMenuService sysMenuService;

	@ApiOperation("查询用户的菜单权限和操作权限")
	@GetMapping("nav")
	public Result<MenuAndAuth> loadUserMenuAndAuth() {
		// 获取用户标识
		Long userId = AuthUtils.getLoginUserId();
		// 获取菜单权限
		Set<SysMenu> menus = sysMenuService.queryMenusByUserId(userId);
		// 获取操作权限
		Set<String> perms = AuthUtils.getLoginUserPerms();
		return Result.success(new MenuAndAuth(menus, perms));
	}

	@ApiOperation("查询系统所有权限集合")
	@GetMapping("table")
	@PreAuthorize("hasAuthority('sys:menu:list')")
	public Result<List<SysMenu>> loadAllSysMenuList() {
		List<SysMenu> sysMenus = sysMenuService.queryAllSysMenuList();
		return Result.success(sysMenus);
	}

	@ApiOperation("新增权限")
	@PostMapping
	@PreAuthorize("hasAuthority('sys:menu:save')")
	public Result saveSysMenu(@RequestBody SysMenu sysMenu) {
		sysMenuService.saveSysMenu(sysMenu);
		return Result.success();
	}

	@ApiOperation("根据标识查询菜单权限信息")
	@GetMapping("info/{menuId}")
	@PreAuthorize("hasAuthority('sys:menu:info')")
	public Result<SysMenu> loadSysMenuInfo(@PathVariable Long menuId) {
		SysMenu sysMenu = sysMenuService.getById(menuId);
		return Result.success(sysMenu);
	}

	@ApiOperation("修改菜单权限信息")
	@PutMapping
	@PreAuthorize("hasAuthority('sys:menu:update')")
	public Result modifySysMenu(@RequestBody SysMenu sysMenu) {
		sysMenuService.modifySysMenu(sysMenu);
		return Result.success();
	}

	@ApiOperation("删除菜单权限")
	@DeleteMapping("{menuId}")
	@PreAuthorize("hasAuthority('sys:menu:delete')")
	public Result<String> removeSysMenu(@PathVariable Long menuId) {
		sysMenuService.removeSysMenuById(menuId);
		return Result.success();
	}
}
