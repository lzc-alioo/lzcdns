package com.lzc.dns.protocol.server.upstream;

import com.alibaba.fastjson.JSON;
import com.lzc.dns.protocol.entity.OriginalRequest;
import com.lzc.dns.protocol.entity.Request;
import com.lzc.dns.util.Packet;
import com.lzc.dns.util.pool.EagleEye;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@Slf4j
public class UpstreamSendTask extends Thread {
    private UpstreamServer upstreamServer;
    private DatagramChannel datagramChannel;
    private SocketAddress upstreamNameServer;

    private Request request;

    public UpstreamSendTask(UpstreamServer upstreamServer, DatagramChannel datagramChannel, SocketAddress upstreamNameServer, Request request) {
        this.upstreamServer = upstreamServer;
        this.datagramChannel = datagramChannel;
        this.upstreamNameServer = upstreamNameServer;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            send(request);
        } catch (Exception e) {
            log.error("send udp package error request:{}", JSON.toJSONString(request), e);
        }
    }


    private void send(Request request) throws IOException {
        short newSequence = request.getNewSequence();
        short originSequence = request.getOriginSequence();

        upstreamServer.saveUpstreamRequest(newSequence, new OriginalRequest(originSequence, request.remoteAddress, request.startTime, EagleEye.getTraceId()));

        Packet packet = request.packet;
        packet.seek(0).setShort(newSequence);

        send(datagramChannel, upstreamNameServer, packet);
    }


    private void send(DatagramChannel datagramChannel, SocketAddress upstreamNameServer, Packet packet) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(packet.getBytes());
        buffer.flip();
        datagramChannel.send(buffer, upstreamNameServer);
    }

}
