package com.lzc.dns.protocol.util;

import com.lzc.dns.protocol.enums.MessageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageTypeUtil {
    private static Map<Integer, MessageType> map = new HashMap<>();

    static {
        Arrays.stream(MessageType.values()).forEach(messageType -> {
            map.put(messageType.getType(), messageType);
        });
    }

    public static MessageType getType(int type) {
        return map.get(type) != null ? map.get(type) : MessageType.UNKNOWN;
    }

    public static String getTypeString(int type) {
        return Optional.ofNullable(map.get(type)).map(MessageType::toString).orElse("unknown(" + type + ")");
    }
}
