package com.lzc.dns.protocol.encode.answer.impl;

import com.lzc.dns.protocol.encode.answer.AnswerEncode;
import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TxTAnswerAnswerEncode implements AnswerEncode {

    @Override
    public int processType() {
        return MessageType.TXT.getType();
    }

    public void encode(Packet packet, ResourceRecord answer) {
        //Data
        byte[] data = answer.getNameServer().getBytes(StandardCharsets.UTF_8);
        packet.addShort((short) (data.length + 1)); //data length
        packet.addByte((byte) (data.length));  //txt length
        packet.addBytes(data);

    }

}
