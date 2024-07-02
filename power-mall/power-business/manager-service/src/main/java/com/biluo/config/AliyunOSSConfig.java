package com.biluo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@RefreshScope
public class AliyunOSSConfig {
	private String endpoint;
	private String bucketName;
	private String accessKey;
	private String secretKey;
}
