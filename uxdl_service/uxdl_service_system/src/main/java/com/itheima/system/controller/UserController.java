package com.itheima.system.controller;

import com.itheima.system.config.TokenDecode;
import com.itheima.system.pojo.SysAdmin;
import com.itheima.system.service.UserService;

import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.Map;

/**
 * 用户业务
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenDecode tokenDecode;

    /**
     * 根据调件查询用户
     *
     * @param searchMap
     * @return
     */
    @GetMapping
    public Result findUserByCondition(@RequestParam Map searchMap) {
        try {
            int pageNum = Integer.parseInt((String) searchMap.get("pageNum"));
            int pageSize = Integer.parseInt((String) searchMap.get("pageSize"));

            if (pageNum < 0 || pageSize < 0) {
                return new Result(false, StatusCode.ERROR, "页码不能为负数");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "页码不合法");
        }

        PageResult pageResult = userService.findUserByCondition(searchMap);
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 根据用户id切换用户状态
     *
     * @param userId
     * @return
     */
    @PatchMapping("/{userId}")
    public Result updateStatus(@PathVariable("userId") String userId) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        String adminName = userInfo.get("username");
        if (adminName == null) {
            return new Result(false, StatusCode.ERROR, "获取管理员身份失败");
        }
        userService.updateStatus(userId, adminName);
        return new Result(true, StatusCode.OK, "状态切换成功");
    }

    /**
     * 获取管理员信息
     *
     * @return
     */
    @GetMapping("/adminInfo")
    public Result getAdminInfo() {

        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return null;
        }
        SysAdmin sysAdmin = new SysAdmin();
        sysAdmin.setUsername(userInfo.get("username"));
        sysAdmin.setId(Integer.parseInt(userInfo.get("id")));
        sysAdmin.setRealname(userInfo.get("realname"));
        try {
            Date birthday = DateUtils.parseString2Date(userInfo.get("birthday"));
            sysAdmin.setBirthday(birthday);
        } catch (Exception e) {
            sysAdmin.setPhone(userInfo.get("phone"));
            sysAdmin.setSex(userInfo.get("sex"));
            return new Result(true, StatusCode.OK, "获取用户信息成功", sysAdmin);
        }
        sysAdmin.setPhone(userInfo.get("phone"));
        sysAdmin.setSex(userInfo.get("sex"));
        return new Result(true, StatusCode.OK, "获取用户信息成功", sysAdmin);

    }

    /**
     * 根据用户id查询用户相关数据
     *
     * @param userId
     * @return
     */
    @GetMapping("/meterials")
    public Result findUserDate(String userId, String pageNum, String pageSize) {
        Map<String, Object> userDate = userService.findUserDate(userId, pageNum, pageSize);
        return new Result(true, StatusCode.OK, "查询成功", userDate);
    }
}
