package com.my.lock.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
//@EnableAspectJAutoProxy
public class LockTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(LockTestApplication.class);
    }
}