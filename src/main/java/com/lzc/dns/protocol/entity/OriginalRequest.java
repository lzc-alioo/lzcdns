package com.lzc.dns.protocol.entity;

import lombok.Data;

import java.net.SocketAddress;

@Data
public class OriginalRequest {

    private short sequence;
    private SocketAddress remoteAddress;
    /**
     * 记录DNS查询的原始时间戳
     */
    private long startTime;

    /**
     * 记录原始traceId，日志跟踪
     */
    private String traceId;

    public OriginalRequest(short seq, SocketAddress addr, long startTime, String traceId) {
        this.sequence = seq;
        this.remoteAddress = addr;
        this.startTime = startTime;
        this.traceId = traceId;
    }

}
