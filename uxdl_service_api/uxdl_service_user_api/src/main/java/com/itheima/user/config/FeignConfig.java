package com.itheima.user.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 给请求添加令牌
 */
public class FeignConfig implements RequestInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if (null != request.getCookies() && !"".equals(request.getCookies()) ) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if ("uid".equals(cookie.getName())){
                    String jwt = stringRedisTemplate.boundValueOps(cookie.getValue()).get();
                    requestTemplate.header("Authorization","Bearer "+jwt);
                }
            }
        }
    }
}
