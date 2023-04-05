package com.lzc.dns.util.pool;

import java.util.UUID;

public class EagleEyeContext {
    private String traceId;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public static EagleEyeContext create() {
        String traceId = UUID.randomUUID().toString().replaceAll("\\-", "").substring(0, 32);
        return create(traceId);
    }

    public static EagleEyeContext create(String traceId) {
        EagleEyeContext eagleEyeContext = new EagleEyeContext();
        eagleEyeContext.setTraceId(traceId);
        return eagleEyeContext;
    }
}
