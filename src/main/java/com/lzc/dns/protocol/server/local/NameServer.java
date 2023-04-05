package com.lzc.dns.protocol.server.local;

import com.lzc.dns.protocol.entity.Request;
import com.lzc.dns.protocol.entity.Response;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Created by matrixy on 2019/4/19.
 */
@Slf4j
public class NameServer extends Thread {

    DatagramChannel datagramChannel = null;
    private NameResolver nameResolver = null;
    private NameSender nameSender = null;

    public NameServer() {
        this.setName("name-server-thread");
        try {
            datagramChannel = initDatagramChannel();

            nameSender = new NameSender(datagramChannel);
            nameResolver = new NameResolver(this);

        } catch (IOException e) {
            log.error("NameServer start error ...", e);
            System.exit(1);
        }
    }

    public void run() {
        try {
            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (!this.isInterrupted()) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    if (selectionKey.isReadable()) {
                        buffer.clear();
                        SocketAddress remoteAddress = datagramChannel.receive(buffer);
                        buffer.flip();
                        byte[] message = new byte[buffer.limit()];
                        buffer.get(message, 0, message.length);

                        nameResolver.resolveRequest(new Request(remoteAddress, Packet.create(message)));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("NameServer receive error", ex);
        } finally {
            try {
                datagramChannel.close();
            } catch (Exception e) {
            }
            log.info("NameServer app exited...");
            System.exit(1);
        }
    }

    public boolean sendResponse(Response response) {
        return nameSender.sendResponse(response);
    }


    private DatagramChannel initDatagramChannel() throws IOException {
        String binIp = Configs.get("dns.server.addr", "0.0.0.0");
        int port = Configs.getInt("dns.server.port", 53);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(binIp, port);

        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.socket().bind(inetSocketAddress);
        datagramChannel.configureBlocking(false);
        log.info("NameServer started at {}:{}", binIp, port);

        return datagramChannel;
    }

    static NameServer instance = null;

    public void init() {
        instance.start();
    }

    public static synchronized NameServer getInstance() {
        if (null == instance) {
            instance = new NameServer();
        }
        return instance;
    }
}
