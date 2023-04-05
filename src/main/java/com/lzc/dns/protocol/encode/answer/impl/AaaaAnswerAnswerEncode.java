package com.lzc.dns.protocol.encode.answer.impl;

import com.lzc.dns.protocol.encode.answer.AnswerEncode;
import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;


@Component
public class AaaaAnswerAnswerEncode implements AnswerEncode {

    @Override
    public int processType() {
        return MessageType.AAAA.getType();
    }

    public void encode(Packet packet, ResourceRecord answer) {
        //Data
        packet.addShort((short) answer.dataLength);
        packet.addBytes(answer.ipv6.getAddress());
    }

}
