package com.itheima.material.jobs;

import com.itheima.material.dao.KeywordMapper;
import com.itheima.material.dao.MaterialMapper;
import com.itheima.material.pojo.KeywordCount;
import com.itheima.material.pojo.Material;
import com.itheima.material.pojo.NumInfo;
import com.itheima.material.service.NumInfoService;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.util.DateUtils;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义Job，实现访问量、下载量、收藏量的数据同步到es
 */
@Component
@Configuration
@EnableScheduling
public class EsImportJob {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private NumInfoService numInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    private UpdateQuery query;

    @Scheduled(cron = "0/20 * * * * ? ")
    public void importData() {
        SetOperations set = redisTemplate.opsForSet();
        Long size = set.size(RedisConstant.DFV_NUM_ID);
        if (size > 0) {
            List<UpdateQuery> list = new ArrayList<>();
            Material material = new Material();
            for (int i = 0; i < size; i++) {
                String id = (String) set.pop(RedisConstant.DFV_NUM_ID);
                NumInfo numInfo = numInfoService.findByMaterialId(id);
                query = new UpdateQuery();
                UpdateRequest request = new UpdateRequest();
                try {
                    request.doc(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("visit_num", numInfo.getVisitNum())
                            .field("download_num", numInfo.getDownloadNum())
                            .field("favourite_num", numInfo.getCollectionNum())
                            .endObject());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                query.setIndexName("materialinfo");
                query.setId(id);
                query.setType("docs");
                query.setUpdateRequest(request);
                materialMapper.selectByPrimaryKey(id);
                if (numInfo.getIsDel()!=1){
                    list.add(query);
                }
                material.setId(id);
                material.setVisit_num(numInfo.getVisitNum());
                material.setFavourite_num(numInfo.getCollectionNum());
                material.setDownload_num(numInfo.getDownloadNum());
                materialMapper.updateByPrimaryKeySelective(material);
            }
            elasticsearchTemplate.bulkUpdate(list);
        }
    }

    @Scheduled(cron = "0 0 0 1 * ? ")
    public void importKeywordSchedule() {
        Date date = new Date();
        String date2String = null;
        try {
            ValueOperations valueOperations = redisTemplate.opsForValue();
            date2String = DateUtils.parseDate2String(date, "yyyy-MM");
            String keywordDate = (String) valueOperations.get("keywordDate");
            if (null == keywordDate ) {
                valueOperations.set("keywordDate", date2String);
            }
            keywordDate = (String) valueOperations.get("keywordDate");
            if ( keywordDate.equals(date2String)) {
                valueOperations.set("keywordDate", date2String);
                importKeyword(keywordDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importKeyword(String dateStr) {
        BoundHashOperations keywordHash = redisTemplate.boundHashOps("keyword");
        KeywordCount keywordCount = new KeywordCount();
        Set keys = keywordHash.keys();
        if (keys != null) {
            for (Object key : keys) {
                Long count = (Long) keywordHash.get(key);
                keywordCount.setKcount(count);
                keywordCount.setKdate(dateStr);
                keywordCount.setKeyword((String) key);
                keywordMapper.insert(keywordCount);
                keywordHash.delete(key);
            }
        }
    }
}