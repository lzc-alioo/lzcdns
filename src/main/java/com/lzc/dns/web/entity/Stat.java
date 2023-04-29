package com.lzc.dns.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Stat {
    //cache,3,guzzoni-apple-com.v.aaplimg.com,65,192.168.16.202
    /**
     * 查询DNS结果分类
     *
     * @link com.lzc.dns.protocol.enums.QueryResultType
     */
    private String resultType;

    private Long costTime;

    private String name;

    private Integer type;

    private String remoteIp;


}
