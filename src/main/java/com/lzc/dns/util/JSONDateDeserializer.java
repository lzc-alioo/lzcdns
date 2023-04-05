package com.lzc.dns.util;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/***
 * 时间格式序列化
 */
public class JSONDateDeserializer implements ObjectDeserializer {

    @Override
    public String deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
        JSONLexer lexer = defaultJSONParser.getLexer();
        String timestamp = lexer.stringVal();
        Long longObject = Long.valueOf(timestamp);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime time = Instant.ofEpochMilli((Long) longObject).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return time.format(dateTimeFormatter);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}