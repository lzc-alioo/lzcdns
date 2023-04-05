package com.lzc.dns.manager.entity;

import com.lzc.dns.util.IPUtils;

/**
 * Created by matrixy on 2019/5/9.
 */
public class IPStat {
    public long ip;
    public int queryCount;

    public IPStat(long ip, int queryCount) {
        this.ip = ip;
        this.queryCount = queryCount;
    }

    public String getIP() {
        return IPUtils.fromInteger(ip);
    }

    public int getQueryCount() {
        return this.queryCount;
    }
}
