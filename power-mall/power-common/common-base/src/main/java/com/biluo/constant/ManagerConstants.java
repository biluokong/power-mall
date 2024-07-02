package com.biluo.constant;

public interface ManagerConstants {
	/**
	 * 商城后台系统的缓存key前缀
	 */
	String SYS_KEY_PREFIX = "mall:sys";

	/**
	 * 商城后台系统的菜单和功能权限缓存key前缀
	 */
	String SYS_MENU_AND_AUTH_KEY_PREFIX = "mall:sys:perms";

	/**
	 * 商城后台系统的所有权限数据的缓存key
	 */
	String SYS_ALL_MENU_KEY = "'menus'";

	/**
	 * 商城后台系统的所有角色数据的缓存key
	 */
	String SYS_ALL_ROLE_KEY = "'roles'";
}
