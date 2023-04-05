package com.lzc.dns.manager;


import com.lzc.dns.protocol.entity.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogManager {

    public static void log(Response response) {
        log.info("dns_query_result_type:{} ## name:{} ## type:{} ## address:{} ## ttl(s):{} ## cost_time(ms):{}",
                response.getResultType(), response.getName(), response.getType(), response.getRecords(), response.getTtl(), (System.currentTimeMillis() - response.getStartTime()));
    }

}
