package com.lzc.dns.protocol.entity;

import com.lzc.dns.util.Packet;
import lombok.Data;

import java.net.SocketAddress;

/**
 * Created by matrixy on 2019/4/24.
 */
@Data
public class Request {
    /**
     * 客户端原始的序列号
     */
    private short originSequence;
    /**
     * 向上游服务器DNS请求时使用篡改之后的序列号
     * 目的：是为了防止多个客户端之间查询时的序列号相同
     */
    private short newSequence;

    public Packet packet;
    /**
     * 记录DNS查询的原始客户端IP&Port
     */
    public SocketAddress remoteAddress;
    /**
     * 记录DNS查询的原始时间戳
     */
    public long startTime;

    public Request(SocketAddress remoteAddress, Packet packet) {
        this.packet = packet;
        this.remoteAddress = remoteAddress;

        this.startTime = System.currentTimeMillis();
    }
}
