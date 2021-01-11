package com.itheima.material.dao;


import com.itheima.material.pojo.Category;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface CategoryMapper extends Mapper<Category> {

    /*
    * id            int(100)      (NULL)           NO      PRI     (NULL)   auto_increment  select,insert,update,references
name          varchar(100)  utf8_general_ci  YES             (NULL)                   select,insert,update,references  分类名称
level         int(100)      (NULL)           YES             (NULL)                   select,insert,update,references  菜单级别
seq           int(100)      (NULL)           YES             (NULL)                   select,insert,update,references  排序
parent_id     int(100)      (NULL)           YES     MUL     (NULL)                   select,insert,update,references  父级id
status        int(1)        (NULL)           YES             1                        select,insert,update,references  状态0禁用，1启用
update_admin  int(20)       (NULL)           YES             (NULL)                   select,insert,update,references  更新人
update_time   date          (NULL)           YES             (NULL)                   select,insert,update,references  更新时间

    * */

    @Select("select * from tb_category where id = #{id} and status = 1")
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
    List<Category> getCategoryList(int id);

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

    @Select("select * from tb_category where parent_id = #{parent_id}")
    @ResultMap("categoryList")
    List<Category> getCategoryByParentId(int parent_id);
}
