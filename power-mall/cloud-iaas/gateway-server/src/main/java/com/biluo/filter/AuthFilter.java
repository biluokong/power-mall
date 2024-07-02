package com.biluo.filter;

import com.biluo.config.WhiteUrlsConfig;
import com.biluo.constant.AuthConstants;
import com.biluo.constant.BusinessEnum;
import com.biluo.model.Result;
import com.biluo.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.biluo.constant.HttpConstants.APPLICATION_JSON;
import static com.biluo.constant.HttpConstants.CONTENT_TYPE;

/**
 * 全局token过滤器，约定请求头中的Authorization字段为token
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {
	private final WhiteUrlsConfig whiteUrlsConfig;
	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().toString();
		// 对白名单的路径放行
		if (whiteUrlsConfig.getAllowUrls().contains(path)) {
			return chain.filter(exchange);
		}
		// 不在白名单的路径进行token校验，只有token合法有效才放行
		String authorization = request.getHeaders().getFirst(AuthConstants.AUTH_HEADER_KEY);
		if (StringUtils.hasText(authorization)) {
			String token = authorization.replaceFirst(AuthConstants.TOKEN_PREFIX, "");
			if (StringUtils.hasText(token) &&
					Boolean.TRUE.equals(stringRedisTemplate.hasKey(AuthConstants.TOKEN_REDIS_PREFIX + token))) {
				return chain.filter(exchange);
			}
		}
		log.error("拦截到非法请求：{}", path);
		ServerHttpResponse response = exchange.getResponse();
		//response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().set(CONTENT_TYPE, APPLICATION_JSON);
		byte[] bytes = JsonUtil.toBytes(Result.fail(BusinessEnum.UN_AUTHORIZATION));
		DataBuffer wrap = response.bufferFactory().wrap(bytes);
		return response.writeWith(Mono.just(wrap));
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
