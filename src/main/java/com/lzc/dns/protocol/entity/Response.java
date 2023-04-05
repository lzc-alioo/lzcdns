package com.lzc.dns.protocol.entity;

import lombok.Data;

import java.net.SocketAddress;

/**
 * Created by matrixy on 2019/4/25.
 */
@Data
public class Response {

    private SocketAddress remoteAddress;
    private byte[] packet;

    //以下内容主要是为了打印日志------------------------------------------------
    /**
     * DNS查询域名
     */
    private String name;
    /**
     * 1： “A”，表示 IPv4 地址
     * 2： “NS”，域名服务器的名字
     * 28：“AAAA”，表示 IPv6 地址
     * 5： “CNAME”，规范名，经常会有一个 CNAME 跟着一票 A 和 AAAA
     * 41： “Additional records” 附加信息
     */
    private int type;
    /**
     * 记录DNS查询的原始时间戳
     */
    private long startTime;
    /**
     * 查询DNS结果分类
     *
     * @link com.lzc.dns.protocol.enums.QueryResultType
     */
    private String resultType;

    /**
     * DNS查询域名结果（存放着IP及各个IP对应的TTL）
     */
    private ResourceRecord[] records;

    /**
     * 值是上面各个IP中TTl中的最小值
     */
    private int ttl;


//    public Response(SocketAddress remoteAddress, byte[] packet, String name, long startTime, String resultType, ResourceRecord[] records, int ttl) {
//        this.packet = packet;
//        this.remoteAddress = remoteAddress;
//        this.name = name;
//        this.startTime = startTime;
//        this.resultType = resultType;
//        this.records = records;
//        this.ttl = ttl;
//    }

    public Response(SocketAddress remoteAddress, byte[] packet, String name, int type, long startTime, String resultType, ResourceRecord[] records, int ttl) {
        this.packet = packet;
        this.remoteAddress = remoteAddress;
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.resultType = resultType;
        this.records = records;
        this.ttl = ttl;
    }


}
