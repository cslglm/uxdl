package com.itheima.system.service;


import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /**
     * 网站数据概览
     * @param dateStr
     * @return
     */
    Map<String, Integer> overview(String dateStr);

    /**
     * 分类统计
     * @param dateStr
     * @return
     */
    Map<String,List<Map>> categoryCount(String dateStr);

    /**
     * 关键词统计
     * @param dateStr
     * @return
     */
    List<Map> keyword(String dateStr);
}
