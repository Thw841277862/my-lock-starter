package com.my.lock.spel;

import org.aopalliance.intercept.MethodInvocation;

/**
 * key构造器
 */
public interface LockKeyBuilder {

    /**
     * 构建key
     *
     * @param invocation     invocation
     * @param definitionKeys 定义
     * @return key
     */
    String buildKey(MethodInvocation invocation, String[] definitionKeys);
}
