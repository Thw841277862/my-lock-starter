package com.my.lock.test;

import com.my.lock.LockTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = LockTestApplication.class)
public class LockTests {
    @Autowired
    TestService testService;

    @Test
     void getValueLock() throws InterruptedException {
        String value = testService.getValue("lock-test-1011");
        System.out.println("----: " + value);
    }
}
