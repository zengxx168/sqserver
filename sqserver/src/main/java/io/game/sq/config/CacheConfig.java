package io.game.sq.config;

import org.cache2k.Cache2kBuilder;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean("cacheManager")
    public SpringCache2kCacheManager cacheManager() {
        SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager();
        Cache2kBuilder<Object, Object> cache2kBuilder = Cache2kBuilder.forUnknownTypes();
        cache2kBuilder.expireAfterWrite(2, TimeUnit.HOURS);
        cacheManager.defaultSetup(b -> cache2kBuilder);
        return cacheManager;
    }

}
