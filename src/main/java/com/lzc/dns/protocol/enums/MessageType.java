package com.lzc.dns.protocol.enums;

public enum MessageType {

    /**
     * A	    1	IPv4主机地址
     * NS	    2	权威名称服务器
     * MD	    3	邮件目的地（被废弃，现用MX）
     * MF	    4	邮件转发器（被废弃，现用MX）
     * CNAME	5	别名
     * SOA	    6	标识权威区域的开始
     * MB	    7	邮箱域名（试验）
     * MG	    8	邮件组成员（试验）
     * MR	    9	邮件重新命名域名（试验）
     * NULL	    10	空RR（试验）
     * WKS	    11	总所周知的业务描述
     * PTR	    12	把IP地址转换成域名
     * HINFO	13	主机信息
     * MINFO	14	邮箱或邮件列表信息
     * MX	    15	邮件交换
     * TXT	    16	文本字符串
     * AAAA	    28	IPv6主机地址
     * ADDI     41 “Additional records” 附加信息
     * HTTPS    65 HTTPS
     */

    A(1, "A", "IPv4主机地址"),
    NS(2, "NS", "权威名称服务器"),
    MD(3, "MD", "邮件目的地（被废弃，现用MX）"),
    MF(4, "MF", "邮件转发器（被废弃，现用MX）"),
    CNAME(5, "CNAME", "别名"),
    SOA(6, "SOA", "标识权威区域的开始"),
    MB(7, "MB", "邮箱域名（试验）"),
    MG(8, "MG", "邮件组成员（试验）"),
    MR(9, "MR", "邮件重新命名域名（试验）"),
    NULL(10, "NULL", "邮件重新命名域名（试验）"),
    WKS(11, "WKS", "总所周知的业务描述"),
    PTR(12, "PTR", "域名指针"),
    HINFO(13, "HINFO", "主机信息"),
    MINFO(14, "MINFO", "邮箱或邮件列表信息"),
    MX(15, "MX", "邮件交换"),
    TXT(16, "TXT", "文本字符串"),
    AAAA(28, "AAAA", "IPv6主机地址"),
    SRV(33, "SRV", "资源记录把服务名字映射为提供服务的服务器名字"),
    ADDR(41, "ADDR", "Additional records附加信息"),
    HTTPS(65, "HTTPS", "HTTPS"),
    UNKNOWN(9999, "UNKNOWN", "未知命令"),

    ;


    private int type;
    private String command;
    private String desc;

    MessageType(int type, String command, String desc) {
        this.type = type;
        this.command = command;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return command + "(" + type + ")";
    }

    public static MessageType getByType(int type) {
        for (MessageType messageType : MessageType.values()) {
            if (type == messageType.getType()) {
                return messageType;
            }
        }
        return MessageType.UNKNOWN;
    }
}
