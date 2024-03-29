package com.my.lock.autoconfigure;

import com.my.lock.LockTemplate;
import com.my.lock.aop.LockAnnotationAdvisor;
import com.my.lock.aop.LockInterceptor;
import com.my.lock.executor.LockExecutor;
import com.my.lock.spel.DefaultLockKeyBuilder;
import com.my.lock.spel.LockKeyBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * 分布式锁自动配置器
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LockProperties.class)
public class LockAutoConfiguration {
    private final LockProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public LockTemplate lockTemplate(List<LockExecutor> executors) {
        LockTemplate lockTemplate = new LockTemplate();
        lockTemplate.setProperties(properties);
        lockTemplate.setExecutors(executors);
        return lockTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public LockKeyBuilder lockKeyBuilder() {
        return new DefaultLockKeyBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public LockInterceptor lockInterceptor(LockTemplate lockTemplate, LockKeyBuilder lockKeyBuilder) {
        return new LockInterceptor(lockTemplate, lockKeyBuilder, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LockAnnotationAdvisor lockAnnotationAdvisor(LockInterceptor lockInterceptor) {
        return new LockAnnotationAdvisor(lockInterceptor, Ordered.HIGHEST_PRECEDENCE);
    }
}
