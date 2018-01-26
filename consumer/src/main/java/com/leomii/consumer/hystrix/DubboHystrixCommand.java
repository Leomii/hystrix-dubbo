package com.leomii.consumer.hystrix;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.netflix.hystrix.*;
import org.apache.log4j.Logger;

/**
 * @author leomii
 * @since 2018-1-25
 * <p>
 * Dubbo hystrix command
 */
public class DubboHystrixCommand extends HystrixCommand<Result> {

    private static Logger logger = Logger.getLogger(DubboHystrixCommand.class);
    private Invoker<?> invoker;
    private Invocation invocation;
    /**
     * 线程池大小 x
     */
    private static int DEFAULT_THREADPOOL_CORE_SIZE = 30;
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
     * 使用dubbo的超时，禁用这里的超时
     */
    private static int EXECUTION_TIMEOUT_ENABLED = 0;


    public DubboHystrixCommand(Invoker<?> invoker, Invocation invocation) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", invocation.getMethodName(),
                        invocation.getArguments() == null ? 0 : invocation.getArguments().length)))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD)
                        .withCircuitBreakerSleepWindowInMilliseconds(CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS)
                        .withCircuitBreakerErrorThresholdPercentage(CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE)
                        .withExecutionTimeoutEnabled(EXECUTION_TIMEOUT_ENABLED == 0 ? false : true)
                )
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(getThreadPoolCoreSize(invoker.getUrl()))));


        this.invoker = invoker;
        this.invocation = invocation;
    }

    @Override
    public Result getFallback() {
        return null;
    }

    /**
     * 获取线程池大小
     *
     * @param url
     * @return
     */
    private static int getThreadPoolCoreSize(URL url) {
        if (url != null) {
            int size = url.getParameter("ThreadPoolCoreSize", DEFAULT_THREADPOOL_CORE_SIZE);
            if (logger.isDebugEnabled()) {
                logger.debug("ThreadPoolCoreSize:" + size);
            }
            return size;
        }

        return DEFAULT_THREADPOOL_CORE_SIZE;

    }

    @Override
    protected Result run() throws Exception {
        return invoker.invoke(invocation);
    }
}