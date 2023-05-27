package com.lzc.dns.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzc.dns.web.SessionManager;
import com.lzc.dns.web.entity.User;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matrixy on 2017/8/26.
 */
public class FeUserInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取登陆的用户身份
//        User user = (User) request.getSession().getAttribute("loginUser");
        String token = request.getHeader("token");
        if (ObjectUtils.isEmpty(token)) {
            tokenDenied((response));
            return false;
        }
        User user = SessionManager.getSession(token);
        if (null == user) {
            tokenDenied((response));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // do nothing here...
    }


    public static void tokenDenied(HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, String> map = new HashMap<>();
        map.put("message", "login failed！");

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), JSON.toJSONString(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
