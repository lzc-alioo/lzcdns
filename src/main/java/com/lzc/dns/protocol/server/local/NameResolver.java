package com.lzc.dns.protocol.server.local;

import com.lzc.dns.protocol.entity.Request;
import com.lzc.dns.util.pool.AliooThreadPoolExecutor;
import com.lzc.dns.util.pool.EagleEye;
import com.lzc.dns.util.pool.EagleEyeContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用独立的线程池去发送响应报文
 */
@Slf4j
public class NameResolver {
    private ThreadPoolExecutor nameResolverPool = new AliooThreadPoolExecutor(4, 4, 0, TimeUnit.DAYS, new LinkedBlockingQueue<>(65536), "name-resolver", new ThreadPoolExecutor.DiscardOldestPolicy());

    private NameServer nameServer;

    public NameResolver(NameServer nameServer) {
        this.nameServer = nameServer;
    }

    public boolean resolveRequest(Request request) {
        EagleEye.set(EagleEyeContext.create());
        nameResolverPool.execute(new NameResolveTask(nameServer, request));

        return true;
    }

}