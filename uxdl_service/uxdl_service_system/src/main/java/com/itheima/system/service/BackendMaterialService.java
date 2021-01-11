package com.itheima.system.service;

import com.itheima.material.pojo.Material;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;

import java.util.Map;

public interface BackendMaterialService {

    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    PageResult findByCondition(Map searchMap);

    /**
     * 删除违规素材
     * @param id
     */
    void delete(String id);

    /**
     * 启用或者禁用素材
     * @param id
     * @return
     */
    Result updateStatus(String id,String status);

    /**
     * 根据id查找素材
     * @param id
     * @return
     */
    Material findOne(String id);

    /**
     * 查看虚拟数据，可选参数（素材名称，模糊查询）
     * 按照更新时间排序
     * @param key
     * @return
     */
    Result checkVirtualData(String key,String pageNum,String pages);

    /**
     * 修改虚拟数据
     * @param id
     * @param paramMap
     * @return
     */
    Material updateVirtualData(String id, Map paramMap);
}
