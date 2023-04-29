package com.lzc.dns.web.service;

import com.lzc.dns.util.DateTimeUtil;
import com.lzc.dns.web.entity.Stat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class StatService {

    @Value("${dns.stat.monitor-path}")
    String monitorPath;

    public Map<String, LinkedHashMap<String, Long>> topStat(String monitorDate, String startTime, String endTime) {

        List<Stat> list = this.readMonitorFile(monitorDate, startTime, endTime);

        //top10排序，按域名，按remoteIp
        LinkedHashMap<String, Long> domainNameStat = findTopNames(list, 10);
        LinkedHashMap<String, Long> ipStat = findTopClients(list, 10);

        Map<String, LinkedHashMap<String, Long>> map = new HashMap();
        map.put("domainNameStat", domainNameStat);
        map.put("ipStat", ipStat);

        return map;

    }

    /**
     * @param monitorDate 格式：yyyy-MM-dd
     * @param startTime   格式：HH:mm
     * @param endTime     格式：HH:mm
     * @return
     */
    private List<Stat> readMonitorFile(String monitorDate, String startTime, String endTime) {
        File monitorFile = getMonitorFile(monitorDate);
        log.info("getMonitorFile monitorDate:{} monitorFile:{}", monitorDate, monitorFile);

        if(monitorFile==null){
            return Collections.EMPTY_LIST;
        }
        try {
            Stream<String> stream = Files.lines(Paths.get(monitorFile.getAbsolutePath()), StandardCharsets.UTF_8);
            List<Stat> list = stream
                    .filter(str -> !ObjectUtils.isEmpty(str) && str.contains("dns_query_monitor"))
                    .filter(str -> {
                        if (ObjectUtils.isEmpty(startTime) || ObjectUtils.isEmpty(endTime)) {
                            return true;
                        }
                        //只提取指定时间区间的数据
                        String tmp = str.substring(11, 16);
                        return tmp.compareTo(startTime) >= 0 && tmp.compareTo(endTime) <= 0;
                    }).map(str -> {
                        int dataIdx = str.indexOf("dns_query_monitor") + "dns_query_monitor".length() + 1;
                        String data = str.substring(dataIdx, str.length());
                        String dataArr[] = data.split(",");

                        try {
                            Stat stat = Stat.builder().resultType(dataArr[0]).costTime(Long.parseLong(dataArr[1])).name(dataArr[2]).type(Integer.parseInt(dataArr[3])).remoteIp(dataArr[4]).build();

                            return stat;
                        } catch (Exception e) {
                            log.error("readMonitorFile error data:" + data, e);
                        }
                        return null;

                    }).filter(stat -> !ObjectUtils.isEmpty(stat)).collect(Collectors.toList());

            return list;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.EMPTY_LIST;
    }


    /**
     * @param monitorDate 格式：yyyy-MM-dd
     * @return
     */
    private File getMonitorFile(String monitorDate) {
        String todayDate = DateTimeUtil.getDateTimeString("yyyy-MM-dd");

        File defaultMonitorFile = new File(monitorPath + "/monitor.log");

        if (ObjectUtils.isEmpty(monitorDate) || todayDate.equals(monitorDate)) {
            return defaultMonitorFile;
        }

        File retFile = Arrays.stream(new File(monitorPath).listFiles())
                .filter((File file) -> file.getName().contains(todayDate))
                .findFirst()
                .orElse(null);

        return retFile;


    }


    // 按被查询域名查TOP N
    private LinkedHashMap<String, Long> findTopNames(List<Stat> list, int N) {
        Map<String, Long> identityMap = new HashMap<>();
        list.stream().map(stat -> stat.getName()).forEach(str -> {
            identityMap.putIfAbsent(str, 0L);
            identityMap.compute(str, (key, val) -> val + 1);

        });

        LinkedHashMap<String, Long> map = identityMap.entrySet().stream().sorted(((item1, item2) -> {
            int compare = item2.getValue().compareTo(item1.getValue());
            return compare;
        })).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        if (map.size() >= N) {
            LinkedHashMap<String, Long> map2 = new LinkedHashMap();
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                if (map2.size() < N)
                    map2.put(entry.getKey(), entry.getValue());
            }
            return map2;
        } else {
            return map;
        }
    }


    // 按查询来源IP查TOP N
    private LinkedHashMap<String, Long> findTopClients(List<Stat> list, int N) {
        Map<String, Long> identityMap = new HashMap<>();
        list.stream().map(stat -> stat.getRemoteIp()).forEach(str -> {
            identityMap.putIfAbsent(str, 0L);
            identityMap.compute(str, (key, val) -> val + 1);

        });

        LinkedHashMap<String, Long> map = identityMap.entrySet().stream().sorted(((item1, item2) -> {
            int compare = item2.getValue().compareTo(item1.getValue());
            return compare;
        })).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        if (map.size() >= N) {
            LinkedHashMap<String, Long> map2 = new LinkedHashMap();
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                if (map2.size() < N)
                    map2.put(entry.getKey(), entry.getValue());
            }
            return map2;
        } else {
            return map;
        }

    }


}
