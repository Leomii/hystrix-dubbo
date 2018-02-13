package com.leomii.provider.filter;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;


@Activate(group = Constants.PROVIDER)
public class PreLogFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        System.out.println("dubbo provider filter ,arguments:" + invocation.getArguments());
        return invoker.invoke(invocation);
    }
}