package com.itheima.user.dao;

import com.itheima.material.pojo.DownloadHistory;
import com.itheima.material.pojo.Material;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DownloadHistoryMapper extends Mapper<DownloadHistory> {

    @Select("select * from tb_material where id IN (select material_id from tb_download_history where user_id=#{user_id} and category_id in ${categoryIds}) limit #{start} , #{size}")
    List<Material> getDownloadHistoryByCategoryId(Long user_id, String categoryIds, Integer start, Integer size);

    @Select("select * from tb_material where id IN (select material_id from tb_download_history where user_id=#{user_id}) limit #{start} , #{size}")
    List<Material> getDownloadHistory(Long user_id,Integer start,Integer size);

    @Select("select count(0) from tb_material where id IN (select material_id from tb_download_history where user_id=#{user_id} and category_id in ${categoryIds})")
    Integer getDownloadHistoryCountByCategoryId(Long user_id, String categoryIds);

    @Select("select count(0) from tb_material where id IN (select material_id from tb_download_history where user_id=#{user_id})")
    Integer getDownloadHistoryCount(Long user_id);
}
