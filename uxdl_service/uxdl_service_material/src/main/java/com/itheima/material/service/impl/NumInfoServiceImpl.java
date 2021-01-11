package com.itheima.material.service.impl;

import com.itheima.material.dao.MaterialMapper;

import com.itheima.material.pojo.Material;
import com.itheima.material.pojo.NumInfo;
import com.itheima.material.respository.NumRespository;
import com.itheima.material.service.NumInfoService;

import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NumInfoServiceImpl implements NumInfoService {

    @Autowired
    private NumRespository numRespository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 导入全部（初始化）
     */
    @Override
    public void importAll() {
        List<Material> materialList = materialMapper.selectAll();
        System.out.println(materialList.size());
        for (Material material : materialList) {
            NumInfo numInfo = new NumInfo();
            numInfo.set_id(material.getId());
            numInfo.setCollectionNum(0);
            numInfo.setDownloadNum(0);
            numInfo.setVisitNum(0);
            numRespository.save(numInfo);
        }
    }

    /**
     * 新增
     *
     * @param materialId
     */
    @Override
    public void add(String materialId) {
        NumInfo numInfo = new NumInfo();
        numInfo.set_id(materialId);
        numInfo.setVisitNum(0);
        numInfo.setDownloadNum(0);
        numInfo.setCollectionNum(0);
        numRespository.save(numInfo);
    }

    /**
     * 删除
     *
     * @param materialId
     */
    @Override
    public void delete(String materialId) {
        NumInfo numInfo = new NumInfo();
        numInfo.set_id(materialId);
        numRespository.delete(numInfo);
    }


    /**
     * 增加收藏数
     *
     * @param materialId
     * @param index
     */
    @Override
    public void updateCollectionNum(String materialId, Integer index) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(materialId));
        Update update = new Update();
        if (index != null) {
            if (index == 0) {
                NumInfo numInfo = numRespository.findById(materialId).get();
                Integer collectionNum = numInfo.getCollectionNum();
                if (collectionNum <= 0) {
                    index = 0;
                }
                if (collectionNum > 0) {
                    index = -1;
                }
            }
            update.inc("collectionNum", index);
            mongoTemplate.updateFirst(query, update, "numInfo");
            SetOperations set = redisTemplate.opsForSet();
            set.add(RedisConstant.DFV_NUM_ID, materialId);
        }
    }

    /**
     * 增加访问数
     *
     * @param materialId
     */
    @Override
    public void updateVisitNum(String materialId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(materialId));
        Update update = new Update();
        update.inc("visitNum", 1);
        mongoTemplate.updateFirst(query, update, "numInfo");
        SetOperations set = redisTemplate.opsForSet();
        set.add(RedisConstant.DFV_NUM_ID, materialId);
    }

    /**
     * 增加下载数
     *
     * @param materialId
     */
    @Override
    public void updateDownloadNum(String materialId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(materialId));
        Update update = new Update();
        update.inc("downloadNum", 1);
        mongoTemplate.updateFirst(query, update, "numInfo");
        SetOperations set = redisTemplate.opsForSet();
        set.add(RedisConstant.DFV_NUM_ID, materialId);
    }

    /**
     * 根据ID查询
     *
     * @param materialId
     * @return
     */
    @Override
    public NumInfo findByMaterialId(String materialId) {
        try {
            NumInfo numInfo = numRespository.findById(materialId).get();
            return numInfo;
        } catch (Exception e) {
            e.printStackTrace();
            add(materialId);
            NumInfo numInfo = numRespository.findById(materialId).get();
            return numInfo;
        }

    }

    /**
     * 修改状态
     * @param materialId
     * @param status
     */
    @Override
    public void updateStatus(String materialId,String status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(materialId));
        Update update = new Update();
        update.addToSet("isDel",Integer.parseInt(status));
        mongoTemplate.updateFirst(query, update, "numInfo");
        SetOperations set = redisTemplate.opsForSet();
        set.add(RedisConstant.DFV_NUM_ID, materialId);
    }
}
