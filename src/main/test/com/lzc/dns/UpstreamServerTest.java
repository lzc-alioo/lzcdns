package com.lzc.dns;

import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.protocol.server.upstream.UpstreamServer;
import com.lzc.dns.protocol.entity.Request;
import com.lzc.dns.util.SpringUtils;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
public class UpstreamServerTest {
    short sequence = 10000;

    @Before
    public void init() {
        try {
            log.info("init...");
            ApplicationContext context = SpringApplication.run(Application.class, new String[]{});
            SpringUtils.init(context);
            Configs.init(context);

            UpstreamServer.getInstance().init();
        } catch (Exception e) {
            log.error("init error", e);
        }
    }


    @Test
    public void batchTestQuery() throws IOException {
        for (int i = 0; i < 10; i++) {
            testQuery2();
        }
    }

    @Test
    public void testQuery2() {
        log.info("sequence start :{}", sequence);

        //c.apple.news
        String queryName = "baidu.com";

        Packet packet = QuestionNameEncoder.buildPacket(queryName, sequence++, (short) MessageType.A.getType());

        SocketAddress remoteAddress = new InetSocketAddress(444);

        Request request = new Request(remoteAddress, packet);
        UpstreamServer.getInstance().sendRequest(request);


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("sequence end :{}", sequence);
    }
//
//    private Packet buildPacket(String queryName) {
//        //5, 98, 97, 105, 100, 117, 3, 99, 111, 109, //queryName baidu.com
//        //1,99,5,97,112,112,108,101,4,110,101,119,115,  //queryName c.apple.news
//
//        //12(header组成)= 2(Transaction ID)+2(Flags)+2(Questions)+2(Answer RRs)+2(Authority RRs)+2(Additional RRs)
//        byte[] message = {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, //header ,length=12
//                5, 98, 97, 105, 100, 117, 3, 99, 111, 109,  //queryName
//                0,    //queryName end
//                0, 1, //queryType
//                0, 1 //queryClass
//        };
//
//        byte[] queryNameBytes = SimpleMessageEncoder.encodeName(queryName);
//        Packet packet = Packet.create(1024);
//        //-------------Header区，12个字节-------------------------
//        // 会话标识 transactionId
//        packet.addShort((short) ((sequence++) & 0xffff));
//        // 标志
//        packet.addShort((short) 0x0100);
//        // 数量
//        packet.addShort((short) 0x01)    //Questions
//                .addShort((short) 0x00)  //Answer RRs
//                .addShort((short) 0x00)  //Authority RRs
//                .addShort((short) 0x00); //Additional RRs
//
//        //-------------Queries区，字节数不固定-------------------------
//        packet.addBytes(queryNameBytes);
//        packet.addByte((byte)0x00);
//        packet.addShort((short) Message.TYPE_A);    //TYPE:A
//        packet.addShort((short) Message.CLASS_IN);  //Class:IN
//        return packet;
//    }

}
