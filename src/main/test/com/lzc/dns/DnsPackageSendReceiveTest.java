package com.lzc.dns;

import com.lzc.dns.util.ByteUtils;
import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.function.Consumer;

@Slf4j
public class DnsPackageSendReceiveTest {
    short sequence = 10;

    public DatagramChannel datagramChannel = null;

    @Before
    public void init() {
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);

            DatagramChannel finalDatagramChannel = datagramChannel;

            Consumer<byte[]> consumer = (byte[] message) -> resolve(message);

            new Thread(() -> DnsUtil.listen(finalDatagramChannel, consumer)).start();

        } catch (Exception e) {
            log.error("Upstream NameServer error...", e);
        }
    }

    private static boolean resolve(byte[] message) {
        short sequence = (short) ByteUtils.getShort(message, 0, 2);
        log.info("resolve sequence:{},message:{}", sequence, message);

        return true;
    }

    @Test
    public void testQuery2() {


        //c.apple.news
        String domain = "c.apple.news";

        //5, 98, 97, 105, 100, 117, 3, 99, 111, 109, //domain baidu.com
        //1,99,5,97,112,112,108,101,4,110,101,119,115,  //domain c.apple.news

        //12(header组成)= 2(Transaction ID)+2(Flags)+2(Questions)+2(Answer RRs)+2(Authority RRs)+2(Additional RRs)
        byte[] message = {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, //header ,length=12
                5, 98, 97, 105, 100, 117, 3, 99, 111, 109,  //domain
                0,    //domain end
                0, 1, //queryType
                0, 1 //queryClass
        };
        Packet packet = PackageBuilder.build(message, sequence++);

        SocketAddress upstreamNameServer = new InetSocketAddress("114.114.114.114", 53);
        DnsUtil.send(datagramChannel, upstreamNameServer, packet);

        log.info("send sequence:{} message:{}", sequence, packet.getBytes());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("sequence end :{}", sequence);
    }


}
