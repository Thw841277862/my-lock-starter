package com.my.lock;

import com.my.lock.autoconfigure.LockProperties;
import com.my.lock.executor.LockExecutor;
import com.my.lock.executor.impl.RedissonLockExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 锁模板方法
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class LockTemplate implements InitializingBean {

    /**
     * 存放执行器的Map
     * Key为执行器的类名
     * Value为执行器对象
     */
    private final Map<Class<? extends LockExecutor>, LockExecutor> executorMap = new LinkedHashMap<>();

    /**
     * lock配置
     */
    @Setter
    private LockProperties properties;

    /**
     * 默认执行器
     */
    private LockExecutor defaultExecutor;

    @Override
    public void afterPropertiesSet() {
        // 校验获取锁超时时间
        Assert.isTrue(properties.getAcquireTimeout() >= 0, "tryTimeout must least 0");
        // 校验锁过期时间
        Assert.isTrue(properties.getExpire() >= -1, "expireTime must lease -1");
        // 校验锁失败时重试时间间隔
        Assert.isTrue(properties.getRetryInterval() >= 0, "retryInterval must more than 0");
        // 校验锁key前缀
        Assert.hasText(properties.getLockKeyPrefix(), "lock key prefix must be not blank");
    }

    /**
     * 获取锁
     *
     * @param key 锁key
     * @return 返回锁信息
     */
    public LockInfo lock(String key) {
        return lock(key, -1, properties.getAcquireTimeout());
    }

    /**
     * 获取锁
     *
     * @param key            锁key
     * @param expire         过期时间(ms)
     * @param acquireTimeout 尝试获取锁超时时间(ms)
     * @return 返回锁信息
     */
    public LockInfo lock(String key, long expire, long acquireTimeout) {
        return lock(key, expire, acquireTimeout, null);
    }

    /**
     * 加锁方法
     *
     * @param key            锁key 同一个key只能被一个客户端持有
     * @param expire         过期时间(ms) 防止死锁
     * @param acquireTimeout 尝试获取锁超时时间(ms)
     * @param executor       执行器
     * @return 加锁成功返回锁信息 失败返回null
     */
    public LockInfo lock(String key, long expire, long acquireTimeout, Class<? extends LockExecutor> executor) {
        acquireTimeout = acquireTimeout < 0 ? properties.getAcquireTimeout() : acquireTimeout;
        long retryInterval = properties.getRetryInterval();
        LockExecutor lockExecutor = obtainExecutor(executor);
        expire = !lockExecutor.renewal() && expire <= 0 ? properties.getExpire() : expire;
        int acquireCount = 0;
        String value = UUID.randomUUID().toString().replace("-", "");
        long start = System.currentTimeMillis();
        try {
            do {
                acquireCount++;
                Object lockInstance = lockExecutor.acquire(key, value, expire, acquireTimeout);
                if (null != lockInstance) {
                    return new LockInfo(key, value, expire, acquireTimeout, acquireCount, lockInstance, lockExecutor);
                }
                TimeUnit.MILLISECONDS.sleep(retryInterval);
            } while (System.currentTimeMillis() - start < acquireTimeout);
        } catch (InterruptedException e) {
            log.error("lock error", e);
        }
        return null;
    }

    /**
     * 释放锁
     *
     * @return 是否释放成功
     */
    public boolean releaseLock(LockInfo lockInfo) {
        // 传入锁为空,释放锁失败
        if (null == lockInfo) {
            return false;
        }
        return lockInfo.getLockExecutor().releaseLock(lockInfo.getLockKey(), lockInfo.getLockValue(), lockInfo.getLockInstance());
    }

    /**
     * 获取执行器
     *
     * @param clazz 执行器类名
     * @return 返回对应的执行器
     */
    private LockExecutor obtainExecutor(Class<? extends LockExecutor> clazz) {
        // 如果clazz为空或者传入的是个接口类则使用默认的执行器
        if (null == clazz || clazz == LockExecutor.class) {
            return defaultExecutor;
        }
        // 根据clazz查找对应的执行器
        LockExecutor lockExecutor = executorMap.get(clazz);
        Assert.notNull(lockExecutor, String.format("can not get bean type of %s", clazz));
        return lockExecutor;
    }

    /**
     * 设置执行器
     *
     * @param executors 执行器
     */
    public void setExecutors(List<LockExecutor> executors) {
        // 校验执行器是否为空
        Assert.notEmpty(executors, "executors must have at least one");
        // 执行器存放到Map容器中,方便后续通过clazz查找执行器
        for (LockExecutor executor : executors) {
            executorMap.put(executor.getClass(), executor);
        }
        // 配置的默认执行器
        Class<? extends LockExecutor> configDefaultExecutor = properties.getDefaultExecutor();
        // 默认使用可重入锁执行器
        if (null == configDefaultExecutor) {
            this.defaultExecutor = executorMap.get(RedissonLockExecutor.class);
        } else {
            this.defaultExecutor = executorMap.get(configDefaultExecutor);
            Assert.notNull(this.defaultExecutor, "primaryExecutor must be not null");
        }
    }
}
