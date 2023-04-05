package com.lzc.dns.protocol.encode;

import com.lzc.dns.protocol.encode.answer.AnswerEncodeFactory;
import com.lzc.dns.protocol.encode.header.HeaderEncode;
import com.lzc.dns.protocol.encode.queries.QueriesEncode;
import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.util.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Encoder {

    @Autowired
    private HeaderEncode headerEncode;
    @Autowired
    private QueriesEncode queriesEncode;
    @Autowired
    private AnswerEncodeFactory answerEncodeFactory;

    public byte[] encode(short sequence, String questionName, int questionType, ResourceRecord[] answers, int ttl) {

        Packet packet = Packet.create(1024);
        //-------------Header区-------------------------
        headerEncode.encode(packet, sequence, answers.length);
        //-------------Queries区-------------------------
        queriesEncode.encode(packet, questionName, questionType);


        //-------------Answers区-------------------------
        for (int i = 0; i < answers.length; i++) {
            ResourceRecord answer = answers[i];

            //Name:<Root> OR Name Like :baidu.com
            if (questionName.isEmpty()) {
                packet.addByte((byte) 0x00);
            } else {
                // 始终指向查询区的名称，此处0xc00c, c0表示启用了压缩算法，0c表示定位到数据包12个字节之后开始读取
                packet.addShort((short) 0xc00c);
            }
            //Type
            packet.addShort((short) answer.getType());
            //Class
            packet.addShort((short) 0x01);
            //Time to live
            packet.addInt(ttl);

            //Data
            answerEncodeFactory.getAnswerEncode(answer.getType()).encode(packet, answer);
        }

        return packet.getBytes();
    }
}
