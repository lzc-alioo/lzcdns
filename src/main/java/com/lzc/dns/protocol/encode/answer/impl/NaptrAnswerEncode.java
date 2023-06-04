package com.lzc.dns.protocol.encode.answer.impl;

import com.lzc.dns.protocol.encode.answer.AnswerEncode;
import com.lzc.dns.protocol.entity.NaptrResourceRecord;
import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.protocol.util.QuestionNameUtil;
import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class NaptrAnswerEncode implements AnswerEncode {

    @Override
    public int processType() {
        return MessageType.NAPTR.getType();
    }

    public void encode(Packet packet, ResourceRecord answer) {
        //Data

        NaptrResourceRecord record = (NaptrResourceRecord) answer;

        //Data
        packet.addShort((short) (2 + 2
                + 1 + record.getFlags().getBytes(StandardCharsets.UTF_8).length
                + 1 + record.getService().getBytes(StandardCharsets.UTF_8).length
                + 1 + record.getRegex().getBytes(StandardCharsets.UTF_8).length
                + 1 + QuestionNameUtil.encode(record.getReplacement()).length
        ));

        packet.addShort(record.getOrder());
        packet.addShort(record.getPreference());
        packet.addByte(record.getFlagLength());
        packet.addBytes(record.getFlags().getBytes(StandardCharsets.UTF_8));
        packet.addByte(record.getServiceLength());
        packet.addBytes(record.getService().getBytes(StandardCharsets.UTF_8));
        packet.addByte(record.getRegexLength());
        packet.addBytes(record.getRegex().getBytes(StandardCharsets.UTF_8));
        //packet.addByte(record.getReplacementLength());
        packet.addBytes(QuestionNameUtil.encode(record.getReplacement()));
        packet.addByte((byte) 0x00);

    }

}
