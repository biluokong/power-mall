package com.biluo.constant;

/**
 * 认证授权常量类
 */
public interface AuthConstants {
	/**
	 * 对应token的header中的key
	 */
	String AUTH_HEADER_KEY = "Authorization";

	/**
	 * token的前缀
	 */
	String TOKEN_PREFIX = "bearer ";

	/**
	 * 对应token的redis中的key前缀
	 */
	String TOKEN_REDIS_PREFIX = "mall:token:";

	/**
	 * 登录的url
	 */
	String LOGIN_URL = "/doLogin";

	/**
	 * 登出的url
	 */
	String LOGOUT_URL = "/doLogout";

	/**
	 * 登录类型
	 */
	String LOGIN_TYPE = "loginType";

	/**
	 * 登录类型：商城后台管理系统
	 */
	String LOGIN_TYPE_SYS = "sysUserLogin";

	/**
	 * 登录类型：商城小程序系统
	 */
	String LOGIN_TYPE_MEMBER = "memberLogin";

	/**
	 * TOKEN有效时长（单位：秒，4个小时）
	 */
	long TOKEN_EXPIRE_TIME = 14400L;

	/**
	 * TOKEN的阈值：3600秒（1个小时）
	 */
	long TOKEN_EXPIRE_THRESHOLD_TIME = 60 * 60L;
}
