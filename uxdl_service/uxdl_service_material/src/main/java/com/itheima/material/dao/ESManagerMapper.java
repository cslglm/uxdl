package com.itheima.material.dao;

import com.itheima.material.pojo.MaterialInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * esMapper
 */
public interface ESManagerMapper extends ElasticsearchRepository<MaterialInfo,String> {
}
