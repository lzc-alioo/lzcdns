package com.lzc.dns.util;


import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JSONSerializer implements ObjectSerializer {
    @Override
    public void write(com.alibaba.fastjson.serializer.JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        Long longObject = (Long) o;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime time = Instant.ofEpochMilli((Long) longObject).atZone(ZoneId.systemDefault()).toLocalDateTime();
        jsonSerializer.write(time.format(dateTimeFormatter));
    }
}
