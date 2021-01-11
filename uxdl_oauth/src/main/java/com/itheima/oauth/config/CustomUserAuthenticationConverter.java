package com.itheima.oauth.config;


import com.itheima.oauth.util.UserJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        LinkedHashMap response = new LinkedHashMap();
        String name = authentication.getName();
        response.put("username", name);
        Map details = (Map) authentication.getDetails();
        String id = (String) details.get("id");
        String head_pic = (String) details.get("head_pic");
        String nickName = (String) details.get("nickName");
        String qq = (String) details.get("qq");
        String weixin = (String) details.get("weixin");
        String phone = (String) details.get("phone");
        String weibo = (String) details.get("weibo");
        String email = (String) details.get("email");
        String sex = (String) details.get("sex");
        String upLoadIndex = (String) details.get("upLoadIndex");

        Object principal = authentication.getPrincipal();
        UserJwt userJwt = null;
        if(principal instanceof  UserJwt){
            userJwt = (UserJwt) principal;
        }else{
            //refresh_token默认不去调用userdetailService获取用户信息，这里我们手动去调用，得到 UserJwt
            UserDetails userDetails = userDetailsService.loadUserByUsername(name);
            userJwt = (UserJwt) userDetails;
        }
        /*response.put("name", userJwt.getName());
        response.put("id", userJwt.getId());*/
        response.put("name", userJwt.getName());
        response.put("id", id);
        response.put("nickName", nickName);
        response.put("head_pic", head_pic);
        response.put("weixin", weixin);
        response.put("qq", qq);
        response.put("weibo", weibo);
        response.put("phone", phone);
        response.put("email", email);
        response.put("sex", sex);
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

}
