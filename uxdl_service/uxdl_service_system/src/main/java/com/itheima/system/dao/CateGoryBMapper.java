package com.itheima.system.dao;

import com.github.pagehelper.Page;
import com.itheima.material.pojo.Category;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CateGoryBMapper extends Mapper<Category> {

    @Select("select max(seq) from tb_category where level = #{level}")
    Integer findMaxSeq(Integer level);



    @Select("select * from tb_category where level = 1")
    @Results(id="categoryList",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "parent_id",column = "parent_id"),
            @Result(property = "level",column = "level"),
            @Result(property = "seq",column = "seq"),
            @Result(property = "status",column = "status"),
            @Result(property = "update_admin",column = "update_admin"),
            @Result(property = "update_time",column = "update_time"),
            @Result(property = "categoryList",column = "id",many = @Many(select = "getCategoryByParentId")),
    })
    Page<Category> getCategoryList();

    @Select("select * from tb_category where parent_id = #{parent_id}")
    @ResultMap("categoryList")
    List<Category> getCategoryByParentId(int parent_id);

    @Insert("insert into tb_category (name,level,parent_id,status,update_admin,update_time) values (#{name}, #{level},#{parent_id},#{status},#{update_admin},#{update_time})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertCategory(Category category);

    @Select("select * from tb_category where id = #{id}")
    @Results(id="categoryList1",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "parent_id",column = "parent_id"),
            @Result(property = "level",column = "level"),
            @Result(property = "seq",column = "seq"),
            @Result(property = "status",column = "status"),
            @Result(property = "update_admin",column = "update_admin"),
            @Result(property = "update_time",column = "update_time"),
            @Result(property = "categoryList",column = "id",many = @Many(select = "getCategoryByPid")),
    })
    Category findCategoryById(int id);

    @Select("select * from tb_category where parent_id = #{parent_id}")
    @ResultMap("categoryList1")
    List<Category> getCategoryByPid(int parent_id);
}
