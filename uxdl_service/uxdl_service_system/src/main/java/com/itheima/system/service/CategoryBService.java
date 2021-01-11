package com.itheima.system.service;

import com.itheima.material.pojo.Category;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;

import java.util.List;

/**
 * 后台分类相关业务
 */
public interface CategoryBService {

    /**
     * 获取所有分类
     * @param num
     * @param size
     * @return
     */
    PageResult findAllCategory(Integer num, Integer size,String condition);

    /**
     * 根据id查找菜单
     * @param id
     * @return
     */
    Category findCategoryById(String id);

    /**
     * 新增分类
     * @param categoryNames
     * @param status
     */
    void addCategoryLevel1(String[] categoryNames, String status,String username);

    /**
     * 编辑分类
     * @param parent_id
     * @param name
     * @param categoryId
     * @param status
     * @param username
     */
    void updateCateGory( String name, String categoryId, String status, String username,String parent_id);

    /**
     * 启用分类
     * @param id
     * @param username
     * @return
     */
    Result updateStatus(String id, String username,String status);

    /**
     * 删除分类
     * @param id
     */
    void delCategory(String id);

    /**
     * 根据等级查询分类信息
     * @param level
     * @return
     */
    List<Category> findByLevel(String level);
}
