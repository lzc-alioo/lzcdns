package com.lzc.dns;

import com.lzc.dns.protocol.enums.MessageType;
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
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);

        } catch (Exception e) {
            log.error("init error", e);
        }

    }


    @Test
    public void batchTestQuery() throws IOException {
        for (int i = 0; i < 80000; i++) {
            testQuery();

        }
    }

    @Test
    public void testQuery() throws IOException {
        log.info("sequence:{}", sequence);


        SocketAddress upstreamNameServer = new InetSocketAddress("192.168.16.233", 53);

//        byte[] message = {0, 1, 1, 32, 0, 1, 0, 0, 0, 0, 0, 1, //header ,length=12
//                5, 98, 97, 105, 100, 117, 3, 99, 111, 109, //domain
//                0,    //domain end
//                0, 1, //queryType
//                0, 1 //queryClass
//        };
//        Packet packet = Packet.create(message);

//        Packet packet = QuestionNameEncoder.buildPacket( "df.tanx.com", sequence++, (short) MessageType.HTTPS.getType());
        Packet packet = QuestionNameEncoder.buildPacket("iterm2.com", sequence, (short) MessageType.HTTPS.getType());
        packet.setShort(0, sequence);

        DnsUtil.send(datagramChannel, upstreamNameServer, packet);

        sequence++;
    }

    @Test
    public void testQuery2() {
        log.info("sequence start :{}", sequence);

        SocketAddress upstreamNameServer = new InetSocketAddress("192.168.16.233", 53);

        Packet packet = QuestionNameEncoder.buildPacket("df.tanx.com.gds.alibabadns.com", sequence, (short) MessageType.HTTPS.getType());

        packet.setShort(0, sequence);

        DnsUtil.send(datagramChannel, upstreamNameServer, packet);

        sequence++;

    }


}
