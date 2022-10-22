package com.my.lock.executor.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 公平锁
 */
@RequiredArgsConstructor
public class FairLockExecutor extends AbstractLockExecutor {
    private final RedissonClient redissonClient;

    @Override
    public RLock getLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }
}
