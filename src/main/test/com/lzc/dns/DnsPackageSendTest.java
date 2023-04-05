package com.lzc.dns;

import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

@Slf4j
public class DnsPackageSendTest {
    short sequence = 1;

    DatagramChannel datagramChannel = null;


    @Before
    public void init() {
        try {
            log.info("init...");
        } catch (Exception e) {
            log.error("init error", e);
        }

    }


    @Test
    public void batchTestQuery() throws IOException {
        for (int i = 0; i < 10; i++) {
            testQuery();

        }
    }

    @Test
    public void testQuery() throws IOException {
        log.info("sequence:{}", sequence);

        DatagramChannel datagramChannel = null;
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);

        SocketAddress upstreamNameServer = null;
        upstreamNameServer = new InetSocketAddress("192.168.16.233", 53);

        byte[] message = {0, 1, 1, 32, 0, 1, 0, 0, 0, 0, 0, 1, //header ,length=12
                5, 98, 97, 105, 100, 117, 3, 99, 111, 109, //domain
                0,    //domain end
                0, 1, //queryType
                0, 1 //queryClass
        };
        Packet packet = Packet.create(message);

        packet.setShort(0, sequence++);

        DnsUtil.send(datagramChannel, upstreamNameServer, packet);

    }

    @Test
    public void testQuery2() {
        log.info("sequence start :{}", sequence);

        SocketAddress upstreamNameServer = new InetSocketAddress("114.114.114.114", 53);


        //c.apple.news
        String domain = "c.apple.news";

        //5, 98, 97, 105, 100, 117, 3, 99, 111, 109, //domain baidu.com
        //1,99,5,97,112,112,108,101,4,110,101,119,115,0,  //domain c.apple.news

        byte[] message = {0, 1, 1, 32, 0, 1, 0, 0, 0, 0, 0, 1, //header ,length=12
                1, 99, 5, 97, 112, 112, 108, 101, 4, 110, 101, 119, 115, 0,  //domain
                0,    //domain end
                0, 65, //queryType
                0, 1 //queryClass
        };
        Packet packet = Packet.create(message);
        packet.setShort(0, sequence++);


        DatagramChannel finalDatagramChannel1 = datagramChannel;
        SocketAddress finalUpstreamNameServer = upstreamNameServer;
        //new Thread(() -> send(finalDatagramChannel1, finalUpstreamNameServer, packet)).start();
        DnsUtil.send(finalDatagramChannel1, finalUpstreamNameServer, packet);

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("sequence end :{}", sequence);

    }

//
//    private void send(DatagramChannel datagramChannel, SocketAddress upstreamNameServer, Packet packet) {
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        buffer.put(packet.getBytes());
//        buffer.flip();
//        try {
//            datagramChannel.send(buffer, upstreamNameServer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
