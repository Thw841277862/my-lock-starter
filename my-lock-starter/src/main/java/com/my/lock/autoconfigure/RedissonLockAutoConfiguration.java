package com.my.lock.autoconfigure;

import com.my.lock.executor.impl.FairLockExecutor;
import com.my.lock.executor.impl.ReadLockExecutor;
import com.my.lock.executor.impl.RedissonLockExecutor;
import com.my.lock.executor.impl.WriteLockExecutor;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson锁自动配置器
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(Redisson.class)
class RedissonLockAutoConfiguration {

    /**
     * 重入锁执行器
     *
     * @param redissonClient Redisson 接口
     * @return 执行器
     */
    @Bean
    public RedissonLockExecutor redissonLockExecutor(RedissonClient redissonClient) {
        return new RedissonLockExecutor(redissonClient);
    }

    /**
     * 公平锁执行器
     *
     * @param redissonClient Redisson 接口
     * @return 执行器
     */
    @Bean
    public FairLockExecutor fairLockExecutor(RedissonClient redissonClient) {
        return new FairLockExecutor(redissonClient);
    }

    /**
     * 读锁执行器
     *
     * @param redissonClient Redisson 接口
     * @return 执行器
     */
    @Bean
    public ReadLockExecutor readLockExecutor(RedissonClient redissonClient) {
        return new ReadLockExecutor(redissonClient);
    }

    /**
     * 写锁执行器
     *
     * @param redissonClient Redisson 接口
     * @return 执行器
     */
    @Bean
    public WriteLockExecutor writeLockExecutor(RedissonClient redissonClient) {
        return new WriteLockExecutor(redissonClient);
    }
}

