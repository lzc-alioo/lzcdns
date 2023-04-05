package com.lzc.dns.protocol.enums;

/**
 * 查询DNS结果分类
 */
public enum QueryResultType {
    RULE("RULE", "基于本地规则"),
    CACHE("CACHE", "基于本地缓存"),
    UP_SERVER("UP_SERVER", "基于上游DNS服务器"),

    ;

    private String type;
    private String desc;

    QueryResultType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
