package com.lzc.dns.protocol.entity;

import lombok.Data;

import java.net.SocketAddress;

/**
 * Created by matrixy on 2019/4/25.
 */
@Data
public class RecursiveResponse {
    /**
     * 查询DNS原始的序列号
     */
    private short sequence;
    private SocketAddress remoteAddress;
    private byte[] packet;
    /**
     * 记录DNS查询的原始时间戳
     */
    private long startTime;
    /**
     * 记录原始traceId，日志跟踪
     */
    private String traceId;


    public RecursiveResponse(short sequence, SocketAddress remoteAddress, long startTime, String traceId, byte[] packet) {
        this.sequence = sequence;
        this.remoteAddress = remoteAddress;
        this.startTime = startTime;
        this.traceId = traceId;
        this.packet = packet;
    }

}
