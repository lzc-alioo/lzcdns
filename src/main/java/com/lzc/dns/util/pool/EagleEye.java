package com.lzc.dns.util.pool;

import java.util.Optional;

public class EagleEye {
    private static ThreadLocal<EagleEyeContext> eagleEyeContextThreadLocal = new ThreadLocal<>();

    public static EagleEyeContext getEagleEyeContext() {
        return eagleEyeContextThreadLocal.get();
    }

    public static void set(EagleEyeContext eagleEyeContext) {
        eagleEyeContextThreadLocal.set(eagleEyeContext);
    }

    public static void clean() {
        eagleEyeContextThreadLocal.remove();
    }

    public static String getTraceId() {
        return Optional.ofNullable(EagleEye.getEagleEyeContext()).map(EagleEyeContext::getTraceId).orElse("");
    }
}
