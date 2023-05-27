package com.lzc.dns.web.configuration;

import com.lzc.dns.web.interceptor.CommonInterceptor;
import com.lzc.dns.web.interceptor.FeUserInterceptor;
import com.lzc.dns.web.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by matrixy on 2017/12/13.
 */
@Configuration
public class AppConfiguration extends WebMvcConfigurerAdapter {

    public ConcurrentHashMap<String, Object> sessionDb() {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        return map;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CommonInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new CommonInterceptor()).addPathPatterns("/manage/stat/**");

        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/manage/**")
                .excludePathPatterns("/fe/manage/stat/**")
                .addPathPatterns("/user/logout");


        registry.addInterceptor(new FeUserInterceptor()).addPathPatterns("/fe/manage/**");

        super.addInterceptors(registry);
    }
}
