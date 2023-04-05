package com.lzc.dns.manager;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.lzc.dns.protocol.entity.CachedItem;
import com.lzc.dns.protocol.entity.ResourceRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by matrixy on 2019/4/23.
 */
@Slf4j
public final class CacheManager {
    Cache<String, CachedItem> cache = null;

    static volatile CacheManager instance;

    private CacheManager() {
        cache = domainCache();
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }


    public CachedItem get(String key) {
        CachedItem item = cache.getIfPresent(key);
        if (item == null) {
            return null;
        }
        if (item.expired()) {
            cache.invalidate(key);
            log.info("cache_key_expire key:{}", JSON.toJSONString(item));
            return null;
        }
        return item;
    }

    public void put(String key, ResourceRecord[] records, long expireTime) {
        cache.put(key, new CachedItem(records, expireTime));
    }

    public long getCachedCount() {
        return cache.estimatedSize();
    }

    public CacheStats getCacheStats() {
        CacheStats cacheStats = cache.stats();
        log.info("cacheStats:{}", cacheStats);

        return cacheStats;
    }


    public Cache<String, CachedItem> domainCache() {
        Cache<String, CachedItem> cache = Caffeine.newBuilder()
                //设置cache的初始大小为10，要合理设置该值
                .initialCapacity(1000)
                //记录统计信息
                .recordStats()
                //设置cache的容量上限
                .maximumSize(10_000)
                //缓存项在创建后，在给定时间内没有被读/写访问，则清除
                .expireAfterAccess(360, TimeUnit.MINUTES)
                //构建cache实例
                .build();

        return cache;
    }


}
