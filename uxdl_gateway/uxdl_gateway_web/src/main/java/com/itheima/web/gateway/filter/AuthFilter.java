package com.itheima.web.gateway.filter;


import com.itheima.web.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 客户端网关全局过滤器
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private AuthService authService;

    private static final String LOGIN_URL = "http://localhost:8001/page/oauth/toLogin";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //1、判断是否登录请求，是则放行
        String path = request.getURI().getPath();
        if ("/oauth/login".equals(path) || !URLFilter.hasAuthorize(path) || "/oauth/login/phone".equals(path)){
            return chain.filter(exchange);
        }
        //2、不是登录请求，获取cookie信息
        String jti = authService.getJtiFromCookie(request);
        //3、判断jti是否存在
        if (StringUtils.isEmpty(jti)) {

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
           /* //页面跳转
            return this.toLoginPage(LOGIN_URL+"?FROM="+request.getURI().getPath(),exchange);*/
        }
        //4、jti存在，从redis中获取jwt
        String jwt = authService.getJwtFromRedis(jti);
        //5、判断jwt是否存在
        if (StringUtils.isEmpty(jwt)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
            /*return this.toLoginPage(LOGIN_URL+"?FROM="+request.getURI().getPath(),exchange);*/
        }
        //6、jwt存在，对请求进行增强,设置头信息Authorization，值为Bearer空格 +jwt令牌，注意空格
        request.mutate().header("Authorization","Bearer "+jwt);

        return chain.filter(exchange);
    }

    /**
     * 页面跳转
     * @param loginUrl
     * @param exchange
     * @return
     */

    private Mono<Void> toLoginPage(String loginUrl, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location",loginUrl);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
