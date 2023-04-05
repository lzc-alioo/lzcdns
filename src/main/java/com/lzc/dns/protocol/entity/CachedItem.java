package com.lzc.dns.protocol.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.lzc.dns.util.JSONDateDeserializer;
import com.lzc.dns.util.JSONSerializer;

/**
 * Created by matrixy on 2019/5/6.
 */
public class CachedItem<T> {
    public T entity;

    /**
     * 剩余的时间戳
     */
    @JSONField(deserializeUsing = JSONDateDeserializer.class, serializeUsing = JSONSerializer.class)
    public long expireTime;

    /**
     * Time To Live
     * 剩余的时间
     */
    private int ttl;

    public CachedItem(T entity, long expireTime) {
        this.entity = entity;
        this.expireTime = expireTime;
    }

    public int getTtl() {
        return (int) ((this.expireTime - System.currentTimeMillis()) / 1000);
    }

    public boolean expired() {
        return System.currentTimeMillis() > expireTime;
    }
}
