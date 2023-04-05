package com.lzc.dns.protocol.entity;

import com.lzc.dns.protocol.util.MessageTypeUtil;
import lombok.Data;

/**
 * Created by matrixy on 2019/4/19.
 */
@Data
public class Question {
    private String name;

    /**
     * @see com.lzc.dns.protocol.enums.MessageType
     */
    private int type;
    /**
     * IN	1	互联网
     * CS	2	CSNET类（被废弃）
     * CH	3	CHAOS类（混沌系统）
     * HS	4	Hesiod（很少使用）
     */
    private int clazz;

    public Question(String name, int type, int clazz) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
    }

    public String getTypeString() {
//        String ret = "";
//        switch (type) {
//            case Message.TYPE_A:
//                ret = "A(1)";
//                break;
//            case Message.TYPE_AAAA:
//                ret = "AAAA(28)";
//                break;
//            case Message.TYPE_NS:
//                ret = "NS(2)";
//                break;
//            case Message.TYPE_CNAME:
//                ret = "CNAME(5)";
//                break;
//            case Message.TYPE_MX:
//                ret = "MX(15)";
//                break;
//            case Message.TYPE_HTTPS:
//                ret = "HTTPS(65)";
//                break;
//            case Message.TYPE_TXT:
//                ret = "TXT(16)";
//                break;
//            default:
//                ret = "(" + type + ")";
//                break;
//        }
//        return ret;
        return MessageTypeUtil.getTypeString(this.type);
    }

    @Override
    public String toString() {
        return "Question{" + "name='" + name + '\'' + ", type=" + getTypeString() + ", clazz=" + clazz + '}';
    }
}
