package com.biluo.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * redis缓存配置类
 */
@Configuration
public class RedisCacheConfig {
	@Bean
	public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				// 统一设置redis中值的默认过期时间（7天）
				.entryTtl(Duration.ofDays(7))
				// redis的value值禁止使用空值
				.disableCachingNullValues()
				// 变双冒号为单冒号
				.computePrefixWith(name -> name + ":")
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()))
				.disableCachingNullValues();
		return RedisCacheManager.RedisCacheManagerBuilder
				.fromConnectionFactory(lettuceConnectionFactory)
				.cacheDefaults(config)
				.transactionAware()
				.build();
	}
}
