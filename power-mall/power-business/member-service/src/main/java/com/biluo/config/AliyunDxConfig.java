package com.biluo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 阿里云短信服务参数配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.dx")
@RefreshScope
public class AliyunDxConfig {
    /**
     * 阿里云平台访问API接口accessKeyID
     */
    private String accessKeyID;
    /**
     * 阿里云平台访问API接口accessKeySecret
     */
    private String accessKeySecret;
    /**
     * 短信签名
     */
    private String signName;
    /**
     * 短信模版CODE
     */
    private String templateCode;

    /**
     * API访问端点
     */
    private String endpoint;
}
