package com.lzc.dns.protocol.server.upstream;

import com.alibaba.fastjson.JSON;
import com.lzc.dns.manager.CacheManager;
import com.lzc.dns.protocol.decode.Decoder;
import com.lzc.dns.protocol.encode.Encoder;
import com.lzc.dns.protocol.entity.*;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.protocol.enums.QueryResultType;
import com.lzc.dns.protocol.server.local.NameServer;
import com.lzc.dns.util.ByteUtils;
import com.lzc.dns.util.Configs;
import com.lzc.dns.util.Packet;
import com.lzc.dns.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matrixy on 2019/4/19.
 */
@Slf4j
public class UpstreamResolveTask extends Thread {
    private RecursiveResponse recursiveResponse;

    public UpstreamResolveTask(RecursiveResponse recursiveResponse) {
        this.recursiveResponse = recursiveResponse;
    }

    private void resolve() throws Exception {
        if (recursiveResponse == null) {
            return;
        }

        // 消息包解码
        Packet packet = Packet.create(recursiveResponse.getPacket());

        //debug data
        //packet = Packet.create(Base64.decodeBase64("AAOBgAABAAQAAAAAAWMFYXBwbGUEbmV3cwAAQQABwAwABQABAAAJJAAYA2Nkbg5pY2xvdWQtY29udGVudANjb20AwCoABQABAAAJJQAfEmNkbi 1pY2xvdWQtY29udGVudAFnB2FhcGxpbWfAPcBOAAUAAQAAACIAGgFjBWFwcGxlBG5ld3MHZWRnZWtleQNuZXQAwHkABQABAAAJJwAZBmUxMjkxOQRkc2NkCmFrYW1haWVkZ2XAjg=="));

        Message msg = Decoder.decodeMessage(packet);
        if (msg.isQuestion() == true) {
            log.error("skip current question: " + ByteUtils.toString(packet.nextBytes()));
            return;
        }

        // 遍历每一个要查询的域名
        packet.seek(12);
        Question question = getQuestion(packet, msg);
        if (question == null) {
            return;
        }

        //这个地方的处理非常巧妙，针对自身DNS暂时不识别的可以通过这个方式直接返回给原始请求方
        if (msg.answerRRs == 0 && msg.authorityRRs == 0) {
            log.error("need_check no_answer_for question:{}, msg:{}", question.toString(), msg);

            packet.setShort(0, recursiveResponse.getSequence());
            NameServer.getInstance().sendResponse(new Response(recursiveResponse.getRemoteAddress(), packet.getBytes(), question.getName(), question.getType(), recursiveResponse.getStartTime(), QueryResultType.UP_SERVER.getType(), null, 0));
            return;
        }

        List<ResourceRecord> records = new ArrayList();
        //默认值：100分钟
        int ttl = getTtl(packet, msg, question, records);

        if (records.size() > 0) {
            CacheManager.getInstance().put(question.getName() + ":" + question.getType(), records.toArray(new ResourceRecord[0]), System.currentTimeMillis() + ttl * 1000);
            // 此时的remoteAddress还是请求方的地址，而非上游服务器端的地址
            Encoder encoder = SpringUtils.getBean(Encoder.class);
            byte[] message = encoder.encode(recursiveResponse.getSequence(), question.getName(), question.getType(), records.toArray(new ResourceRecord[0]), ttl);
//            byte[] message = SimpleMessageEncoder.encode(recursiveResponse.getSequence(), question.getName(), question.getType(), records.toArray(new ResourceRecord[0]), ttl);
            NameServer.getInstance().sendResponse(new Response(recursiveResponse.getRemoteAddress(), message, question.getName(), question.getType(), recursiveResponse.getStartTime(), QueryResultType.UP_SERVER.getType(), records.toArray(new ResourceRecord[0]), ttl));

        } else {
            log.error("need_check records.size()=0 question:{}, msg:{}", question.toString(), msg);
            //进入到这里说明解析上层DNS服务器结果出现了问题，所以直接将上层DNS结果返回
            packet.setShort(0, recursiveResponse.getSequence());
            NameServer.getInstance().sendResponse(new Response(recursiveResponse.getRemoteAddress(), packet.getBytes(), question.getName(), question.getType(), recursiveResponse.getStartTime(), QueryResultType.UP_SERVER.getType(), null, 0));
            return;
        }

    }

    private int getTtl(Packet packet, Message msg, Question question, List<ResourceRecord> records) throws UnknownHostException {
        //注：此处丢弃了additionalRRS数据包
        //for (int i = 0, k = msg.answerRRs + msg.authorityRRs + msg.additionalRRs; i < k; i++) {
        for (int i = 0, k = msg.answerRRs + msg.authorityRRs; i < k; i++) {
            ResourceRecord resourceRecord = getResourceRecord(packet);
            if (resourceRecord != null) {
//            log.info("i:{},resourceRecord:{}", i, JSON.toJSONString(resourceRecord));
                log.info("i:{},resourceRecord:{}", i, resourceRecord.toString());
                records.add(resourceRecord);
            }
        }
        //ttl值决定了缓存的时间，在这里进行适当的放大，可以提高缓存的利用率
        int ttl = records.stream().max((o1, o2) -> o1.getTtl() - o2.getTtl()).map(ResourceRecord::getTtl).orElse(900);

        int expireTime = Configs.getInt("dns.cache.expire_time", 600);
        if (ttl < expireTime) {
            ttl = expireTime;
        }

        return ttl;
    }

    private Question getQuestion(Packet packet, Message msg) {
        int len = 0;
        ArrayList<Question> questions = new ArrayList(100);
        for (int i = 0; i < msg.questions; i++) {
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
//            logger.info("question: {}, type: {}", name.toString(), queryType);
        }

        if (questions.size() == 0) {
            log.error("no question block found...");
            return null;
        }

        Question question = questions.get(0);
        return question;
    }

    private ResourceRecord getResourceRecord(Packet packet) throws UnknownHostException {
        int minTtl = 300;

        //Answer Name
        String answerName = this.getAnswerName(packet);
        //Answer Type
        int type = packet.nextShort();

        //Answer Class 资源记录的类型，与问题部分中的查询类型值是一样的。
        short clazz = packet.nextShort();
        //Answer Time To Live(unit : second)
        int ttl = packet.nextInt();
        //Answer Data Length
        int len = packet.nextShort() & 0xffff; //len=4

        if (type == MessageType.A.getType()) {
            long ipV4 = packet.nextInt() & 0xffffffffL;
            minTtl = Math.min(ttl, minTtl);
            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, minTtl, ipV4);
            return resourceRecord;
        } else if (type == MessageType.AAAA.getType()) {
            byte[] ipV6 = packet.nextBytes(len);
            minTtl = Math.min(ttl, minTtl);
            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, minTtl, (Inet6Address) Inet6Address.getByAddress(ipV6));
            return resourceRecord;
        } else if (type == MessageType.NS.getType()) {
            String nameServer = this.getAnswerName(packet);

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, nameServer);
            return resourceRecord;
        } else if (type == MessageType.MX.getType()) {
            short preference = packet.nextShort();
            String mailExchange = this.getAnswerName(packet);

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, preference, mailExchange);
            return resourceRecord;
        } else if (type == MessageType.CNAME.getType()) {
            String nameServer = this.getAnswerName(packet);

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, nameServer);
            return resourceRecord;
        } else if (type == MessageType.HTTPS.getType()) {
            log.error("check_type_https");
            return null;
        } else if (type == MessageType.SOA.getType()) {
            String nameServer = this.getAnswerName(packet);

            String mailBox = this.getAnswerName(packet);
            int serialNumber = packet.nextInt();
            int refreshInterval = packet.nextInt();
            int retryInterval = packet.nextInt();
            int expireTime = packet.nextInt();
            int minimumTtl = packet.nextInt();

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, nameServer, mailBox, serialNumber, refreshInterval, retryInterval, expireTime, minimumTtl);
            return resourceRecord;
        } else if (type == MessageType.PTR.getType()) {
            String domainName = this.getAnswerName(packet);

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, domainName);
            return resourceRecord;
        } else if (type == MessageType.TXT.getType()) {
            String txt = getText(packet);

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, txt);
            return resourceRecord;
        } else if (type == MessageType.SRV.getType()) {
            short priority = packet.nextShort();
            short weight = packet.nextShort();
            short port = packet.nextShort();
            String target = this.getAnswerName(packet);

            ResourceRecord resourceRecord = new ResourceRecord(answerName, type, clazz, ttl, priority, weight, port, target);
            return resourceRecord;
        }

        return null;

    }


    private String getText(Packet packet) {
        int tmpLen = (int) packet.nextByte();
        byte[] dataBytes = packet.nextBytes(tmpLen);
        String txt = new String(dataBytes);

        return txt;
    }


    private String getAnswerName(Packet packet) {
        StringBuilder name = new StringBuilder(64);
        int tmpLen = 0;
        while ((tmpLen = packet.nextByte() & 0xff) > 0) {
            /**
             * 本应该显示域名的字段中频繁出现"0xC00C"，或者其他以“C0”开头的两字节。查协议得知原来这是DNS协议消息压缩技术，使用偏移指针代替重复的字符
             * 最开始的两个bit必须都为1，目的是为了避免混淆。后面的14bit表示字符串在整个DNS消息包中的偏移量。
             * 由于DNS应答包中的Answers段出现的域名往往在Queries中已经出现，因此后面只需使用其偏移量表示即可。显然，Queries中的域名出现的频率最高，
             * 而其中第一个出现的域名偏移量固定为12字节（00001100）
             * cloud.tencent.com/developer/article/1149875#:~:text=1%20%E5%A6%82%E6%9E%9C%E6%9C%80%E9%AB%98%E4%B8%A4%E4%BD%8D%E6%98%AF%2000%EF%BC%8C%E5%88%99%E8%A1%A8%E7%A4%BA%E4%B8%8A%E9%9D%A2%E7%AC%AC%E4%B8%80%E7%A7%8D%202%20%E5%A6%82%E6%9E%9C%E6%9C%80%E9%AB%98%E4%B8%A4%E4%BD%8D%E6%98%AF%2011%EF%BC%8C%E5%88%99%E8%A1%A8%E7%A4%BA%E8%BF%99%E6%98%AF%E4%B8%80%E4%B8%AA%E5%8E%8B%E7%BC%A9%E8%A1%A8%E7%A4%BA%E6%B3%95%E3%80%82%20%E8%BF%99%E4%B8%80%E4%B8%AA%E5%AD%97%E8%8A%82,%E5%8E%BB%E6%8E%89%E6%9C%80%E9%AB%98%E4%B8%A4%E4%BD%8D%20%E5%90%8E%E5%89%A9%E4%B8%8B%E7%9A%846%E4%BD%8D%EF%BC%8C%E4%BB%A5%E5%8F%8A%E6%8E%A5%E4%B8%8B%E6%9D%A5%E7%9A%84%208%20%E4%BD%8D%E6%80%BB%E5%85%B1%2014%20%E4%BD%8D%E9%95%BF%E7%9A%84%E6%95%B0%E6%8D%AE%EF%BC%8C%E6%8C%87%E5%90%91%20DNS%20%E6%95%B0%E6%8D%AE%E6%8A%A5%E6%96%87%E4%B8%AD%E7%9A%84%E6%9F%90%E4%B8%80%E6%AE%B5%E5%9F%9F%E5%90%8D%EF%BC%88%E4%B8%8D%E4%B8%80%E5%AE%9A%E6%98%AF%E5%AE%8C%E6%95%B4%E5%9F%9F%E5%90%8D%EF%BC%8C%E5%8F%82%E8%A7%81%E7%AC%AC%E4%B8%89%E7%A7%8D%EF%BC%89%EF%BC%8C%E5%8F%AF%E4%BB%A5%E7%AE%97%E6%98%AF%E6%8C%87%E9%92%88%E5%90%A7%E3%80%82
             */
            if (tmpLen == 0xC0) { //在这里还要进行2次判断下，存在2次压缩的可能性
                int tmpOffset2 = packet.nextByte() & 0xff;
                name.append(this.getCompressName(packet, tmpOffset2));
                break;
            } else {
                name.append(new String(packet.nextBytes(tmpLen)));
                name.append('.');
            }
        }
        if (name.length() > 0 && name.charAt(name.length() - 1) == '.') {
            name.deleteCharAt(name.length() - 1);
        }
        return name.toString();
    }

    private String getCompressName(Packet packet, int tmpOffset) {
        int nowOffset = packet.offset();
        packet.seek(tmpOffset);

        String name = getAnswerName(packet);

        packet.seek(nowOffset);
        return name;
    }


    public void run() {
        try {
            resolve();
        } catch (Exception e) {
            log.error("upstream server resolve domain name exception recursiveResponse:" + JSON.toJSONString(recursiveResponse), e);
        } catch (Throwable e) {
            List x = null;
            if (e.getStackTrace() != null) {
                x = Arrays.asList(e.getStackTrace());
                if (x.size() > 20) {
                    x = x.subList(0, 20);
                }
            }

            log.error("upstream server resolve domain name error recursiveResponse:{} , {}", JSON.toJSONString(recursiveResponse), x);
        }
    }
}
