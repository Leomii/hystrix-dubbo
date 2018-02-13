package com.leomii.consumer.service.impl;

import com.leomii.consumer.aop.EnableHystrix;
import com.leomii.consumer.service.WelcomeService;
import com.leomii.service.HelloService;

/**
 * @author leomii
 * @since 2018-1-25
 */
public class WelcomeServiceImpl implements WelcomeService {

    private HelloService helloService;

    public HelloService getHelloService() {
        return helloService;
    }

    @EnableHystrix
    @Override
    public String welcome(String name) {
        return helloService.sayHello(name);
    }

    @Override
    public String hello(String name) {
        return helloService.sayHello(name);
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}