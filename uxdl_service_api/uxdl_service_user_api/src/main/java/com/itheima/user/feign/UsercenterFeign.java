package com.itheima.user.feign;

import com.itheima.user.config.FeignConfig;
import com.itheima.uxdl.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "TBUSER",configuration = FeignConfig.class)
public interface UsercenterFeign {

    /**
     * 获取用户收藏Feign接口
     * @return
     */
    @GetMapping("/usercenter/works/favorites")
    public Result getFavorites();

    @GetMapping("/usercenter/userInfo")
    public Map<String, String> getUserInfo();
}
