package com.itheima.material.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分类菜单实体类
 */
@Table(name = "tb_category")
public class Category implements Serializable {
    @Id
    private Integer id; //
    private String name;// 分类名称
    private Integer level; // 菜单级别
    private Integer seq; // 排序
    private Integer parent_id; // 父级id
    private Integer status;//状态 状态0禁用，1启用
    private String update_admin;//更新人
    private Date update_time;//更新时间
    private List<Category> categoryList; //封装子类

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", seq=" + seq +
                ", parent_id=" + parent_id +
                ", status=" + status +
                ", update_admin='" + update_admin + '\'' +
                ", update_time=" + update_time +
                ", categoryList=" + categoryList +
                '}';
    }
}
