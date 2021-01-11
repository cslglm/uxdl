package com.itheima.system.service;

import com.itheima.system.pojo.SysAdmin;
import com.itheima.uxdl.entity.PageResult;

import java.util.List;
import java.util.Map;

public interface AdminService {



    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    PageResult findByCondition(Map searchMap);

    /**
     * 切换用户状态
     * @param id
     */
    void updateStatus(String id);

    /**
     * 新增管理员
     * @param sysAdmin
     */
    void addAdmin(SysAdmin sysAdmin);

    /**
     * 编辑用户数据回显（根据id查询用户）
     * @param id
     * @return
     */
    SysAdmin findById(String id);

    /**
     * 编辑用户
     * @param sysAdmin
     */
    void update(SysAdmin sysAdmin);

    /**
     * 删除管理员
     * @param id
     */
    void delAdmin(String id);
}
