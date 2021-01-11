package com.itheima.system.service;


import com.itheima.user.pojo.TbUser;
import com.itheima.uxdl.entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 用户管理相关业务
 */
public interface UserService {

    /**
     * 根据条件查询用户
     * @param searchMap
     * @return
     */
    public PageResult findUserByCondition(Map searchMap);

    /**
     * 根据用户id切换用户状态
     * @param userId
     */
    public void updateStatus(String userId,String adminName);

    /**
     * 查询用户数据
     * @param userId
     * @return
     */
    public Map<String,Object> findUserDate(String userId ,String pageNum,String pageSize);
}
