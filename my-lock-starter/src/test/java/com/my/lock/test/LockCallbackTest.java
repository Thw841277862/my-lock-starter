package com.my.lock.test;

import com.my.lock.LockTimeoutCallback;
import org.aopalliance.intercept.MethodInvocation;

public class LockCallbackTest implements LockTimeoutCallback {
    @Override
    public Object onCallback(String key, MethodInvocation invocation) {
        return "onCallback";
    }
}
