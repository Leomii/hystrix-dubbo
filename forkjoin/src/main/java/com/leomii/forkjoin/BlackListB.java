package com.leomii.forkjoin;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * @author ronglei
 */
public class BlackListB implements Callable {

    @Override
    public Boolean call() {
        System.out.println("BlackListB.call" + LocalDateTime.now());
        return false;
    }
}
