package com.lzc.dns.protocol.encode.answer.impl;

import com.lzc.dns.protocol.encode.answer.AnswerEncode;
import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.protocol.util.QuestionNameUtil;
import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;

@Component
public class SrvAnswerAnswerEncode implements AnswerEncode {

    @Override
    public int processType() {
        return MessageType.SRV.getType();
    }

    public void encode(Packet packet, ResourceRecord answer) {
        //Data
//        byte[] data = QuestionNameUtil.encode(answer.getNameServer());
//        byte[] data2 = QuestionNameUtil.encode(answer.getMailBox());
//
//        packet.addShort((short) (data.length + 1 + data2.length + 1 + 4 * 5));
//        packet.addBytes(data);
//        packet.addByte((byte) 0x00);
//        packet.addBytes(data2);
//        packet.addByte((byte) 0x00);
//        packet.addInt(answer.getSerialNumber());
//        packet.addInt(answer.getRefreshInterval());
//        packet.addInt(answer.getRetryInterval());
//        packet.addInt(answer.getExpireTime());
//        packet.addInt(answer.getMinimumTtl());


        byte[] data = QuestionNameUtil.encode(answer.getTarget());
        int len = 2 + 2 + 2 + data.length + 1;

        packet.addShort((short) len);
        packet.addShort(answer.getPriority());
        packet.addShort(answer.getWeight());
        packet.addShort(answer.getPort());
        packet.addBytes(data);
        packet.addByte((byte) 0x00);
    }

}
