package com.lzc.dns.protocol.encode.answer;

import com.lzc.dns.protocol.entity.ResourceRecord;
import com.lzc.dns.util.Packet;

public interface AnswerEncode {

    int processType();

    void encode(Packet packet, ResourceRecord answer);


}
