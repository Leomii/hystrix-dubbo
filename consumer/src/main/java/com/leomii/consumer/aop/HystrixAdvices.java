package com.leomii.consumer.aop;


import com.leomii.consumer.hystrix.ServciceHystrixCommand;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知类，横切逻辑
 *
 * @author leomii
 */
public class HystrixAdvices {

    private Map<String, ServciceHystrixCommand> commandMap = new ConcurrentHashMap<>();

    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Boolean result = targetMethod.isAnnotationPresent(EnableHystrix.class);

        if (!result) {
            return joinPoint.proceed();
        }

        String group = methodName + "group";
        String command = String.format("%s_%d", methodName, joinPoint.getArgs() == null ? 0 : joinPoint.getArgs().length);
        String pool = methodName + "pool";

        ServciceHystrixCommand shc = new ServciceHystrixCommand(group, command, pool, joinPoint);
        System.out.println("ServciceHystrixCommand:" + shc.hashCode());
        return shc.execute();
    }
}