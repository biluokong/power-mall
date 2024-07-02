package com.biluo.config;

import cn.hutool.core.util.ObjectUtil;
import com.biluo.constant.AuthConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * feign拦截器
 * 作用：解决服务之间调用没有token的情况
 * <p>
 * 浏览器 -> A服务 -> B服务
 * 定时器 -> A服务
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		// 获取当前请求的上下文对象
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		// 判断是否有值
		if (ObjectUtil.isNotNull(requestAttributes)) {
			// 获取请求对象
			HttpServletRequest request = requestAttributes.getRequest();
			// 判断是否有值
			if (ObjectUtil.isNotNull(request)) {
				// 获取当前请求头中的token值，传递到一下一个请求对象的请求头中
				String authorization = request.getHeader(AuthConstants.AUTH_HEADER_KEY);
				requestTemplate.header(AuthConstants.AUTH_HEADER_KEY, authorization);
				return;
			}
		}
		// 如果是走定时器或消息队列调用，则直接使用默认的token（该token在redis中设置了永不过期）
		requestTemplate.header(AuthConstants.AUTH_HEADER_KEY,
				AuthConstants.TOKEN_PREFIX + "7a2d5f70-c59c-4288-9e45-842689ae4594");

	}
}
