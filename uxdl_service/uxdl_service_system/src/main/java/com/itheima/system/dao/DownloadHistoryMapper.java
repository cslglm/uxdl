package com.itheima.system.dao;

import com.itheima.material.pojo.DownloadHistory;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface DownloadHistoryMapper extends Mapper<DownloadHistory> {

    @Select("select count(user_id) downloadCount,category from tb_download_history where download_time >#{beginTime} and download_time < #{endTime}  group by category ")
    @Results(id="downloadCountMap",value = {
            @Result(property = "downloadCount",column = "downloadCount"),
            @Result(property = "category",column = "category"),})
    List<Map> findDownloadCountByDate(String beginTime, String endTime);

    @Select("select count(user_id) downloadCount,category from tb_download_history group by category ")
    @ResultMap("downloadCountMap")
    List<Map> findDownloadCount();

}
