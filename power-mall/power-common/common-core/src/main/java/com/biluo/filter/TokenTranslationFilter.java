package com.biluo.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.biluo.constant.AuthConstants;
import com.biluo.model.SecurityUser;
import com.biluo.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * token转换过滤器
 */
@Component
@RequiredArgsConstructor
public class TokenTranslationFilter extends OncePerRequestFilter {
	private final StringRedisTemplate stringRedisTemplate;

	/**
	 * token转换过滤器
	 * 前提：
	 * 只负责处理携带token的请求，然后将认证的用户信息转换出来
	 * 没有携带token的请求，交给security资源配置类中的处理器进行处理
	 * <p>
	 * 1.获取token
	 * 2.判断token是否有值
	 * 有：
	 * token转换为用户信息
	 * 将用户信息转换为security框架认识的用户信息对象
	 * 再将认识的用户信息对象存放到当前资源服务的容器中
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 从请求头中获取Authorization的值，格式为:bearer token
		String authorizationValue = request.getHeader(AuthConstants.AUTH_HEADER_KEY);
		// 判断是否有值
		if (StringUtils.hasText(authorizationValue)) {
			// 获取token
			String token = authorizationValue.replaceFirst(AuthConstants.TOKEN_PREFIX, "");
			// 判断token是否有值
			if (StringUtils.hasText(token)) {
				// 解决token续签的问题
				// 从redis中获取token的存活时长
				String key = AuthConstants.TOKEN_REDIS_PREFIX + token;
				Long expire = stringRedisTemplate.getExpire(key);
				// 判断是否超过系统指定的阈值
				if (expire < AuthConstants.TOKEN_EXPIRE_THRESHOLD_TIME) {
					// 给当前用户的token续签（本质就是增加token在redis中的存活时长）
					stringRedisTemplate.expire(key, AuthConstants.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
				}

				// 从redis中获取json格式字符串的认证用户信息
				String userJsonStr = stringRedisTemplate.opsForValue().get(key);
				// 将json格式字符串的认证用户信息转换为认证用户对象
				SecurityUser securityUser = JsonUtil.toObject(userJsonStr, SecurityUser.class);
				// 处理权限
				Set<SimpleGrantedAuthority> collect = securityUser.getPerms().stream()
						.map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
				// 创建UsernamePasswordAuthenticationToken对象
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(securityUser, null, collect);

				// 将认证用户对象存放到当前模块的上下方中
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}
