package com.lzc.dns.protocol.server.local;

import com.lzc.dns.manager.CacheManager;
import com.lzc.dns.manager.RuleManager;
import com.lzc.dns.manager.StatManager;
import com.lzc.dns.protocol.decode.Decoder;
import com.lzc.dns.protocol.encode.Encoder;
import com.lzc.dns.protocol.entity.*;
import com.lzc.dns.protocol.enums.MessageType;
import com.lzc.dns.protocol.enums.QueryResultType;
import com.lzc.dns.protocol.server.upstream.UpstreamServer;
import com.lzc.dns.util.ByteUtils;
import com.lzc.dns.util.DateTimeUtil;
import com.lzc.dns.util.IPUtils;
import com.lzc.dns.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by matrixy on 2019/4/19.
 */
public class NameResolveTask extends Thread {
    static Logger logger = LoggerFactory.getLogger(NameResolveTask.class);

    private NameServer nameServer;
    private Request request;

    public NameResolveTask(NameServer nameServer, Request request) {
        this.nameServer = nameServer;
        this.request = request;
    }

    public void run() {
        try {
            if (request == null) {
                return;
            }
            resolve();
        } catch (Exception e) {
            logger.error("domain name resolve error", e);
        }

    }

    private void resolve() throws Exception {
        // 消息包解码
        Message message = Decoder.decodeMessage(request.packet);
        if (message.isQuestion() == false) {
            logger.info("query_fail by not question, question: " + ByteUtils.toString(request.packet.nextBytes()));
            return;
        }

        // 遍历每一个要查询的域名
        request.packet.seek(12);
        Question question = Decoder.decodeQuestion(request.packet, message.questions);

        CacheManager cacheManager = CacheManager.getInstance();
        int now = DateTimeUtil.getDateTime8Integer();

        logger.info("dns_query_start from = {}, name = {}, type = {}", request.remoteAddress.toString(), question.getName(), question.getTypeString());
        logger.info("message decode:{}", message.toString());

        long remoteAddress = ByteUtils.getLong(((InetSocketAddress) request.remoteAddress).getAddress().getAddress(), 0, 4);

        // dns服务器，日志打点
        StatManager.getInstance().log(remoteAddress, now, question.getName());

        //第一步，查询本地配置规则
        if(question.getType()==MessageType.A.getType()){
            String answer = RuleManager.getInstance().matches(now, remoteAddress, question.getName());
            if (answer != null) {
                // 返回结果
                int ttl = 180;
                ResourceRecord[] records = new ResourceRecord[]{new ResourceRecord(question.getName(), MessageType.A.getType(), 01, ttl, IPUtils.toInteger(answer))};
                Encoder encoder = SpringUtils.getBean(Encoder.class);
                byte[] resp = encoder.encode((short) (message.transactionId & 0xffff), question.getName(), MessageType.A.getType(), records, ttl);
//            byte[] resp = SimpleMessageEncoder.encode((short) (message.transactionId & 0xffff), question.getName(), Message.TYPE_A, records, ttl);
                this.nameServer.sendResponse(new Response(request.remoteAddress, resp, question.getName(), question.getType(), request.startTime, QueryResultType.RULE.getType(), records, ttl));
                return;
            }
        }

        //第二步，查询缓存
        CachedItem<ResourceRecord[]> cachedItem = cacheManager.get(question.getName() + ":" + question.getType());
        if (cachedItem != null) {
            int ttl = cachedItem.getTtl();
            ResourceRecord[] records = cachedItem.entity;
            Encoder encoder = SpringUtils.getBean(Encoder.class);
            byte[] resp = encoder.encode((short) (message.transactionId & 0xffff), question.getName(), question.getType(), records, ttl);
//            byte[] resp = SimpleMessageEncoder.encode((short) (message.transactionId & 0xffff), question.getName(), question.getType(), records, ttl);
            this.nameServer.sendResponse(new Response(request.remoteAddress, resp, question.getName(), question.getType(), request.startTime, QueryResultType.CACHE.getType(), records, ttl));
            return;
        }

        //第三步，交给递归解析线程去上游服务器解析
        UpstreamServer.getInstance().sendRequest(request);
        StatManager.getInstance().addQueryUpstreamCount();
    }

}
