package com.lzc.dns.web;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static ConcurrentHashMap<String, Object> sessionDb = new ConcurrentHashMap<>();

    public static boolean addSession(String token, Object obj) {
        sessionDb.put(token, obj);
        return true;
    }

    public static <T> T getSession(String token) {
        return (T) sessionDb.get(token);
    }


}
