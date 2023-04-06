package com.lzc.dns;

import com.lzc.dns.manager.StatManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class StatManagerTest {


    @Test
    public void batchTestQuery() throws Exception {

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 80000; j++) {
                    try {
                        testQuery();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        Thread.sleep(5000L);

        long totalQueryCount = StatManager.getInstance().getTotalQueryCount();
        log.info("totalQueryCount:{}", totalQueryCount);
    }

    @Test
    public void testQuery() throws IOException {
        // dns服务器，日志打点
        StatManager.getInstance().log(12345, 1212, "lzc.com");

    }


}
