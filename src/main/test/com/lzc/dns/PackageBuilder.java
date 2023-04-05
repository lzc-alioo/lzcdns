package com.lzc.dns;

import com.lzc.dns.util.Packet;

public class PackageBuilder {

    public static Packet build(byte[] message) {
        Packet packet = Packet.create(message);
        return packet;
    }

    public static Packet build(byte[] message, short sequence) {
        Packet packet = build(message);
        packet.setShort(0, sequence++);
        return packet;
    }


}
