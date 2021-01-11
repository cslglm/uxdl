package com.itheima.system.service.impl;

import com.itheima.material.pojo.DownloadHistory;
import com.itheima.material.pojo.Favourite;
import com.itheima.material.pojo.Material;
import com.itheima.material.pojo.VisitHistory;
import com.itheima.system.dao.*;
import com.itheima.system.service.StatisticsService;
import com.itheima.user.pojo.TbUser;
import com.itheima.uxdl.util.DateUtils;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private VisitHistoryMapper visitHistoryMapper;

    @Autowired
    private DownloadHistoryMapper downloadHistoryMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 网站数据概览
     *
     * @param dateStr
     * @return
     */
    @Override
    public Map<String, Integer> overview(String dateStr) {
        Example visitExample = new Example(VisitHistory.class);
        Example downloadExample = new Example(DownloadHistory.class);
        Example favouriteExample = new Example(Favourite.class);
        Example userExample = new Example(TbUser.class);
        Example materialExample = new Example(Material.class);
        int activeCount=0 ;
        if (null == dateStr || "".equals(dateStr)) {
            visitExample.createCriteria().andIsNotNull("user");
            downloadExample.createCriteria().andIsNotNull("user_id");
            favouriteExample.createCriteria().andIsNotNull("user_id");
            userExample.createCriteria().andIsNotNull("id");
            materialExample.createCriteria().andIsNotNull("id");
            activeCount = visitHistoryMapper.findActiveCount();

        } else {
            try {
                Date begin = DateUtils.parseString2Date(dateStr + "-01");
                Date end = DateUtils.parseString2Date(dateStr + "-31");
                visitExample.createCriteria().andGreaterThanOrEqualTo("visit_time", begin).andLessThanOrEqualTo("visit_time", end);
                downloadExample.createCriteria().andGreaterThanOrEqualTo("download_time", begin).andLessThanOrEqualTo("download_time", end);
                favouriteExample.createCriteria().andGreaterThanOrEqualTo("collect_time", begin).andLessThanOrEqualTo("collect_time", end);
                userExample.createCriteria().andGreaterThanOrEqualTo("create_time", begin).andLessThanOrEqualTo("create_time", end);
                materialExample.createCriteria().andGreaterThanOrEqualTo("create_time", begin).andLessThanOrEqualTo("create_time", end);
                activeCount = visitHistoryMapper.findActiveCountByDate(dateStr + "-01",dateStr + "-31");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        Map<String, Integer> map = new HashMap<>();
        int visitCount = visitHistoryMapper.selectCountByExample(visitExample);
        int downloadCount = downloadHistoryMapper.selectCountByExample(downloadExample);
        int favouriteCount = favouriteMapper.selectCountByExample(favouriteExample);
        int userCount = userMapper.selectCountByExample(userExample);
        int materialCount = materialMapper.selectCountByExample(materialExample);
        map.put("visitCount", visitCount);
        map.put("downloadCount", downloadCount);
        map.put("favouriteCount", favouriteCount);
        map.put("userCount", userCount);
        map.put("materialCount", materialCount);
        map.put("activeCount",activeCount);
        return map;
    }

    /**
     * 分类统计
     *
     * @param dateStr
     * @return
     */
    @Override
    public Map<String, List<Map>> categoryCount(String dateStr) {

        List<Map> visitCount = null;
        List<Map> downloadCount = null;
        List<Map> favouriteCount = null;
        if (null == dateStr || "".equals(dateStr)) {
            visitCount = visitHistoryMapper.findVisitCount();
            downloadCount = downloadHistoryMapper.findDownloadCount();
            favouriteCount = favouriteMapper.findFavouriteCount();
        } else {
            String begin = dateStr + "-01";
            String end = dateStr + "-31";
            visitCount = visitHistoryMapper.findVisitCountByDate(begin, end);
            downloadCount = downloadHistoryMapper.findDownloadCountByDate(begin, end);
            favouriteCount = favouriteMapper.findFavouriteCountByDate(begin, end);
        }
        Map<String, List<Map>> map = new HashMap<>();

        map.put("visitCount", visitCount);
        map.put("downloadCount", downloadCount);
        map.put("favouriteCount", favouriteCount);
        return map;
    }

    /**
     * 关键词统计
     *
     * @param dateStr
     * @return
     */
    @Override
    public List<Map> keyword(String dateStr) {

        List<Map> keywordCount = null;
        int totalCount = 0;
        if (null == dateStr) {
            keywordCount = keywordMapper.findKeywordCount();
            totalCount = keywordMapper.findCount();
        } else {
            String keywordDate = null;
            try {
                keywordDate = DateUtils.parseDate2String(new Date(), "yyyy-MM");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (keywordDate.equals(dateStr)) {
                BoundHashOperations keywordHash = redisTemplate.boundHashOps("keyword");
                Set keys = keywordHash.keys();
                Map keywordMap = new HashMap();
                if (keys != null && keys.size() > 0) {
                    for (Object key : keys) {
                        Long count = (Long) keywordHash.get(key);
                        keywordMap.put(key, count);
                        keywordCount.add(keywordMap);
                    }
                }
            } else {
                keywordCount = keywordMapper.findKeywordCountByDate(dateStr);
            }
            totalCount = keywordMapper.findCountByDate(dateStr);
        }

        Map totalMap = new HashMap();
        totalMap.put("kcount", totalCount);
        totalMap.put("keyword","全部");
        keywordCount.add(totalMap);
        return keywordCount;
    }
}
