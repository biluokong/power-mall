package com.biluo.config;

import cn.hutool.json.JSONUtil;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.HttpConstants;
import com.biluo.constant.ResourceConstants;
import com.biluo.filter.TokenTranslationFilter;
import com.biluo.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

/**
 * Spring Security安全框架的资源服务器配置类
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {
    private final TokenTranslationFilter tokenTranslationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用跨站请求伪造（CSRF）保护
        http.csrf().disable();
        // 禁用CORS（跨源资源共享）功能
        http.cors().disable();
        // 禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // token解析过滤器，将token转换为security安全框架能够认证的用户信息，再存放到当前资源服务器的容器中
        http.addFilterBefore(tokenTranslationFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置处理携带token但权限不足的请求
        http.exceptionHandling()
                // 处理没有携带token的请求
                .authenticationEntryPoint((request, response, authException) -> {
                    // 设置响应头信息
                    response.setContentType(HttpConstants.APPLICATION_JSON);
                    response.setCharacterEncoding(HttpConstants.UTF_8);

                    // 创建项目统一响应结果对象
                    Result<Object> result = Result.fail(BusinessEnum.UN_AUTHORIZATION);
                    String json = JSONUtil.toJsonStr(result);
                    PrintWriter writer = response.getWriter();
                    writer.write(json);
                    writer.flush();
                    writer.close();
                })
                // 处理携带token，但是权限不足的请求
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // 设置响应头信息
                    response.setContentType(HttpConstants.APPLICATION_JSON);
                    response.setCharacterEncoding(HttpConstants.UTF_8);

                    // 创建项目统一响应结果对象
                    Result<Object> result = Result.fail(BusinessEnum.ACCESS_DENY_FAIL);
                    String json = JSONUtil.toJsonStr(result);
                    PrintWriter writer = response.getWriter();
                    writer.write(json);
                    writer.flush();
                    writer.close();
                });

        // 配置其它请求
        http.authorizeHttpRequests()
                .antMatchers(ResourceConstants.RESOURCE_ALLOW_URLS)
                .permitAll()
                .anyRequest().authenticated();  // 除了需要放行的请求，都得需要进行身份的认证


    }
}
