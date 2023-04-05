package com.lzc.dns.protocol.util;

import com.lzc.dns.protocol.entity.Question;
import com.lzc.dns.util.Packet;

import java.util.ArrayList;

public class QuestionNameUtil {

    public static Question decode(Packet packet, int questionSize) {
        if (questionSize > 1) {
            throw new RuntimeException("multiple question name resolve unsupported");
        }

        int len;
        ArrayList<Question> questions = new ArrayList<>(2);
        for (int i = 0; i < questionSize; i++) {
            StringBuilder name = new StringBuilder(64);
            while ((len = packet.nextByte() & 0xff) > 0) {
                name.append(new String(packet.nextBytes(len)));
                name.append('.');
            }
            if (name.length() > 0 && name.charAt(name.length() - 1) == '.') {
                name.deleteCharAt(name.length() - 1);
            }
            int queryType = packet.nextShort() & 0xffff;
            int queryClass = packet.nextShort() & 0xffff;
            questions.add(new Question(name.toString(), queryType, queryClass));
        }

        // 依次处理，一般来说，都是单个查询的吧，只有自己写程序才有可能会有批量查询的情况
        Question question = questions.get(0);
        return question;
    }


    public static byte[] encode(String questionName) {
        //Name:<Root> OR Name:baidu.com
        if (questionName.isEmpty()) {
            return new byte[0];
        }

        byte[] nameBytes = new byte[questionName.length() + 1];
        int lastHeadIndex = 0, s = 0;
        for (int i = 0, k = 1; i < questionName.length(); i++, k++) {
            char chr = questionName.charAt(i);
            if (chr == '.') {
                nameBytes[lastHeadIndex] = (byte) s;
                s = 0;
                lastHeadIndex = i + 1;
                continue;
            }
            nameBytes[k] = (byte) chr;
            s += 1;
        }
        nameBytes[lastHeadIndex] = (byte) s;
        return nameBytes;
    }
}
