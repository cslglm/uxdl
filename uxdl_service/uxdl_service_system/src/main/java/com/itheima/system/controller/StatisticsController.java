package com.itheima.system.controller;

import com.itheima.system.service.StatisticsService;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics/core")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    /**
     * 网站数据概览
     * @param dateStr
     * @return
     */
    @GetMapping("/overview/{dateStr}")
    public Result overview(@PathVariable("dateStr") String dateStr) {

        Map<String,Integer> map = statisticsService.overview(dateStr);

        return new Result(true, StatusCode.OK, "查询成功",map);
    }

    /**
     * 关键词搜索
     * @param dateStr
     * @return
     */
    @GetMapping("/keyword/{dateStr}")
    public Result KeyWord(@PathVariable("dateStr") String dateStr){
           List<Map> list =  statisticsService.keyword(dateStr);
           return new Result(true,StatusCode.OK,"查询成功",list);

    }

    /**
     * 分类统计
     * @param dateStr
     * @return
     */
    @GetMapping("/category/{dateStr}")
    public Result categoryCount(@PathVariable("dateStr") String dateStr){
        Map<String,List<Map>> map = statisticsService.categoryCount(dateStr);
        return new Result(true,StatusCode.OK,"查询成功",map);
    }

}
