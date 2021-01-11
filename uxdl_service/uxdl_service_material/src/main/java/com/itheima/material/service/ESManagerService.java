package com.itheima.material.service;


import com.itheima.material.pojo.Material;

public interface ESManagerService {

    /**
     * 创建索引和映射
     */
    void createIndexAndMapping();

    /**
     * 导入数据
     */
    void importAll();

    /**
     * 根据素材id导入素材
     * @param material
     */
    void importDataByMaterialId(Material material);

    /**
     * 根据id删除素材
     * @param materialId
     */
    void delDataByMaterialId(String materialId);
}
