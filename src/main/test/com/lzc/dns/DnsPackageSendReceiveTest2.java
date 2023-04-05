package com.lzc.dns;

import com.lzc.dns.util.ByteUtils;
import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

@Slf4j
public class DnsPackageSendReceiveTest2 {
    short sequence = 10;

    public static DatagramChannel datagramChannel = null;


    public static void main(String[] args) {
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);

            DatagramChannel finalDatagramChannel = datagramChannel;
            new Thread(() -> listen(finalDatagramChannel)).start();

            new DnsPackageSendReceiveTest2().testQuery2(datagramChannel);

            Thread.sleep(5000L);
        } catch (Exception e) {
            log.error("Upstream NameServer error...", e);
        }
    }

    private static void listen(DatagramChannel datagramChannel) {
        try {
            Selector selector = Selector.open();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            datagramChannel.register(selector, SelectionKey.OP_READ);
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        buffer.clear();
                        datagramChannel.receive(buffer);
                        buffer.flip();
                        //buffer -> message
                        byte[] message = new byte[buffer.limit()];
                        buffer.get(message, 0, message.length);

                        resolve(message);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("nameserver receive error", ex);
        } finally {
            try {
                datagramChannel.close();
            } catch (Exception e) {
            }
            log.info("NameServer app exited...");
            System.exit(1);
        }
    }

    private static boolean resolve(byte[] message) {
        short sequence = (short) ByteUtils.getShort(message, 0, 2);
        log.info("sequence:{},message:{}", sequence, message);

        return true;
    }

    @Test
    public void testQuery2(DatagramChannel datagramChannel) {
        log.info("sequence start :{}", sequence);

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


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("sequence end :{}", sequence);
    }


}
