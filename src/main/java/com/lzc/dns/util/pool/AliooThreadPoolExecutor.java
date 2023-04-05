package com.lzc.dns.util.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AliooThreadPoolExecutor extends ThreadPoolExecutor {


    public AliooThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String poolName, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(poolName), handler);
    }

//    public AliThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, NamedThreadFactory namedThreadFactory, RejectedExecutionHandler handler) {
//        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, namedThreadFactory, handler);
//    }

    @Override
    public void execute(Runnable command) {
        EagleEyeContext eagleEyeContext = EagleEye.getEagleEyeContext();
        super.execute(() -> {
            try {
                EagleEye.set(eagleEyeContext);
                command.run();
            } catch (Exception e) {
                log.error("execute error", e);
            } finally {
                EagleEye.clean();
            }
        });
    }
}
