package com.lzc.dns.protocol.encode.answer;

import com.lzc.dns.protocol.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AnswerEncodeFactory {

    @Autowired
    private List<AnswerEncode> list;

    private static Map<Integer, AnswerEncode> answerEncodeMap = new HashMap<>();

    @PostConstruct
    private void init() {

//        List<AnswerEncode> list = new ArrayList<>();
//        list.add(new TxTAnswerAnswerEncode());
//        list.add(new PtrAnswerAnswerEncode());
//        list.add(new SoaAnswerAnswerEncode());
//        list.add(new CNameAnswerAnswerEncode());
//        list.add(new MxAnswerAnswerEncode());
//        list.add(new NsAnswerAnswerEncode());
//        list.add(new AaaaAnswerAnswerEncode());
//        list.add(new AAnswerAnswerEncode());

        answerEncodeMap = list.stream().collect(Collectors.toMap(obj -> obj.processType(), obj -> obj, (k1, k2) -> k1));

        answerEncodeMap.forEach((key, val) -> {
            MessageType messageType = MessageType.getByType(key);
            log.info("加载处理器 CMD:{} PROCESS:{}", messageType.getCommand() + "(" + messageType.getType() + ")", val.getClass());
        });
    }


    public AnswerEncode getAnswerEncode(int type) {
        return answerEncodeMap.get(type);
    }


}
