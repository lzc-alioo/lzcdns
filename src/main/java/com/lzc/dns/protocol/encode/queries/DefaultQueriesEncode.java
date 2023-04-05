package com.lzc.dns.protocol.encode.queries;

import com.lzc.dns.protocol.entity.Message;
import com.lzc.dns.protocol.util.QuestionNameUtil;
import com.lzc.dns.util.Packet;
import org.springframework.stereotype.Component;

@Component
public class DefaultQueriesEncode implements QueriesEncode {

    public void encode(Packet packet, String questionName, int questionType) {
        //-------------Queries区，字节数不固定-------------------------

        packet.addBytes(QuestionNameUtil.encode(questionName));
        packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
        packet.addShort((short) questionType);   //TYPE:TXT
        packet.addShort((short) Message.CLASS_IN);  //Class:IN

    }


}
