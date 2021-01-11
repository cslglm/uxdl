package com.itheima.system.dao;

import com.itheima.material.pojo.Favourite;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FavouriteMapper extends Mapper<Favourite> {

    @Select("select count(user_id) favouriteCount,category from tb_user_material where collect_time >#{beginTime} and collect_time < #{endTime}  group by category ")
    @Results(id="favouriteCountMap",value = {
            @Result(property = "favouriteCount",column = "favouriteCount"),
            @Result(property = "category",column = "category"),})
    List<Map> findFavouriteCountByDate(String beginTime, String endTime);

    @Select("select count(user_id) favouriteCount,category from tb_user_material group by category ")
    @ResultMap("favouriteCountMap")
    List<Map> findFavouriteCount();
}
