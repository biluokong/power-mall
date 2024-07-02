package com.biluo.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.biluo.constant.AuthConstants;
import com.biluo.domain.LoginSysUser;
import com.biluo.mapper.LoginSysUserMapper;
import com.biluo.model.SecurityUser;
import com.biluo.strategy.LoginStrategy;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 商城后台系统登录策略
 */
@Service(AuthConstants.LOGIN_TYPE_SYS)
@AllArgsConstructor
public class SysUserLoginStrategy implements LoginStrategy {
	private final LoginSysUserMapper loginSysUserMapper;

	@Override
	public UserDetails doLogin(String username) {
		LambdaQueryWrapper<LoginSysUser> eq = Wrappers.<LoginSysUser>lambdaQuery()
				.eq(LoginSysUser::getUsername, username);
		LoginSysUser loginSysUser = loginSysUserMapper.selectOne(eq);
		if (ObjectUtils.isNotEmpty(loginSysUser)) {
			Set<String> perms = loginSysUserMapper.selectPermsByUserId(loginSysUser.getUserId());
			SecurityUser securityUser = new SecurityUser();
			securityUser.setUserId(loginSysUser.getUserId());
			securityUser.setPassword(loginSysUser.getPassword());
			securityUser.setShopId(loginSysUser.getShopId());
			securityUser.setStatus(loginSysUser.getStatus());
			securityUser.setLoginType(AuthConstants.LOGIN_TYPE_SYS);
			if (ObjectUtils.isNotEmpty(perms)) {
				securityUser.setPerms(perms);
			}
			return securityUser;
		}
		return null;
	}
}
