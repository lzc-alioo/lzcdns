package com.lzc.dns.manager;


import com.lzc.dns.protocol.entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class LogManager {
    private static Logger log = LoggerFactory.getLogger("LOGGER_MONITOR");

    public static final String EMPTY = "";

    public static void log(Response response) {
        log.info("dns_query_monitor {},{},{},{},{}",
                response.getResultType().toLowerCase(),
                (System.currentTimeMillis() - response.getStartTime()),
                response.getName(),
                response.getType(),
                getRemoteIp(response.getRemoteAddress())
        );

    }

    private static String getRemoteIp(SocketAddress remoteAddress) {
        if (remoteAddress != null && remoteAddress instanceof InetSocketAddress) {
//            NetworkInterface.getByInetAddress(((InetSocketAddress) remoteAddress))
//            ((InetSocketAddress) remoteAddress).
            return ((InetSocketAddress) remoteAddress).getHostString();
        }
        return EMPTY;

    }

}
