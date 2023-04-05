package com.lzc.dns.manager.entity;

/**
 * Created by matrixy on 2019/5/8.
 */
public class DomainNameStat {
    public String name;
    public int queryCount;

    public DomainNameStat(String name, int queryCount) {
        this.name = name;
        this.queryCount = queryCount;
    }
}
