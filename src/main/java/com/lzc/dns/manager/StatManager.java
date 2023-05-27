package com.lzc.dns.manager;

import com.alibaba.fastjson.JSON;
import com.lzc.dns.manager.entity.DomainNameStat;
import com.lzc.dns.manager.entity.IPStat;
import com.lzc.dns.util.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by matrixy on 2019/5/8.
 */
public final class StatManager {
    static Logger log = LoggerFactory.getLogger(StatManager.class);

    private Map<String, DomainNameStat> domainNameMap = null;
    private Map<Long, IPStat> ipStatMap = null;

    // 当天问询次数
    AtomicInteger totalQueryCount;

    // 当天问询上游次数
    AtomicInteger totalQueryUpstreamCount;

    // 查询应答次数
    AtomicInteger totalAnswerCount;

    // 当天每分钟查询次数
    private AtomicIntegerArray everyMinuteQueries;

    private StatManager() {
        domainNameMap = new ConcurrentHashMap<>();
        ipStatMap = new ConcurrentHashMap<>();

        totalQueryCount = new AtomicInteger(0);
        totalQueryUpstreamCount = new AtomicInteger(0);
        totalAnswerCount = new AtomicInteger(0);
        everyMinuteQueries = new AtomicIntegerArray(60 * 24);

        // TODO: 启动定时器，在每日0:00:00时清空一次数据
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reset();
            }
        }, 1000, 1000 * 60);
    }

    // 记录查询日志，保存查询时的来源IP、时间（时分秒）、域名（通过map转为数字id，提高复用率）
    public void log(long ip, int time, String domainName) {
        totalQueryCount.addAndGet(1);
        // 时分钟转为自0:00:00起经过的分钟数
        logEveryMinuteQueries(time);

        // 如果配置为不记录查询日志，则直接跳出
        if ("on".equals(Configs.get("dns.stat-logger", "on")) == false) {
            log.info("realtime logger was turned off");
            return;
        }

        DomainNameStat domainNameStat = new DomainNameStat(domainName, 0);
        domainNameMap.putIfAbsent(domainName, domainNameStat);
        domainNameMap.compute(domainName, (k, v) -> {
            v.queryCount++;
            return v;
        });

        IPStat ipStat = new IPStat(ip, 0);
        ipStatMap.putIfAbsent(ip, ipStat);
        ipStatMap.compute(ip, (k, v) -> {
            v.queryCount++;
            return v;
        });
    }


    public void addQueryUpstreamCount() {
        this.totalQueryUpstreamCount.addAndGet(1);
    }

    public void addAnswerCount() {
        this.totalAnswerCount.addAndGet(1);
    }


    private void logEveryMinuteQueries(int time) {
        int hours = time / 10000, minutes = (time - hours * 10000) / 100;
        int m = hours * 60 + minutes;
        everyMinuteQueries.addAndGet(m, 1);

    }

    // 总查询次数
    public int getTotalQueryCount() {
        return this.totalQueryCount.intValue();
    }

    // 总查询上游查询次数
    public int getTotalQueryUpstreamCount() {
        return this.totalQueryUpstreamCount.intValue();
    }

    // 总应答次数
    public int getTotalAnswerCount() {
        return this.totalAnswerCount.intValue();
    }

    // 总查询域名数
    public int getTotalQueryNames() {
        return this.domainNameMap.size();
    }

    // 查询每分钟查询量
    public int[] getEveryMinuteQueryCount() {
        Date now = new Date();
        int minutes = now.getHours() * 60 + now.getMinutes();

        int[] queries = new int[minutes];
        for (int i = 0; i < minutes; i++) {
            queries[i] = everyMinuteQueries.get(i);
        }
        return queries;
    }

//    // 按查询来源IP查TOP N
//    public List<IPStat> findTopClients(int N) {
//        // 排序后取前N
//        List<IPStat> statList = new ArrayList(ipStatMap.values());
//        Collections.sort(statList, new Comparator<IPStat>() {
//            @Override
//            public int compare(IPStat o1, IPStat o2) {
//                return o2.queryCount - o1.queryCount;
//            }
//        });
//
//        if (statList.size() >= N) return statList.subList(0, N);
//        else return statList;
//
//    }

//    // 按被查询域名查TOP N
//    public List<DomainNameStat> findTopNames(int N) {
//        // 排序后取前N
//        List<DomainNameStat> statList = new ArrayList(domainNameMap.values());
//        Collections.sort(statList, new Comparator<DomainNameStat>() {
//            @Override
//            public int compare(DomainNameStat o1, DomainNameStat o2) {
//                return o2.queryCount - o1.queryCount;
//            }
//        });
//
//        if (statList.size() >= N) {
//            return statList.subList(0, N);
//        } else {
//            return statList;
//        }
//    }

    // 重置计数/统计项、日志信息等
    private synchronized void reset() {
        Date now = new Date();
        if (now.getHours() == 0 && now.getMinutes() == 0) {
            totalQueryCount.set(0);
            totalQueryUpstreamCount.set(0);
            totalAnswerCount.set(0);

            //  Arrays.fill(everyMinuteQueries, 0);
            for (int i = 0; i < everyMinuteQueries.length(); i++) {
                everyMinuteQueries.set(i, 0);
            }

            //logBeforeReset();

            //logs.reset();
            domainNameMap.clear();
            ipStatMap.clear();
        }
    }

//    private void logBeforeReset() {
//        List<IPStat> ipStatList = findTopClients(10);
//        log.info("统计信息 ipStatList:{}", JSON.toJSONString(ipStatList));
//
//        List<DomainNameStat> domainNameStatList = findTopNames(10);
//        log.info("统计信息 domainNameStatList:{}", JSON.toJSONString(domainNameStatList));
//
//    }

    static StatManager instance = null;

    public static synchronized StatManager getInstance() {
        if (null == instance) {
            instance = new StatManager();
        }
        return instance;
    }


}
