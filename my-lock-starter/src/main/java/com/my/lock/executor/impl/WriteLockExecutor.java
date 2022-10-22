package com.my.lock.executor.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

/**
 * 写锁
 */
@RequiredArgsConstructor
public class WriteLockExecutor extends AbstractLockExecutor {
    private final RedissonClient redissonClient;
    @Override
    public RLock getLock(String lockKey) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(lockKey);
        return readWriteLock.writeLock();
    }
}
