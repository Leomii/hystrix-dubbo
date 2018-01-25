package com.leomii.consumer.service.impl;

import com.leomii.consumer.service.WelcomeService;
import com.leomii.service.HelloService;

/**
 * @author leomii
 * @since 2018-1-25
 */
public class WelcomeServiceImpl implements WelcomeService {

    private HelloService helloService;

    public String welcome(String name) {
        return helloService.sayHello(name);
    }

    public HelloService getHelloService() {
        return helloService;
    }

    public void setHelloService(HelloService helloService) {
        this.helloService = helloService;
    }
}