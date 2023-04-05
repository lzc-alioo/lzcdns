//package cn.org.hentai.dns.protocol.coder;
//
//import cn.org.hentai.dns.protocol.entity.Message;
//import cn.org.hentai.dns.protocol.entity.ResourceRecord;
//import cn.org.hentai.dns.util.Packet;
//
///**
// * Created by matrixy on 2019/4/23.
// */
//public final class SimpleMessageEncoder {
////    // 创建回应消息包
////    public static byte[] encode(Message message, String questionName, ResourceRecord[] answers, int ttl) {
////        return encode((short) (message.transactionId & 0xffff), questionName, Message.TYPE_A, answers, ttl);
////    }
////
////    // 创建回应消息包
////    public static byte[] encode(short sequence, String questionName, ResourceRecord[] answers, int ttl) {
////        return encode(sequence, questionName, Message.TYPE_A, answers, ttl);
////    }
//
//
//    public static byte[] encode(short sequence, String questionName, int questionType, ResourceRecord[] answers, int ttl) {
////        //增加一个切面，转发到新的代码处理流程当中
////        if (questionType == Message.TYPE_A || questionType == Message.TYPE_AAAA
////                || questionType == Message.TYPE_TXT || questionType == Message.TYPE_PTR
////                || questionType == Message.TYPE_SOA || questionType == Message.TYPE_CNAME
////                || questionType == Message.TYPE_MX || questionType == Message.TYPE_NS) {
////            return EncoderFactory.getInstance().getEncoder().encode(sequence, questionName, questionType, answers, ttl);
////        }
//
//        Packet packet = Packet.create(1024);
//
//        //-------------Header区，12个字节-------------------------
//        // 会话标识 transactionId
//        packet.addShort((short) (sequence & 0xffff));
//        // 标志，始终为成功应答 flags
//        packet.addShort((short) 0x8000);
//        // 数量
//        packet.addShort((short) 0x01)  //Questions
//                .addShort((short) answers.length)  //Answer RRs
//                .addShort((short) 0x00)  //Authority RRs
//                .addShort((short) 0x00); //Additional RRs
//
//
//        //-------------Queries区，字节数不固定-------------------------
//        // todo 这部分需要针对query questionType（A/AAAA/NS/MX/CNAME）来分别封装
////        if (questionType == Message.TYPE_A) {
////            // Queries区域，这里只处理单域名查询的情况
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);    //TYPE:A
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        } else if (questionType == Message.TYPE_AAAA) {
////            // Queries区域，这里只处理单域名查询的情况
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);   //TYPE:AAAA
////            packet.addShort((short) Message.CLASS_IN);    //Class:IN
////        } else if (questionType == Message.TYPE_NS) {
////            //Name:<Root> OR Name:baidu.com
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);
////            packet.addShort((short) Message.TYPE_NS);   //TYPE:NS
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        } else if (questionType == Message.TYPE_MX) {
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);   //TYPE:MX
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        } else if (questionType == Message.TYPE_CNAME) {
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);   //TYPE:MX
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        } else if (questionType == Message.TYPE_SOA) {
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);   //TYPE:MX
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        } else if (questionType == Message.TYPE_PTR) {
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);   //TYPE:MX
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        } else if (questionType == Message.TYPE_TXT) {
////            packet.addBytes(encodeName(questionName));
////            packet.addByte((byte) 0x00);   //前面 encodeName 并没有针对结尾补充0，这里手工补一下
////            packet.addShort((short) questionType);   //TYPE:TXT
////            packet.addShort((short) Message.CLASS_IN);  //Class:IN
////        }
////
////        //-------------Answers区，字节数不固定-------------------------
////        for (int i = 0; i < answers.length; i++) {
////            ResourceRecord answer = answers[i];
////
////            //Name:<Root> OR Name Like :baidu.com
////            if (questionName.isEmpty()) {
////                packet.addByte((byte) 0x00);
////            } else {
////                // 始终指向查询区的名称，此处0xc00c, c0表示启用了压缩算法，0c表示定位到数据包12个字节之后开始读取
////                packet.addShort((short) 0xc00c);
////            }
////            //Type
////            packet.addShort((short) answer.type);
////            //Class
////            packet.addShort((short) 0x01);
////            //Time to live
////            packet.addInt(ttl);
////
////
////            if (answer.type == Message.TYPE_A) {
////                packet.addShort((short) answer.dataLength);
////                packet.addInt((int) (answer.ipv4 & 0xffffffff));
////            } else if (answer.type == Message.TYPE_AAAA) {
////                packet.addShort((short) answer.dataLength);
////                packet.addBytes(answer.ipv6.getAddress());
////            } else if (answer.type == Message.TYPE_NS) {
////                byte[] data = encodeName(answer.getNameServer());
////                packet.addShort((short) (data.length + 1));
////                packet.addBytes(data);
////                packet.addByte((byte) 0x00);
////            } else if (answer.type == Message.TYPE_MX) {
////                byte[] data = encodeName(answer.getMailExchange());
////                packet.addShort((short) (2 + data.length + 1));
////                packet.addShort(answer.getPreference());
////                packet.addBytes(data);
////                packet.addByte((byte) 0x00);
////            } else if (answer.type == Message.TYPE_CNAME) {
////                byte[] data = encodeName(answer.getNameServer());
////                packet.addShort((short) (data.length + 1));
////                packet.addBytes(data);
////                packet.addByte((byte) 0x00);
////            } else if (answer.type == Message.TYPE_SOA) {
////
////                byte[] data = encodeName(answer.getNameServer());
////                byte[] data2 = encodeName(answer.getMailBox());
////
////                packet.addShort((short) (data.length + 1 + data2.length + 1 + 4 * 5));
////                packet.addBytes(data);
////                packet.addByte((byte) 0x00);
////                packet.addBytes(data2);
////                packet.addByte((byte) 0x00);
////                packet.addInt(answer.getSerialNumber());
////                packet.addInt(answer.getRefreshInterval());
////                packet.addInt(answer.getRetryInterval());
////                packet.addInt(answer.getExpireTime());
////                packet.addInt(answer.getMinimumTtl());
////            } else if (answer.type == Message.TYPE_PTR) {
////                byte[] data = encodeName(answer.getNameServer());
////                packet.addShort((short) (data.length + 1));
////                packet.addBytes(data);
////                packet.addByte((byte) 0x00);
////            } else if (answer.type == Message.TYPE_TXT) {
////                byte[] data = answer.getNameServer().getBytes(StandardCharsets.UTF_8);
////                packet.addShort((short) (data.length + 1)); //data length
////                packet.addByte((byte) (data.length));  //txt length
////                packet.addBytes(data);
////            } else {
////                packet.addBytes(answer.data);
////            }
//        }
//
//        return packet.getBytes();
//    }
////
////    public static byte[] encodeName(String questionName) {
////        //Name:<Root> OR Name:baidu.com
////        if (questionName.isEmpty()) {
////            return new byte[0];
////        }
////
////        byte[] nameBytes = new byte[questionName.length() + 1];
////        int lastHeadIndex = 0, s = 0;
////        for (int i = 0, k = 1; i < questionName.length(); i++, k++) {
////            char chr = questionName.charAt(i);
////            if (chr == '.') {
////                nameBytes[lastHeadIndex] = (byte) s;
////                s = 0;
////                lastHeadIndex = i + 1;
////                continue;
////            }
////            nameBytes[k] = (byte) chr;
////            s += 1;
////        }
////        nameBytes[lastHeadIndex] = (byte) s;
////        return nameBytes;
////    }
//
//}
