package com.lzc.dns.util.pool;

import java.util.UUID;

public class IdGenerator {
    public static String generateId(){
        String id = UUID.randomUUID().toString().replaceAll("\\-", "").substring(0, 32);
        return id;
    }
}
