package com.itheima.material.service.impl;

import com.alibaba.fastjson.JSON;
import com.itheima.material.dao.ESManagerMapper;
import com.itheima.material.dao.MaterialMapper;
import com.itheima.material.pojo.Material;
import com.itheima.material.pojo.MaterialInfo;
import com.itheima.material.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ESManagerServiceImpl implements ESManagerService {


    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ESManagerMapper esManagerMapper;

    /**
     * 创建es索引库
     */
    @Override
    public void createIndexAndMapping() {

        elasticsearchTemplate.createIndex(MaterialInfo.class);
        elasticsearchTemplate.putMapping(MaterialInfo.class);
    }

    /**
     * es导入全部数据
     */
    @Override
    public void importAll() {
        Example example = new Example(Material.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1").andEqualTo("draft","0").andEqualTo("isdel","0");
        List<Material> materialList = materialMapper.selectByExample(example);
        if (materialList == null || materialList.size() == 0) {
            throw new RuntimeException("无数据，无法导入");
        }
        String jsonMaterialList = JSON.toJSONString(materialList);
        List<MaterialInfo> materialInfoList = JSON.parseArray(jsonMaterialList, MaterialInfo.class);
        esManagerMapper.saveAll(materialInfoList);
    }

    /**
     * es导入单条数据
     * @param material
     */
    @Override
    public void importDataByMaterialId(Material material) {

        if (material == null || material.getStatus() == 0 ) {
            throw new RuntimeException("没有数据，无法导入");
        }

        String jsonSkuList = JSON.toJSONString(material);

        MaterialInfo materialInfo = JSON.parseObject(jsonSkuList, MaterialInfo.class);

        esManagerMapper.save(materialInfo);
    }

    /**
     * 根据materialId删除es库中的数据
     * @param materialId
     */
    @Override
    public void delDataByMaterialId(String materialId) {
        esManagerMapper.deleteById(materialId);
        //System.out.println("下架成功");
    }


}
