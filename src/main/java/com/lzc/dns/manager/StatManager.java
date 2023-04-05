package com.lzc.dns.manager;

import com.lzc.dns.manager.entity.DomainNameStat;
import com.lzc.dns.manager.entity.IPStat;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by matrixy on 2019/5/8.
 */
public final class StatManager {
    static Logger logger = LoggerFactory.getLogger(StatManager.class);

    private Map<String, DomainNameStat> domainNameMap = null;
    private Map<Long, IPStat> ipStatMap = null;

    // 当天问询次数
    AtomicInteger totalQueryCount;

    // 当天问询上游次数
    AtomicInteger totalQueryUpstreamCount;

    // 查询应答次数
    AtomicInteger totalAnswerCount;

    // 当天每分钟查询次数
    int[] everyMinuteQueries;

    private StatManager() {
        String lmem = Configs.get("dns.stat-logger.memory");
        int i = Conversion.toByte(lmem);
        domainNameMap = new HashMap();
        ipStatMap = new HashMap();

        totalQueryCount = new AtomicInteger(0);
        totalQueryUpstreamCount = new AtomicInteger(0);
        totalAnswerCount = new AtomicInteger(0);
        everyMinuteQueries = new int[60 * 24];

        // TODO: 启动定时器，在每日0:00:00时清空一次数据
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reset();
            }
        }, 1000, 1000 * 60);
    }

    // 记录查询日志，保存查询时的来源IP、时间（时分秒）、域名（通过map转为数字id，提高复用率）
    public synchronized void log(long ip, int time, String domainName) {
        totalQueryCount.addAndGet(1);
        // 时分钟转为自0:00:00起经过的分钟数
        logEveryMinuteQueries(time);

        // 如果配置为不记录查询日志，则直接跳出
        if ("on".equals(Configs.get("dns.stat-logger")) == false) {
            logger.info("realtime logger was turned off");
            return;
        }

        DomainNameStat domainNameStat = domainNameMap.get(domainName);
        if (domainNameStat == null) {
            domainNameStat = new DomainNameStat(domainName, 1);
            domainNameMap.put(domainName, domainNameStat);
        }
        domainNameStat.queryCount += 1;


        IPStat ipStat = ipStatMap.get(ip);
        if (ipStat == null) {
            ipStat = new IPStat(ip, 1);
            ipStatMap.put(ip, ipStat);
        }
        ipStat.queryCount += 1;

    }

    private void logEveryMinuteQueries(int time) {
        int hours = time / 10000, minutes = (time - hours * 10000) / 100;
        int m = hours * 60 + minutes;
        everyMinuteQueries[m] += 1;
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

    public void addQueryUpstreamCount() {
        this.totalQueryUpstreamCount.addAndGet(1);
    }

    public void addAnswerCount() {
        this.totalAnswerCount.addAndGet(1);
    }

    // 查询每分钟查询量
    public int[] getEveryMinuteQueryCount() {
        Date now = new Date();
        int minutes = now.getHours() * 60 + now.getMinutes();
        int[] queries = new int[minutes];
        System.arraycopy(everyMinuteQueries, 0, queries, 0, minutes);
        return queries;
    }

    // 按查询来源IP查TOP N
    public List<IPStat> findTopClients(int N) {
        // 排序后取前N
        List<IPStat> statList = new ArrayList(ipStatMap.values());
        Collections.sort(statList, new Comparator<IPStat>() {
            @Override
            public int compare(IPStat o1, IPStat o2) {
                return o2.queryCount - o1.queryCount;
            }
        });

        if (statList.size() >= N) return statList.subList(0, N);
        else return statList;

    }

    // 按被查询域名查TOP N
    public List findTopNames(int N) {
        // 排序后取前N
        List<DomainNameStat> statList = new ArrayList(domainNameMap.values());
        Collections.sort(statList, new Comparator<DomainNameStat>() {
            @Override
            public int compare(DomainNameStat o1, DomainNameStat o2) {
                return o2.queryCount - o1.queryCount;
            }
        });

        if (statList.size() >= N) return statList.subList(0, N);
        else return statList;
    }

    // 重置计数/统计项、日志信息等
    private synchronized void reset() {
        Date now = new Date();
        if (now.getHours() == 0 && now.getMinutes() == 0) {
            totalQueryCount.set(0);
            totalQueryUpstreamCount.set(0);
            totalAnswerCount.set(0);

            Arrays.fill(everyMinuteQueries, 0);

            //logs.reset();
            domainNameMap.clear();
            ipStatMap.clear();
        }
    }

    static StatManager instance = null;

    public static synchronized StatManager getInstance() {
        if (null == instance) instance = new StatManager();
        return instance;
    }


}
