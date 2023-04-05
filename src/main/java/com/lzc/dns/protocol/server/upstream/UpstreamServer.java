package com.lzc.dns.protocol.server.upstream;

import com.lzc.dns.protocol.entity.OriginalRequest;
import com.lzc.dns.protocol.entity.RecursiveResponse;
import com.lzc.dns.protocol.entity.Request;
import com.lzc.dns.util.ByteUtils;
import com.lzc.dns.util.Packet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by matrixy on 2019/4/19.
 */
@Slf4j
public class UpstreamServer extends Thread {
    Map<Short, OriginalRequest> transactionMap = null;
    public short sequence = 1;

    private DatagramChannel datagramChannel = null;
    private UpstreamSender upstreamSender = null;
    private UpstreamResolver upstreamResolver = null;


    public UpstreamServer() {
        this.setName("recursive-resolver-thread");
        transactionMap = new ConcurrentHashMap<>(65535);

        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);

            upstreamSender = new UpstreamSender(this, datagramChannel);
            upstreamResolver = new UpstreamResolver();
            log.info("Upstream NameServer started at " + upstreamSender.getUpstreamNameServer());
        } catch (IOException e) {
            log.error("Upstream NameServer error...", e);
        }

    }

    public void run() {
        listen(datagramChannel);
    }

    private void listen(DatagramChannel datagramChannel) {
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

    private boolean resolve(byte[] message) {
        short sequence = (short) ByteUtils.getShort(message, 0, 2);
        OriginalRequest originalRequest = getUpstreamRequest(sequence);
        if (originalRequest == null) {
            log.info("dns_query_upstream no original request found for: " + sequence);
            return false;
        }
        upstreamResolver.resolveRequest(new RecursiveResponse(originalRequest.getSequence(), originalRequest.getRemoteAddress(),
                originalRequest.getStartTime(), originalRequest.getTraceId(), message));
        return true;
    }

    public void sendRequest(Request request) {
        Packet packet = request.packet;
        short newSequence = generateSequence();
        short originSequence = packet.seek(0).nextShort();

        //补充序列号
        request.setNewSequence(newSequence);
        request.setOriginSequence(originSequence);

        upstreamSender.sendRequest(request);
    }

    protected void saveUpstreamRequest(short sequence, OriginalRequest request) {
        transactionMap.put(sequence, request);
    }

    protected OriginalRequest getUpstreamRequest(short sequence) {
        return transactionMap.remove(sequence);
    }

    static UpstreamServer instance = null;

    public void init() {
        instance.start();
    }

    public static synchronized UpstreamServer getInstance() {
        if (null == instance) {
            instance = new UpstreamServer();
        }
        return instance;
    }

    public synchronized short generateSequence() {
        return sequence++;
    }
}
