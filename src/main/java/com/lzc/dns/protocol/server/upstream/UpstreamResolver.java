package com.lzc.dns.protocol.server.upstream;

import com.lzc.dns.protocol.entity.RecursiveResponse;
import com.lzc.dns.util.Configs;
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
public class UpstreamResolver {
    private ThreadPoolExecutor resolverPool = new AliooThreadPoolExecutor(
            Configs.getInt("dns.upstream.resolver.workers", 4),
            Configs.getInt("dns.upstream.resolver.workers", 4),
            0, TimeUnit.DAYS,
            new LinkedBlockingQueue<>(10000),
            "upstream-resolver",
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public boolean resolveRequest(RecursiveResponse recursiveResponse) {
        EagleEye.set(EagleEyeContext.create(recursiveResponse.getTraceId()));

        resolverPool.execute(new UpstreamResolveTask(recursiveResponse));

        return true;
    }

}