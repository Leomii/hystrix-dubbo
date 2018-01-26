package com.leomii.forkjoin;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * @author ronglei
 */
public class BlackListE implements Callable {

    @Override
    public Boolean call() {
        System.out.println("BlackListE.call" + LocalDateTime.now());
        return false;
    }
}