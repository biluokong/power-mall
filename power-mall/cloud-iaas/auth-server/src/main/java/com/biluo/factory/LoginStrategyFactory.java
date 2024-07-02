package com.biluo.factory;

import com.biluo.strategy.LoginStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 登录策略工厂
 */
@Component
@RequiredArgsConstructor
public class LoginStrategyFactory {
	private final Map<String, LoginStrategy> loginStrategyMap;

	public LoginStrategy getInstance(String loginType) {
		return loginStrategyMap.get(loginType);
	}
}
