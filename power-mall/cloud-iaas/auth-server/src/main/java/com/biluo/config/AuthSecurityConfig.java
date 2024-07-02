package com.biluo.config;

import cn.hutool.json.JSONUtil;
import com.biluo.constant.AuthConstants;
import com.biluo.constant.HttpConstants;
import com.biluo.model.LoginResult;
import com.biluo.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class AuthSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailService;
	private final StringRedisTemplate stringRedisTemplate;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 配置认证流程
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 禁用跨站请求伪造（CSRF）保护
		http.csrf().disable();
		// 禁用CORS（跨源资源共享）功能
		http.cors().disable();
		// 禁用session
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		// 配置哪些请求需要认证
		http.authorizeRequests().anyRequest().authenticated();
		// 配置登录
		http.formLogin()
				.loginProcessingUrl(AuthConstants.LOGIN_URL)
				// 登录成功处理
				.successHandler((request, response, authentication) -> {
					response.setContentType(HttpConstants.APPLICATION_JSON);
					response.setCharacterEncoding(HttpConstants.UTF_8);

					String token = UUID.randomUUID().toString();
					// 获取认证对象
					String userJson = JSONUtil.toJsonStr(authentication.getPrincipal());
					stringRedisTemplate.opsForValue().set(AuthConstants.TOKEN_REDIS_PREFIX + token, userJson,
							AuthConstants.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
					Result<LoginResult> result = Result.success(new LoginResult(token, AuthConstants.TOKEN_EXPIRE_TIME));
					PrintWriter writer = response.getWriter();

					writer.write(JSONUtil.toJsonStr(result));
					writer.flush();
					writer.close();
				})
				// 登录失败处理
				.failureHandler((request, response, exception) -> {
					response.setContentType(HttpConstants.APPLICATION_JSON);
					response.setCharacterEncoding(HttpConstants.UTF_8);

					Result<Object> result = Result.fail();
					if (exception instanceof BadCredentialsException) {
						result.setMsg("用户名或密码错误");
					} else if (exception instanceof UsernameNotFoundException) {
						result.setMsg("用户不存在");
					} else if (exception instanceof AccountExpiredException) {
						result.setMsg("账号已过期");
					} else if (exception instanceof AccountStatusException) {
						result.setMsg("账号不可用");
					} else if (exception instanceof InternalAuthenticationServiceException) {
						result.setMsg(exception.getMessage());
					}

					PrintWriter writer = response.getWriter();
					writer.write(JSONUtil.toJsonStr(result));
					writer.flush();
					writer.close();
				});
		// 配置登出
		http.logout()
				.logoutUrl(AuthConstants.LOGOUT_URL)
				// 登出成功处理
				.logoutSuccessHandler((request, response, authentication) -> {
					response.setContentType(HttpConstants.APPLICATION_JSON);
					response.setCharacterEncoding(HttpConstants.UTF_8);

					String authorization = response.getHeader(AuthConstants.AUTH_HEADER_KEY);
					String token = authorization.replaceFirst(AuthConstants.TOKEN_PREFIX, "");
					stringRedisTemplate.delete(AuthConstants.TOKEN_REDIS_PREFIX + token);

					Result<Object> result = Result.success();
					PrintWriter writer = response.getWriter();
					writer.write(JSONUtil.toJsonStr(result));
					writer.flush();
					writer.close();
				});
	}
}
