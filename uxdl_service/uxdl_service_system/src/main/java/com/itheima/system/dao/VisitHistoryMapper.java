package com.itheima.system.dao;

import com.itheima.material.pojo.VisitHistory;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface VisitHistoryMapper extends Mapper<VisitHistory> {

    @Select("select count(user) visitCount,category from tb_visit_history where visit_time >#{beginTime} and visit_time < #{endTime}  group by category ")
    @Results(id = "visitCountMap", value = {
            @Result(property = "visitCount", column = "visitCount"),
            @Result(property = "category", column = "category"),})
    List<Map> findVisitCountByDate(String beginTime, String endTime);

    @Select("select count(user) visitCount,category from tb_visit_history group by category ")
    @ResultMap("visitCountMap")
    List<Map> findVisitCount();

    @Select("select count(user) activeCount from tb_visit_history where visit_time >#{beginTime} and visit_time < #{endTime}  group by user ")
    int findActiveCountByDate(String beginTime, String endTime);

    @Select("select count(user) activeCount from tb_visit_history group by user ")
    int findActiveCount();

}
