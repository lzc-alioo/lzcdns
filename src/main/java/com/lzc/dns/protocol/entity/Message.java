package com.lzc.dns.protocol.entity;

/**
 * 原文链接：https://blog.csdn.net/weixin_45975575/article/details/115561913
 */
public class Message {

//    /**
//     * A	    1	IPv4主机地址
//     * NS	    2	权威名称服务器
//     * MD	    3	邮件目的地（被废弃，现用MX）
//     * MF	    4	邮件转发器（被废弃，现用MX）
//     * CNAME	5	别名
//     * SOA	    6	标识权威区域的开始
//     * MB	    7	邮箱域名（试验）
//     * MG	    8	邮件组成员（试验）
//     * MR	    9	邮件重新命名域名（试验）
//     * NULL	    10	空RR（试验）
//     * WKS	    11	总所周知的业务描述
//     * PTR	    12	把IP地址转换成域名
//     * HINFO	13	主机信息
//     * MINFO	14	邮箱或邮件列表信息
//     * MX	    15	邮件交换
//     * TXT	    16	文本字符串
//     * AAAA	    28	IPv6主机地址
//     * ADDI     41 “Additional records” 附加信息
//     * HTTPS    65 HTTPS
//     *
//     * @see com.lzc.dns.protocol.enums.MessageType
//     */
//    public static final int TYPE_A = 1;          // IPv4主机地址
//    public static final int TYPE_NS = 2;         // 权威名称服务器,用来指定该域名由哪个DNS服务器来进行解析
//    public static final int TYPE_CNAME = 5;      // 查询别名
//    public static final int TYPE_SOA = 6;        // 开始授权,起始授权机构记录，说明了在众多 NS 记录里哪一台才是主要的服务器
//    public static final int TYPE_WKS = 11;       // 熟知服务
//    public static final int TYPE_PTR = 12;       // 把IP地址转换成域名
//    public static final int TYPE_HINFO = 13;     // 主机信息
//    public static final int TYPE_MX = 15;        // 邮件交换
//    public static final int TYPE_TXT = 16;        // 文本字符串
//    public static final int TYPE_AAAA = 28;      // 由域名获得IPv6地址
//    public static final int TYPE_AXFR = 252;     // 传送整个区的请求
//    public static final int TYPE_ANY = 255;      // 对所有记录的请求
//    public static final int TYPE_HTTPS = 65;      // 对所有记录的请求
//    public static final int TYPE_SRV = 33;      // 对所有记录的请求

    /**
     * IN	1	互联网
     * CS	2	CSNET类（被废弃）
     * CH	3	CHAOS类（混沌系统）
     * HS	4	Hesiod（很少使用）
     */
    public static final int CLASS_IN = 1;          // 互联网


    /**
     * 会话标识 占用两个字节
     * 请求的ID号，应答数据包中的ID号会与请求中的ID号一一对应
     */
    public int transactionId;

    /**
     * 标志，占用两个字节,按位表示多个含义（目前只用到了前12个比特位，最后4个没有用到）
     * <p>
     * <p>
     * Flags: 0x0000 Standard query                        #报文中的标志字段
     * 0... .... .... .... = Response: Message is a query  #从右往左数，第16个bit位：Q/R:第一位表示请求(1)/应答(0);
     * .... 0... .... .... = Opcode: Standard query (0)    #从右往左数，第12个bit位：Opcode：操作码。其中，0 表示标准查询；1 表示反向查询；2 表示服务器状态请求;
     * .... .0.. .... .... = Truncated:                    #从右往左数，第11个bit位：AA（Authoritative）：授权应答。该字段在响应报文中有效。其中 1 表示名称服务器是权威服务器；0 表示不是权威服务器;
     * .... ..0. .... .... = Truncated:                    #从右往左数，第10个bit位：Truncated：是否被截断。其中 1 表示响应已超过 512 字节并已被截断，只返回前 512 个字节;
     * .... ...0 .... .... = Recursion desired:            #从右往左数，第9个bit位：RD（Recursion Desired）：期望递归;
     * .... ...0 .... .... = Recursion desired:            #从右往左数，第8个bit位：RA（Recursion Available）：可用递归。该字段只出现在响应报文中。当值为 1 时，表示服务器支持递归查询;
     * .... .... 0... .... = Z: reserved (0)               #保留字段, 值为0
     * .... .... ...0 .... = Non-authenticated data:       #未启用字段
     */
    public int flags;
    public int questions;               // 问题数
    public int answerRRs;               // 回答资源记录数
    public int authorityRRs;            // 权威名称服务器记录数
    public int additionalRRs;           // 附加资源记录数

    public Message() {

    }


    public boolean isQuestion() {
        return (flags & 0x8000) == 0;
    }

    /**
     * Opcode：操作码。其中，0 表示标准查询；1 表示反向查询；2 表示服务器状态请求。
     *
     * @return
     */
    public int getOpcode() {
        return (flags >> 11) & 0x0f;
    }

    /**
     * AA（Authoritative）：授权应答。该字段在响应报文中有效。其中 1 表示名称服务器是权威服务器；0 表示不是权威服务器。
     *
     * @return
     */
    public int getAA() {
        return (flags >> 10) & 0x01;
    }

    /**
     * TC（Truncated）：表示是否被截断。其中 1 表示响应已超过 512 字节并已被截断，只返回前 512 个字节。
     *
     * @return
     */
    public int getTC() {
        return (flags >> 9) & 0x01;
    }

    /**
     * RD（Recursion Desired）：期望递归。该字段能在一个查询中设置，并在响应中返回。该标志告诉名称服务器必须处理这个查询，这种方式被称为一个递归查询。
     * 如果该位为 0，且被请求的名称服务器没有一个授权回答，它将返回一个能解答该查询的其他名称服务器列表。这种方式被称为迭代查询。
     *
     * @return
     */
    public int getRD() {
        return (flags >> 8) & 0x01;
    }

    /**
     * RA（Recursion Available）：可用递归。该字段只出现在响应报文中。当值为 1 时，表示服务器支持递归查询。
     *
     * @return
     */
    public int getRA() {
        return (flags >> 7) & 0x01;
    }

    /**
     * Z：保留字段，在所有的请求和应答报文中，它的值必须为 0。
     *
     * @return
     */
    public int getZ() {
        return (flags >> 6) & 0x01;
    }


    /**
     * rcode（Reply code）：返回码字段，表示响应的差错状态。
     * 当值为 0 时，表示没有错误；
     * 当值为 1 时，表示报文格式错误（Format error），服务器不能理解请求的报文；
     * 当值为 2 时，表示域名服务器失败（Server failure），因为服务器的原因导致没办法处理这个请求；
     * 当值为 3 时，表示名字错误（Name Error），只有对授权域名解析服务器有意义，指出解析的域名不存在；
     * 当值为 4 时，表示查询类型不支持（Not Implemented），即域名服务器不支持查询类型；
     * 当值为 5 时，表示拒绝（Refused），一般是服务器由于设置的策略拒绝给出应答，如服务器不希望对某些请求者给出应答。
     *
     * @return
     */
    public int getRCode() {
        return flags & 0x0f;
    }

    @Override
    public String toString() {
        return "{" +
                "transactionId:" + transactionId +
                ", flags:" + formatFlag(flags) +
                ", questions:" + questions +
                ", answerRRs:" + answerRRs +
                ", authorityRRs:" + authorityRRs +
                ", additionalRRs:" + additionalRRs +
                '}';
    }

    private String formatFlag(int flags) {
        String flagStr = Integer.toBinaryString(flags);
        flagStr = leftPad(flagStr, 16, '0');

        String regex = "(.{4})";
        flagStr = flagStr.replaceAll(regex, "$1_");
        flagStr = flagStr.substring(0, flagStr.length() - 1);

        return flagStr;
    }

    private String leftPad(String originStr, int length, char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - originStr.length(); i++) {
            sb.append(c);
        }
        sb.append(originStr);
        return sb.toString();
    }


    public String parseFlags() {
        StringBuilder sb = new StringBuilder();
        sb.append("isQuestion").append(":").append(isQuestion()).append(",");
        sb.append("Opcode").append(":").append(getOpcode()).append(",");
        sb.append("AA").append(":").append(getAA()).append(",");
        sb.append("TC").append(":").append(getTC()).append(",");
        sb.append("RD").append(":").append(getRD()).append(",");
        sb.append("RA").append(":").append(getRA()).append(",");
        sb.append("RCode").append(":").append(getRCode());

        return sb.toString();
    }

}
