package com.itheima.system.dao;

import com.itheima.material.pojo.KeywordCount;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface KeywordMapper extends Mapper<KeywordCount> {
    @Select("select keyword , kcount from tb_keyword where kdate = #{dateStr} order by kcount limit 0,5")
    @Results(id = "keywordCountMap", value = {
            @Result(property = "keyword", column = "keyword"),
            @Result(property = "keywordCount", column = "kount"),})
    List<Map> findKeywordCountByDate(String dateStr);

    @Select("select count(0) from tb_keyword where kdate=#{dateStr}")
    int findCountByDate(String dateStr);

    @Select("select count(0) from tb_keyword")
    int findCount();

    @Select("select keyword , kcount from tb_keyword  order by kcount limit 0,5")
    @ResultMap("keywordCountMap")
    List<Map> findKeywordCount();

}
