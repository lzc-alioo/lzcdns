package com.lzc.dns.manager;


import com.lzc.dns.protocol.entity.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogManager {

    public static void log(Response response) {
        log.info("dns_query_monitor {},{},{},{}",
                response.getResultType().toLowerCase(),
                (System.currentTimeMillis() - response.getStartTime()),
                response.getName(),
                response.getType()
        );
    }

}
