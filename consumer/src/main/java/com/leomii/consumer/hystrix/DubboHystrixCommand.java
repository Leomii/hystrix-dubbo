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
     * 是否开启超时时间中断抛出异常的功能
     */
    private static int EXECUTION_TIMEOUT_ENABLED = 0;


    public DubboHystrixCommand(Invoker<?> invoker, Invocation invocation) {
        super
                (
                        Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
                                .andCommandKey
                                        (
                                                HystrixCommandKey.Factory.asKey
                                                        (
                                                                String.format("%s_%d", invocation.getMethodName(),
                                                                        invocation.getArguments() == null ? 0 : invocation.getArguments().length)
                                                        )
                                        )
                                .andCommandPropertiesDefaults
                                        (
                                                HystrixCommandProperties.Setter()
                                                        //设置在一个滚动窗口中，打开断路器的最少请求数;默认值：20
                                                        .withCircuitBreakerRequestVolumeThreshold(CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD)

                                                        //设置在回路被打开，拒绝请求到再次尝试请求并决定回路是否继续打开的时间;默认值：5000（毫秒）
                                                        .withCircuitBreakerSleepWindowInMilliseconds(CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS)

                                                        //设置打开回路并启动回退逻辑的错误比率;默认值：50
                                                        .withCircuitBreakerErrorThresholdPercentage(CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE)

                                                        //是否开启超时时间中断抛出异常的功能 ;默认值：true
                                                        .withExecutionTimeoutEnabled(EXECUTION_TIMEOUT_ENABLED == 0 ? false : true)

                                                        //设置断路器是否起作用;默认值：true
                                                        .withCircuitBreakerEnabled(true)

                                                        //如果该属性设置为true，强制断路器进入关闭状态，将会允许所有的请求，无视错误率;默认值：false
                                                        .withCircuitBreakerForceClosed(false)

                                                        //如果该属性设置为true，强制断路器进入打开状态，将会拒绝所有的请求；该属性优先级比circuitBreaker.forceClosed高；默认值：false
                                                        .withCircuitBreakerForceOpen(false)

                                                        //设置当使用ExecutionIsolationStrategy.SEMAPHORE(信号隔离)时，HystrixCommand.run()方法允许的最大请求数。
                                                        // 如果达到最大并发数时，后续请求会被拒绝。
                                                        //信号量应该是容器（比如Tomcat）线程池一小部分，不能等于或者略小于容器线程池大小，否则起不到保护作用。
                                                        //默认值：10
                                                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)

                                                        //隔离策略:线程 || 信号量
                                                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)

                                                        //设置HystrixCommand.run()的执行是否在超时发生时被中断;默认值：true
                                                        .withExecutionIsolationThreadInterruptOnTimeout(true)

                                                        //当隔离策略为THREAD时，当执行线程执行超时时，是否进行中断处理，即Future#cancel(true)处理，默认为false。
                                                        .withExecutionIsolationThreadInterruptOnFutureCancel(false)

                                                        //设置调用者等待命令执行的超时限制，超过此时间，HystrixCommand被标记为TIMEOUT，并执行回退逻辑;默认值：1000（毫秒）
                                                        .withExecutionTimeoutInMilliseconds(1000)

                                                        //设置调用线程产生的HystrixCommand.getFallback()方法的允许最大请求数目。
                                                        // 如果达到最大并发数目，后续请求将会被拒绝，如果没有实现回退，则抛出异常。
                                                        // 默认值：10
                                                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)

                                                        //该属性决定当故障或者拒绝发生时，一个调用将会去尝试HystrixCommand.getFallback()。默认值：true
                                                        .withFallbackEnabled(true)

                                                        //记录健康采用统计的快照频率，默认为500ms，即500ms一个采样统计间隔，那么桶的数量为10000/500=20个。
                                                        .withMetricsHealthSnapshotIntervalInMilliseconds(500)


                                                        //设置每个bucket内执行的次数,如果超过这个次数,丢弃最早的，加入最新的
                                                        .withMetricsRollingPercentileBucketSize(100)

                                                        //是否开启监控统计功能,如果设置false,任何统计都返回-1
                                                        .withMetricsRollingPercentileEnabled(true)

                                                        //用于计算百分比的滚动窗口时间长度
                                                        .withMetricsRollingPercentileWindowInMilliseconds(60000)

                                                        //用于计算百分比的滚动窗口内buckets的个数
                                                        .withMetricsRollingPercentileWindowBuckets(6)

                                                        //设置统计的滚动窗口的时间段大小。该属性是线程池保持指标时间长短;默认值：10000（毫秒）
                                                        .withMetricsRollingStatisticalWindowInMilliseconds(10000)

                                                        //设置滚动的统计窗口被分成的桶（bucket）的数目;默认值：10
                                                        .withMetricsRollingStatisticalWindowBuckets(10)

                                                        //设置HystrixCommand.getCacheKey()是否启用，由HystrixRequestCache通过请求缓存提供去重复数据功能;默认值：true
                                                        .withRequestCacheEnabled(true)

                                                        //设置HystrixCommand执行和事件是否要记录日志到HystrixRequestLog;默认值：true
                                                        .withRequestLogEnabled(true)
                                        )
                                .andThreadPoolPropertiesDefaults
                                        (
                                                HystrixThreadPoolProperties.Setter()
                                                        //设置核心线程池大小;默认值：10
                                                        .withCoreSize(getThreadPoolCoreSize(invoker.getUrl()))

                                                        //.withMaximumSize(0)

                                                        //设置存活时间，单位分钟。如果coreSize小于maximumSize，那么该属性控制一个线程从实用完成到被释放的时间;默认值：1
                                                        .withKeepAliveTimeMinutes(1)

                                                        //设置BlockingQueue最大的队列值。
                                                        //如果设置为-1，那么使用SynchronousQueue，否则正数将会使用LinkedBlockingQueue。
                                                        //如果需要去除这些限制，允许队列动态变化，可以参考queueSizeRejectionThreshold属性。
                                                        //修改SynchronousQueue和LinkedBlockingQueue需要重启。
                                                        //默认值：-1
                                                        .withMaxQueueSize(-1)

                                                        //设置队列拒绝的阈值——一个人为设置的拒绝访问的最大队列值，即使maxQueueSize还没有达到。
                                                        //当将一个线程放入队列等待执行时，HystrixCommand使用该属性。
                                                        //注意：如果maxQueueSize设置为-1，该属性不可用。
                                                        //默认值：5
                                                        .withQueueSizeRejectionThreshold(5)

                                                        //该属性允许maximumSize起作用。属性值可以等于或者大于coreSize值，
                                                        // 设置coreSize小于maximumSize的线程池能够支持maximumSize的并发数，但是会将不活跃的线程返回到系统中去。
                                                        //默认值：false
                                                        .withAllowMaximumSizeToDivergeFromCoreSize(false)

                                                        //设置统计的滚动窗口的时间段大小。该属性是线程池保持指标时间长短;默认值：10000（毫秒）
                                                        .withMetricsRollingStatisticalWindowInMilliseconds(10000)

                                                        //可统计的滚动窗口内的buckets数量,用于熔断器和指标发布
                                                        //设置滚动的统计窗口被分成的桶（bucket）的数目。
                                                        //注意：”metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0"必须为true，否则会抛出异常。
                                                        //默认值：10
                                                        .withMetricsRollingStatisticalWindowBuckets(10)
                                        )
                );


        this.invoker = invoker;
        this.invocation = invocation;
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