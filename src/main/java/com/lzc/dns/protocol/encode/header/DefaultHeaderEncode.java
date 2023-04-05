package com.lzc.dns.protocol.encode.header;

import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;

@Component
public class DefaultHeaderEncode implements HeaderEncode {

    public void encode(Packet packet, short sequence, int answerLength) {
        //-------------Header区，12个字节-------------------------
        // 会话标识 transactionId
        packet.addShort((short) (sequence & 0xffff));
        // 标志，始终为成功应答 flags
        packet.addShort((short) 0x8000);
        // 数量
        packet.addShort((short) 0x01)  //Questions
                .addShort((short) answerLength)  //Answer RRs
                .addShort((short) 0x00)  //Authority RRs
                .addShort((short) 0x00); //Additional RRs


    }


}
