package com.biluo.strategy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biluo.config.WxParamConfig;
import com.biluo.constant.AuthConstants;
import com.biluo.domain.LoginMember;
import com.biluo.mapper.LoginMemberMapper;
import com.biluo.model.SecurityUser;
import com.biluo.strategy.LoginStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 商城购物系统登录策略
 */
@Service(AuthConstants.LOGIN_TYPE_MEMBER)
@RequiredArgsConstructor
public class MemberLoginStrategy implements LoginStrategy {
	private final WxParamConfig wxParamConfig;
	private final LoginMemberMapper loginMemberMapper;

	@Override
	public UserDetails doLogin(String username) {
		String realUrl = String.format(wxParamConfig.getUrl(), wxParamConfig.getAppid(),
				wxParamConfig.getSecret(), username);
		// 使用get方法调用登录凭证校验接口
		String jsonStr = HttpUtil.get(realUrl);
		// 判断响应是否有值
		if (!StringUtils.hasText(jsonStr)) {
			throw new InternalAuthenticationServiceException("登录异常，请重试");
		}
		JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
		String openid = jsonObject.getStr("openid");
		if (!StringUtils.hasText(openid)) {
			throw new InternalAuthenticationServiceException("登录异常，请重试");
		}
		//根据openid获取用户信息
		LoginMember loginMember = loginMemberMapper.selectOne(new LambdaQueryWrapper<LoginMember>()
				.eq(LoginMember::getOpenId, openid));
		if (ObjectUtil.isNull(loginMember)) {
			//会员不存在，就注册
			loginMember = registerMember(openid);
		}
		// 会员存在，返回SecurityUser
		if (!loginMember.getStatus().equals(1)) {
			throw new InternalAuthenticationServiceException("登录异常，请联系管理员");
		}
		SecurityUser securityUser = new SecurityUser();
		securityUser.setUserId(Long.valueOf(loginMember.getId()));
		securityUser.setLoginType(AuthConstants.LOGIN_TYPE_MEMBER);
		securityUser.setUsername(openid);
		securityUser.setStatus(loginMember.getStatus());
		securityUser.setPassword(wxParamConfig.getPwd());
		securityUser.setOpenid(openid);
		return securityUser;
	}

	@Transactional(rollbackFor = Exception.class)
	public LoginMember registerMember(String openid) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		String ip = request.getRemoteAddr();

		LoginMember loginMember = new LoginMember();
		loginMember.setOpenId(openid);
		loginMember.setStatus(1);
		loginMember.setCreateTime(new Date());
		loginMember.setUpdateTime(new Date());
		loginMember.setUserLasttime(new Date());
		loginMember.setUserRegip(ip);
		loginMember.setUserLastip(ip);
		// 如果有积分业务，需要赋值
		loginMember.setScore(0);
		loginMemberMapper.insert(loginMember);
		return loginMember;
	}
}
