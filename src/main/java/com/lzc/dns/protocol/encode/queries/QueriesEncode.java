package com.lzc.dns.protocol.encode.queries;

import com.lzc.dns.util.Packet;

public interface QueriesEncode {

    void encode(Packet packet, String questionName, int questionType);

}
