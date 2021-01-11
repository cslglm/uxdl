package com.itheima.web.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


     /** 从cookie中获取jti
     * @param request
     * @return*/


    public String getJtiFromCookie(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String jti = headers.getFirst("uid");
        if (jti != null && !jti.equals("")) {
            return jti;
        }
        HttpCookie cookie = request.getCookies().getFirst("uid");
        if (cookie !=null){
            jti = cookie.getValue();
            return jti;
        }
        return null;
    }


    /* * 从redis中获取jwt
     * @param jti
     * @return*/


    public String getJwtFromRedis(String jti) {
        String jwt = stringRedisTemplate.boundValueOps(jti).get();
        return jwt;
    }
}
