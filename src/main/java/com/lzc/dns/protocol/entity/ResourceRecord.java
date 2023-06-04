package com.lzc.dns.protocol.entity;

import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.util.IPUtils;
import lombok.Data;

import java.net.Inet6Address;
import java.nio.charset.StandardCharsets;

/**
 * Created by matrixy on 2019/4/23.
 * 响应消息的资源记录，一般为域名所对应的IP或CNAME等
 */
@Data
public class ResourceRecord {
    //通用字段
    public String name;    //Answer Name
    public int type;       //Answer Type
    public int clazz;      //Answer Class 资源记录的类型，与问题部分中的查询类型值是一样的。

    public int ttl;        //Answer Time To Live(unit : second)
    public int dataLength; //Answer Data Length


    public long ipv4;            // type=A，则使用IP作为data，省得转来转去
    public Inet6Address ipv6;    // type=AAAA,记录ipv6地址
    private String nameServer;   // type=NS/SOA 记录nameServer ; type=CNAME 记录cname ; type=PTR 记录domainName
    private short preference;    // type=MX preference mx记录优先级
    private String mailExchange; // type=MX mailExchange 邮件交换服务器

    private String mailBox;      //type=SOA专用
    private int serialNumber;    //type=SOA专用
    private int refreshInterval; //type=SOA专用 单位：秒
    private int retryInterval;   //type=SOA专用 单位：秒
    private int expireTime;      //type=SOA专用 单位：秒
    private int minimumTtl;      //type=SOA专用 单位：秒

    private short priority;      //type=SRV专用
    private short weight;        //type=SRV专用
    private short port;          //type=SRV专用
    private String target;       //type=SRV专用


    public ResourceRecord(String name, int type, int clazz) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
    }

    /**
     * IPV4
     *
     * @param name
     * @param type
     * @param clazz
     * @param ttl
     * @param ipv4
     */
    public ResourceRecord(String name, int type, int clazz, int ttl, long ipv4) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.ipv4 = ipv4;
        this.dataLength = 4;
    }

    /**
     * IPV6
     *
     * @param name
     * @param type
     * @param clazz
     * @param ttl
     * @param ipv6
     */
    public ResourceRecord(String name, int type, int clazz, int ttl, Inet6Address ipv6) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.ipv6 = ipv6;
        this.dataLength = 16;
    }

    /**
     * NS/CNAME/PTR/TXT
     *
     * @param name
     * @param type
     * @param clazz
     * @param ttl
     * @param nameServer
     */
    public ResourceRecord(String name, int type, int clazz, int ttl, String nameServer) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.nameServer = nameServer;
        this.dataLength = nameServer.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * MX
     *
     * @param name
     * @param type
     * @param clazz
     * @param ttl
     * @param preference
     * @param mailExchange
     */
    public ResourceRecord(String name, int type, int clazz, int ttl, short preference, String mailExchange) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.preference = preference;
        this.mailExchange = mailExchange;
    }

    /**
     * SOA
     *
     * @param name
     * @param type
     * @param clazz
     * @param ttl
     * @param nameServer
     * @param mailBox
     * @param serialNumber
     * @param refreshInterval
     * @param retryInterval
     * @param expireTime
     * @param minimumTtl
     */
    public ResourceRecord(String name, int type, int clazz, int ttl, String nameServer, String mailBox, int serialNumber, int refreshInterval, int retryInterval, int expireTime, int minimumTtl) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.nameServer = nameServer;
        this.mailBox = mailBox;
        this.serialNumber = serialNumber;
        this.refreshInterval = refreshInterval;
        this.retryInterval = retryInterval;
        this.expireTime = expireTime;
        this.minimumTtl = minimumTtl;
    }

    /**
     * SRV
     *
     * @param name
     * @param type
     * @param clazz
     * @param ttl
     * @param priority
     * @param weight
     * @param port
     * @param target
     */
    public ResourceRecord(String name, int type, int clazz, int ttl, short priority, short weight, short port, String target) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;

        this.priority = priority;
        this.weight = weight;
        this.port = port;
        this.target = target;
    }


    public String toString() {
        if (type == MessageType.A.getType()) {
            return "{ip:" + IPUtils.fromInteger(this.ipv4) + ",ttl:" + ttl + "}";
        } else if (type == MessageType.AAAA.getType()) {
            return "{ip:" + ipv6.toString() + ",ttl:" + ttl + "}";
        } else if (type == MessageType.NS.getType()) {
            return "{ns:" + nameServer + ",ttl:" + ttl + "}";
        } else if (type == MessageType.MX.getType()) {
            return "{mx:" + mailExchange + ",preference:" + preference + ",ttl:" + ttl + "}";
        } else if (type == MessageType.CNAME.getType()) {
            return "{cname:" + nameServer + ",ttl:" + ttl + "}";
        } else if (type == MessageType.SOA.getType()) {
            return "{soa:" + nameServer + ",mailBox:" + mailBox + ",ttl:" + ttl + "}";
        } else if (type == MessageType.TXT.getType()) {
            return "{txt:" + nameServer + ",ttl:" + ttl + "}";
        } else if (type == MessageType.PTR.getType()) {
            return "{ptr:" + nameServer + ",ttl:" + ttl + "}";
        } else if (type == MessageType.SRV.getType()) {
            return "{srv: priority:" + priority + ",weight:" + weight + ",port:" + port + ",target:" + target + ",ttl:" + ttl + "}";
        } else {
            return "{ttl:" + ttl + "}";
        }
    }
}
