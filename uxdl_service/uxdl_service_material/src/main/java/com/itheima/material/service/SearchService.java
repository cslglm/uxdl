package com.itheima.material.service;

import java.util.Map;

public interface SearchService {

    /**
     * 根据分类id查询素材
     * @param searchMap
     * @return
     */
    Map<String,Object> searchMaterial(Map<String,String> searchMap );

}
