package com.leomii.consumer.hystrix;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

/**
 * @author leomii
 * @since 2018-1-25
 * <p>
 * 通过dubbo spi filter 实现 hystrix 熔断限流
 */
@Activate(group = Constants.CONSUMER)
public class HystrixFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        DubboHystrixCommand command = new DubboHystrixCommand(invoker, invocation);
        return command.execute();
    }
}
