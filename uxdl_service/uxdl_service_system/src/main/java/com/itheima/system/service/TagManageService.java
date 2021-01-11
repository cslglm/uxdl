package com.itheima.system.service;


import com.itheima.uxdl.entity.PageResult;

import java.util.Map;

public interface TagManageService {

    /**
     * 条件分页查询
     * @param searchMap
     * @return
     */
    PageResult findPage(Map searchMap);

    /**
     * 新建标签
     * @param name
     * @param username
     * @param categoryId
     */
    void add(String name, String username, int categoryId);

    /**
     * 删除标签
     * @param tagId
     */
    void delete(String tagId);

    /**
     * 编辑标签
     * @param category_id
     * @param name
     * @param id
     * @param username
     */
    void update( String name, String id, String username,String category_id);
}
