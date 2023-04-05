package com.lzc.dns.protocol.encode.header;

import com.lzc.dns.util.Packet;

public interface HeaderEncode {

    void encode(Packet packet, short sequence, int answerLength);

}
