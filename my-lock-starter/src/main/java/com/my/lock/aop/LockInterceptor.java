package com.my.lock.aop;

import cn.hutool.extra.spring.SpringUtil;
import com.my.lock.LockInfo;
import com.my.lock.LockTemplate;
import com.my.lock.annotation.MyLock;
import com.my.lock.autoconfigure.LockProperties;
import com.my.lock.spel.LockKeyBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 分布式锁aop处理器
 */
@Slf4j
@RequiredArgsConstructor
public class LockInterceptor implements MethodInterceptor {
    private final LockTemplate lockTemplate;

    private final LockKeyBuilder lockKeyBuilder;

    private final LockProperties lock4jProperties;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> cls = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        // 避免多次AOP
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        // 获取注解信息
        MyLock myLock = invocation.getMethod().getAnnotation(MyLock.class);
        // 定义锁的信息
        LockInfo lockInfo = null;
        try {
            // 锁Key
            String key = getLockKey(myLock, invocation);
            // 获取锁
            lockInfo = lockTemplate.lock(key, myLock.expire(), myLock.acquireTimeout(), myLock.executor());
            // 如果获取锁失败
            if (null == lockInfo) {
                return SpringUtil.getBean(myLock.timeoutCallback()).onCallback(key, invocation);
            }
            return invocation.proceed();
        } finally {
            // 释放锁
            if (null != lockInfo && myLock.autoRelease()) {
                boolean releaseLock = lockTemplate.releaseLock(lockInfo);
                if (!releaseLock) {
                    log.error("release lock fail,lockKey={},lockValue={}", lockInfo.getLockKey(), lockInfo.getLockValue());
                }
            }
        }
    }

    /**
     * 获取锁Key
     *
     * @param myLock  分布式锁注解
     * @param invocation aop拦截的方法
     * @return key
     */
    private String getLockKey(MyLock myLock, MethodInvocation invocation) {
        // 获取key的前缀
        String prefix = getLockPrefix(myLock, invocation);
        return prefix + lockKeyBuilder.buildKey(invocation, myLock.keys());
    }

    /**
     * 获取锁的前缀
     *
     * @return prefix
     */
    private String getLockPrefix(MyLock myLock, MethodInvocation invocation) {
        String prefix = lock4jProperties.getLockKeyPrefix() + "#";
        return Optional.ofNullable(myLock.name())
                .filter(StringUtils::hasText)
                .map(name -> prefix + name)
                .orElse(prefix + invocation.getMethod().getDeclaringClass().getName() + invocation.getMethod().getName());
    }
}
