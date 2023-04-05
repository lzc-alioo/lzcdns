package com.lzc.dns.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by Expect on 2018/1/25.
 */
public final class SpringUtils {
    private static BeanFactory beanFactory;

    public static void init(ApplicationContext context) {
        SpringUtils.beanFactory = context;
    }

    public static <T> T getBean(Class serviceClass) {
        return (T) beanFactory.getBean(serviceClass);
    }

    public static void destroy(Object bean) {
        // ...
    }
}
