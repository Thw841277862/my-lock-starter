package com.my.lock.executor.impl;

import com.my.lock.executor.LockExecutor;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * 抽象锁的执行器
 */
public abstract class AbstractLockExecutor implements LockExecutor {
    public abstract RLock getLock(String lockKey);

    @Override
    public boolean renewal() {
        return true;
    }

    @Override
    public Object acquire(String lockKey, String lockValue, long expire, long acquireTimeout) {
        RLock lockInstance = getLock(lockKey);
        //尝试获取锁
        try {
            boolean locked = lockInstance.tryLock(acquireTimeout, expire, TimeUnit.MILLISECONDS);
            return locked ? lockInstance : null;
        } catch (InterruptedException interruptedException) {
            return null;
        }
    }

    @Override
    public boolean releaseLock(String key, String value, Object lockInstance) {
        if (!(lockInstance instanceof RLock)) {
            return false;
        }
        RLock instance = (RLock) lockInstance;
        //检查此锁是否由当前线程持有
        if (instance.isHeldByCurrentThread()) {
            try {
                instance.unlock();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}
