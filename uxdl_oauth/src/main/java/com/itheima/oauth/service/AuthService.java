package com.itheima.oauth.service;


import com.itheima.oauth.util.AuthToken;
import com.itheima.uxdl.entity.Result;

public interface AuthService {

    AuthToken login(String username, String password,String index, String clientId, String clientSecret);

    /**
     * 用户注册
     * @param phone
     * @param password
     * @return
     */
    public Result register(String phone, String password);

    /**
     * 修改密码
     * @param phone
     * @param password
     * @return
     */
    public Result updatePassword(String phone, String password);
}
