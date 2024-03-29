package com.lzc.dns;

import com.lzc.dns.manager.RuleManager;
import com.lzc.dns.protocol.server.local.NameServer;
import com.lzc.dns.protocol.server.upstream.UpstreamServer;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by matrixy on 2019/4/19.
 */
//@ComponentScan(value = {"com.lzc.dns"})
@Slf4j
@SpringBootApplication
@MapperScan("com.lzc.dns.web.dao")
public class Application {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        SpringUtils.init(context);
        Configs.init(context);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.info("The JVM exit success!!!")));

        RuleManager.getInstance().init();
        UpstreamServer.getInstance().init();
        NameServer.getInstance().init();
    }
}
