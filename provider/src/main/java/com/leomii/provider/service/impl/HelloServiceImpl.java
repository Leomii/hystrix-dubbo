package com.leomii.provider.service.impl;

import com.leomii.service.HelloService;

/**
 * @author leomii
 * @since 2018-1-25
 */
public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        return name + ",hello!";
    }
}