package com.lzc.dns.protocol.server.local;

import com.lzc.dns.protocol.entity.Response;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.pool.AliooThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.DatagramChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用独立的线程池去发送响应报文
 */
@Slf4j
public class NameSender {
    private ThreadPoolExecutor nameSenderPool = new AliooThreadPoolExecutor(
            Configs.getInt("dns.server.sender.workers", 4)/2,
            Configs.getInt("dns.server.sender.workers", 4),
            300, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100000),
            "name-sender",
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private DatagramChannel datagramChannel;

    public NameSender(DatagramChannel datagramChannel) {
        this.datagramChannel = datagramChannel;
    }

    public boolean sendResponse(Response response) {
        nameSenderPool.execute(new NameSendTask(datagramChannel, response));
        return true;
    }

}