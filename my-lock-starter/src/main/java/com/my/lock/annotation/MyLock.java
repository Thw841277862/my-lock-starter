package com.my.lock.annotation;

import com.my.lock.LockTimeoutCallback;
import com.my.lock.autoconfigure.LockProperties;
import com.my.lock.executor.LockExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式注解
 * @author
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MyLock {

    /**
     * 锁的名称
     * 为空时的默认值为：包名+类名+方法名
     *
     * @return 锁名称
     */
    String name() default "";

    /**
     * spel表达式
     * 列：keys = {"#user.id", "#user.name"}
     *
     * @return key
     */
    String[] keys() default "";

    /**
     * 锁的过期时间 单位：毫秒
     * 未设置则为默认时间30秒 默认值：{@link LockProperties#expire}
     *
     * @return 过期时间
     */
    long expire() default -1;

    /**
     * 获取锁超时时间 单位：毫秒
     * 未设置则为默认时间3秒 默认值：{@link LockProperties#acquireTimeout}
     *
     * @return 获取锁超时时间
     */
    long acquireTimeout() default -1;

    /**
     * 自动释放锁
     * 如果为false，锁将不会自动释放直至到达过期时间才释放 {@link MyLock#expire()}
     *
     * @return 是否自动释放锁
     */
    boolean autoRelease() default true;

    /**
     * 配置执行器
     *
     * @return 执行器
     */
    Class<? extends LockExecutor> executor() default LockExecutor.class;

    /**
     * 锁获取超时回调
     *
     * @return 回调
     */
    Class<? extends LockTimeoutCallback> timeoutCallback();
}
