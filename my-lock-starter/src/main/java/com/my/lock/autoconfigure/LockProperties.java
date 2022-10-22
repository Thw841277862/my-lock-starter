package com.my.lock.autoconfigure;

import com.my.lock.executor.LockExecutor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * lock配置
 */
@Data
@ConfigurationProperties(prefix = "lock")
public class LockProperties {

    /**
     * 过期时间 单位：毫秒
     */
    private Long expire = 30000L;

    /**
     * 获取锁超时时间 单位：毫秒
     */
    private Long acquireTimeout = 3000L;

    /**
     * 获取锁失败时重试时间间隔 单位：毫秒
     */
    private Long retryInterval = 100L;

    /**
     * 锁key前缀
     */
    private String lockKeyPrefix = "Lock";

    /**
     * 默认执行器
     */
    private Class<? extends LockExecutor> defaultExecutor;
}
