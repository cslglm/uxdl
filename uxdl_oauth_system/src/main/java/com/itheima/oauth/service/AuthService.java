package com.itheima.oauth.service;


import com.itheima.oauth.util.AuthToken;
import com.itheima.uxdl.entity.Result;

public interface AuthService {

    AuthToken login(String adminname, String password, String clientId, String clientSecret);

    /**
     * 修改密码
     * @param phone
     * @param password
     * @return
     */
    Result updatePassword(String phone, String password);

}
