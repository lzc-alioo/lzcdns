package com.lzc.dns.protocol.encode.answer.impl;

import com.lzc.dns.protocol.encode.answer.AnswerEncode;
import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.protocol.util.QuestionNameUtil;
import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;

@Component
public class PtrAnswerAnswerEncode implements AnswerEncode {

    @Override
    public int processType() {
        return MessageType.PTR.getType();
    }

    public void encode(Packet packet, ResourceRecord answer) {

        //Data
        byte[] data = QuestionNameUtil.encode(answer.getNameServer());
        packet.addShort((short) (data.length + 1));
        packet.addBytes(data);
        packet.addByte((byte) 0x00);

    }

}
