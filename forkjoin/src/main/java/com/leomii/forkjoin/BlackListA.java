package com.leomii.forkjoin;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * @author ronglei
 */
public class BlackListA implements Callable {

    @Override
    public Boolean call() {
        System.out.println("BlackListA.call" + LocalDateTime.now());
        return true;
    }
}
