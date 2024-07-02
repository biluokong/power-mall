package com.biluo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
@RefreshScope
public class WxParamConfig {
	private String appid;
	private String secret;
	private String url;

	/**
	 * 微信小程序用户的登录密码（固定不变）
	 */
	private String pwd;
}
