package com.itheima.user.controller;

import com.itheima.user.config.TokenDecode;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private TokenDecode tokenDecode;

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/info")
    public Result getUserInfo(){
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0){
            return new Result(true, StatusCode.ERROR, "用户未登录");
        }
        String id = userInfo.get("id");
        String nickName = userInfo.get("nickName");
        String head_pic = userInfo.get("head_pic");
        String weixin = userInfo.get("weixin");
        String qq = userInfo.get("qq");
        String weibo = userInfo.get("weibo");
        String phone = userInfo.get("phone");
        String email = userInfo.get("email");
        String sex = userInfo.get("sex");

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("id", id);
        resultMap.put("head_pic", head_pic);
        resultMap.put("nickName", nickName);
        resultMap.put("weixin", weixin);
        resultMap.put("qq", qq);
        resultMap.put("phone", phone);
        resultMap.put("weibo", weibo);
        resultMap.put("email", email);
        resultMap.put("sex", sex);
        return new Result(true, StatusCode.OK, "获取成功", resultMap);
    }
}
