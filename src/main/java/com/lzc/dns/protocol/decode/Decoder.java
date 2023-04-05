package com.lzc.dns.protocol.decode;

import com.lzc.dns.protocol.entity.Message;
import com.lzc.dns.protocol.entity.Question;
import com.lzc.dns.protocol.util.QuestionNameUtil;
import com.lzc.dns.util.Packet;

/**
 * Created by matrixy on 2019/4/19.
 * 查询消息包解码器
 */
public final class Decoder {
    public static Message decodeMessage(Packet packet) {
        Message msg = new Message();
        msg.transactionId = packet.nextShort() & 0xffff;
        msg.flags = packet.nextShort() & 0xffff;
        msg.questions = packet.nextShort() & 0xffff;
        msg.answerRRs = packet.nextShort() & 0xffff;
        msg.authorityRRs = packet.nextShort() & 0xffff;
        msg.additionalRRs = packet.nextShort() & 0xffff;
        return msg;
    }

    public static Question decodeQuestion(Packet packet, int questionSize) {
        return QuestionNameUtil.decode(packet, questionSize);
    }


}
