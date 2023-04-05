package com.lzc.dns;

import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.function.Consumer;

@Slf4j
public class DnsUtil {

    public static void send(DatagramChannel datagramChannel, SocketAddress upstreamNameServer, Packet packet) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(packet.getBytes());
        buffer.flip();
        try {
            datagramChannel.send(buffer, upstreamNameServer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listen(DatagramChannel datagramChannel, Consumer<byte[]> consumer) {
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

                        //resolve(message);
                        consumer.accept(message);
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


}
