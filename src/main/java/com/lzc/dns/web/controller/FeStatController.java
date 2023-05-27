package com.lzc.dns.web.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.lzc.dns.manager.CacheManager;
import com.lzc.dns.manager.StatManager;
import com.lzc.dns.manager.entity.RankVo;
import com.lzc.dns.web.entity.Result;
import com.lzc.dns.web.service.StatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by matrixy on 2019/5/10.
 */
@Slf4j
@Controller
@RequestMapping("/fe/manage/stat")
public class FeStatController extends BaseController {

    @Autowired
    private StatService statService;

    // 请求示例：
    // http://localhost:8053/manage/stat/summary2?monitorDate=2023-04-29&startTime=03:00&endTime=03:40
    @RequestMapping("/summary2")
    @ResponseBody
    public Map<String, List<RankVo>> summary2(String monitorDate, String startTime, String endTime) {
        log.info("summary2 monitorDate:{} startTime:{} endTime:{}", monitorDate, startTime, endTime);
        Map<String, List<RankVo>> map = statService.topStat(monitorDate, startTime, endTime);
        return map;
    }

    // 几个计数项：总查询次数、总应答次数、总回源查询次数、当前缓存量
    @RequestMapping("/summary")
    @ResponseBody
    public Result summary() {
        Result result = new Result();
        try {
            StatManager mgr = StatManager.getInstance();
            CacheStats cacheStats = CacheManager.getInstance().getCacheStats();
            result.setData(Result.values("totalQueryCount", mgr.getTotalQueryCount(),
                    "totalAnswerCount", mgr.getTotalAnswerCount(),
                    "totalQueryUpstreamCount", mgr.getTotalQueryUpstreamCount(),
                    "totalCachedCount", CacheManager.getInstance().getCachedCount(),
                    "queryCachedCount", (cacheStats.hitCount() + cacheStats.missCount()),
                    "hitCachedCount", cacheStats.hitCount(),
                    "totalQueryNames", mgr.getTotalQueryNames()
            ));
        } catch (Exception ex) {
            result.setError(ex);
        }
        return result;
    }

    // 当天每分钟的查询量
    @RequestMapping("/everyMinuteQueries")
    @ResponseBody
    public Result everyMinuteQueries() {
        Result result = new Result();
        try {
            result.setData(StatManager.getInstance().getEveryMinuteQueryCount());
        } catch (Exception ex) {
            result.setError(ex);
        }
        return result;
    }


}
