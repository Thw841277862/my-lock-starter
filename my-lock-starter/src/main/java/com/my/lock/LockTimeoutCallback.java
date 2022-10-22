package com.my.lock;

import org.aopalliance.intercept.MethodInvocation;

/**
 * 获取锁异常处理
 */
public interface LockTimeoutCallback {

    /**
     * 获取锁超时回调
     *
     * @param key        锁key
     * @param invocation aop拦截的方法
     * @return 返回的参数
     */
    Object onCallback(String key, MethodInvocation invocation);
}
