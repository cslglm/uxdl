package com.itheima.user.dao;

import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Material;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MaterialMapper extends Mapper<Material> {

    @Select("select * from tb_material where id IN (select material_id from tb_user_material where user_id=#{user_id} and category_id in ${categoryIds}) limit #{start} , #{size}")
    List<Material> getFavouritesByCategoryId(String user_id,String categoryIds,Integer start,Integer size);

    @Select("select * from tb_material where id IN (select material_id from tb_user_material where user_id=#{user_id}) limit #{start} , #{size}")
    List<Material> getFavourites(String user_id,Integer start,Integer size);

    @Select("select count(0) from tb_material where id IN (select material_id from tb_user_material where user_id=#{user_id} and category_id in ${categoryIds})")
    Integer getFavouritesCountByCategoryId(String user_id,String categoryIds);

    @Select("select count(0) from tb_material where id IN (select material_id from tb_user_material where user_id=#{user_id})")
    Integer getFavouritesCount(String user_id);
}
