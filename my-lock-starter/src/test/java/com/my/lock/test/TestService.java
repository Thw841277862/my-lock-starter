package com.my.lock.test;

import com.my.lock.annotation.MyLock;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @MyLock(keys = {"#name"}, timeoutCallback = LockCallbackTest.class)
    public String getValue(String name) throws InterruptedException {
        Thread.sleep(1000 * 3);
        return "ok";
    }
}
