package com.lzc.dns;

import com.lzc.dns.protocol.entity.Message;
import com.lzc.dns.util.Packet;
import com.lzc.dns.protocol.util.QuestionNameUtil;

/**
 * Created by matrixy on 2019/4/23.
 */
public final class QuestionNameEncoder {

    /**
     * 构造 DNS Query Data Package
     *
     * @param questionName
     * @param transactionId
     * @param type          @link cn.org.hentai.dns.protocol.entity.Message
     * @return
     */
    public static Packet buildPacket(String questionName, short transactionId, short type) {
        //example
        //5, 98, 97, 105, 100, 117, 3, 99, 111, 109, //questionName baidu.com
        //1,99,5,97,112,112,108,101,4,110,101,119,115,  //questionName c.apple.news

        //12(header组成)= 2(Transaction ID)+2(Flags)+2(Questions)+2(Answer RRs)+2(Authority RRs)+2(Additional RRs)
//        byte[] message = {0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, //header ,length=12
//                5, 98, 97, 105, 100, 117, 3, 99, 111, 109,  //questionName
//                0,    //questionName end
//                0, 1, //queryType
//                0, 1 //queryClass
//        };

        byte[] questionNameBytes = QuestionNameUtil.encode(questionName);
        Packet packet = Packet.create(1024);
        //-------------Header区，12个字节-------------------------
        // 会话标识 transactionId
        packet.addShort((short) ((transactionId) & 0xffff));
        // 标志
        packet.addShort((short) 0x0100);
        // 数量
        packet.addShort((short) 0x01)    //Questions
                .addShort((short) 0x00)  //Answer RRs
                .addShort((short) 0x00)  //Authority RRs
                .addShort((short) 0x00); //Additional RRs

        //-------------Queries区，字节数不固定-------------------------
        packet.addBytes(questionNameBytes);
        packet.addByte((byte) 0x00);
        packet.addShort(type);    //TYPE:A
        packet.addShort((short) Message.CLASS_IN);  //Class:IN
        return packet;
    }
}
