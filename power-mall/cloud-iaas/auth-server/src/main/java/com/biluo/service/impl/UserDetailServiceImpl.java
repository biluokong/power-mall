package com.biluo.service.impl;

import com.biluo.constant.AuthConstants;
import com.biluo.factory.LoginStrategyFactory;
import com.biluo.strategy.LoginStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
	private final LoginStrategyFactory loginStrategyFactory;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		String loginType = request.getHeader(AuthConstants.LOGIN_TYPE);
		// 根据请求类型判断登录请求是哪个系统的
		if (!StringUtils.hasText(loginType)) {
			throw new InternalAuthenticationServiceException("登录类型不能为空");
		}
		// 为了便于扩展（登录方式可能多种），使用策略模式
		LoginStrategy instance = loginStrategyFactory.getInstance(loginType);
		return instance.doLogin(username);
	}
}
