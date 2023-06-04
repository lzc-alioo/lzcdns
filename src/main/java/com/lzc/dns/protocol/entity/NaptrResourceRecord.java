package com.lzc.dns.protocol.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by matrixy on 2019/4/23.
 */
@Getter
@Setter
public class NaptrResourceRecord extends ResourceRecord {

    private short order;                          //type=NAPTR专用
    private short preference;                     //type=NAPTR专用
    private byte flagLength;                     //type=NAPTR专用
    private String flags;                       //type=NAPTR专用
    private byte serviceLength;                  //type=NAPTR专用
    private String service;                     //type=NAPTR专用
    private byte regexLength;                    //type=NAPTR专用
    private String regex;                       //type=NAPTR专用
    private byte replacementLength;              //type=NAPTR专用
    private String replacement;                    //type=NAPTR专用

    public NaptrResourceRecord(String name, int type, int clazz, short order, short preference, byte flagLength, String flags, byte serviceLength, String service, byte regexLength, String regex, byte replacementLength, String replacement) {
        super(name, type, clazz);
        this.order = order;
        this.preference = preference;
        this.flagLength = flagLength;
        this.flags = flags;
        this.serviceLength = serviceLength;
        this.service = service;
        this.regexLength = regexLength;
        this.regex = regex;
        this.replacementLength = replacementLength;
        this.replacement = replacement;
    }


    public String toString() {
        return "{naptr: order:" + order + ",preference:" + preference
                + ",flagLength:" + flagLength + ",flags:" + flags + ",serviceLength:" + serviceLength + ",service:" + service
                + ",regexLength:" + regexLength + ",regex:" + regex + ",replacementLength:" + replacementLength + ",replacement:" + replacement
                + "}";
    }
}
