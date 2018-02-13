package com.leomii.consumer.service;

/**
 * @author leomii
 * @since 2018-1-25
 */
public interface WelcomeService {

    /**
     * 打招呼方法
     *
     * @param name 姓名
     * @return String 招呼字符串
     */
    String welcome(String name);

    String hello(String name);
}