package com.lzc.dns.protocol.server.local;

import com.alibaba.fastjson.JSON;
import com.lzc.dns.manager.LogManager;
import com.lzc.dns.manager.StatManager;
import com.lzc.dns.protocol.entity.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@Slf4j
public class NameSendTask extends Thread {
    private DatagramChannel datagramChannel;
    private Response response;

    public NameSendTask(DatagramChannel datagramChannel, Response response) {
        this.datagramChannel = datagramChannel;
        this.response = response;
    }

    @Override
    public void run() {
        try {
            //发送UDP包，返回给请求方
            sendResponse(response);
            //监控打点
            StatManager.getInstance().addAnswerCount();
            //记录日志
            LogManager.log(response);
        } catch (Exception e) {
            log.error("send udp package error response:{}", JSON.toJSONString(response), e);
        }
    }


    private void sendResponse(Response response) throws IOException {
        if (response.getRemoteAddress() == null) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //buffer.clear();
        buffer.put(response.getPacket());
        buffer.flip();
        datagramChannel.send(buffer, response.getRemoteAddress());
    }


}
