package com.lzc.dns.protocol.server.upstream;

import com.lzc.dns.protocol.entity.Request;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.pool.AliooThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用独立的线程池去发送响应报文
 */
@Slf4j
public class UpstreamSender {
    private ThreadPoolExecutor senderPool = new AliooThreadPoolExecutor(
            Configs.getInt("dns.upstream.sender.workers", 4),
            Configs.getInt("dns.upstream.sender.workers", 4),
            0, TimeUnit.DAYS,
            new LinkedBlockingQueue<>(10000),
            "upstream-sender",
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private UpstreamServer upstreamServer;
    private DatagramChannel datagramChannel;
    private SocketAddress upstreamNameServer;

    public UpstreamSender(UpstreamServer upstreamServer, DatagramChannel datagramChannel) {
        this.upstreamServer = upstreamServer;
        this.datagramChannel = datagramChannel;

        upstreamNameServer = new InetSocketAddress(Configs.get("dns.upstream.server.address", "192.168.16.233"), Configs.getInt("dns.upstream.server.port", 53));
    }

    public boolean sendRequest(Request request) {
        senderPool.execute(new UpstreamSendTask(upstreamServer, datagramChannel, upstreamNameServer, request));
        return true;
    }

    public SocketAddress getUpstreamNameServer() {
        return upstreamNameServer;
    }
}