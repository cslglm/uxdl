package com.itheima.system.service;

import com.itheima.tutu.pojo.Tutu;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;

import java.util.Map;

public interface TutuManageService {

    /**
     * 条件分页查询
     * @param searchMap
     * @return
     */
    PageResult findPage(Map searchMap);

    /**
     * 新增轮播图
     * @param tutu
     */
    Result add(Tutu tutu);

    /**
     * 删除轮播图
     * @param tutuId
     */
    void delete(String tutuId);

    /**
     * 编辑轮播图
     * @param tutu
     */
    Result update(Tutu tutu,String id);

    /**
     * 修改轮播图权重值和跳转链接
     * @param id
     * @param weight
     * @return
     */
    Result updateWeight(String id, String weight,String link_url,String status);
}
