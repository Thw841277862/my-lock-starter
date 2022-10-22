package com.my.lock.executor.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 重入锁
 */
@RequiredArgsConstructor
public class RedissonLockExecutor extends AbstractLockExecutor {
    private final RedissonClient redissonClient;

    @Override
    public RLock getLock(String lockKey) {
        return  redissonClient.getLock(lockKey);
    }
}
