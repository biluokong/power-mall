package com.biluo.strategy;

import org.springframework.security.core.userdetails.UserDetails;

public interface LoginStrategy {
	UserDetails doLogin(String username);
}
