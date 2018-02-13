package com.leomii.consumer.hystrix;

import com.netflix.hystrix.*;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author leomii
 * @since 2018-1-25
 * <p>
 * Dubbo hystrix command
 */
public class ServciceHystrixCommand extends HystrixCommand<Object> {

    private static Logger logger = Logger.getLogger(ServciceHystrixCommand.class);
    private ProceedingJoinPoint joinPoint;
    /**
     * 线程池大小 x
     */
    private static int DEFAULT_THREADPOOL_CORE_SIZE = 5;
    /**
     * 10秒钟内至少 x 次请求失败，熔断器发挥作用
     */
    private static int CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD = 20;
    /**
     * 熔断器中断请求 x 秒后会进入半打开状态,放部分流量过去重试
     */
    private static int CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS = 30000;
    /**
     * 错误率达到 x 开启熔断保护
     */
    private static int CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE = 50;
    /**
     * 是否开启超时时间中断抛出异常的功能
     */
    private static boolean EXECUTION_TIMEOUT_ENABLED = false;


    public ServciceHystrixCommand(String group, String command, String pool, ProceedingJoinPoint joinPoint) {
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(command))
                        .andCommandPropertiesDefaults
                                (
                                        HystrixCommandProperties.Setter()
                                                .withCircuitBreakerRequestVolumeThreshold(CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD)
                                                .withCircuitBreakerSleepWindowInMilliseconds(CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS)
                                                .withCircuitBreakerErrorThresholdPercentage(CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE)
                                                .withExecutionTimeoutEnabled(EXECUTION_TIMEOUT_ENABLED)
                                )
                        .andThreadPoolPropertiesDefaults
                                (
                                        HystrixThreadPoolProperties.Setter().withCoreSize(DEFAULT_THREADPOOL_CORE_SIZE)
                                )
        );
        this.joinPoint = joinPoint;
    }


    @Override
    protected Object run() {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}