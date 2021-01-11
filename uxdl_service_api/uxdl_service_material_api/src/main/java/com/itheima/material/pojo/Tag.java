package com.itheima.material.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * 标签实体类
 */
@Table(name = "tb_tag")
public class Tag {
    @Id
    private Integer id;
    private String name;//标签名称
    private String update_admin;//更新人
    private Date update_time;//更新时间
    private Integer category_id;//分类id
    private Category category;//分类信息

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdate_admin() {
        return update_admin;
    }

    public void setUpdate_admin(String update_admin) {
        this.update_admin = update_admin;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
