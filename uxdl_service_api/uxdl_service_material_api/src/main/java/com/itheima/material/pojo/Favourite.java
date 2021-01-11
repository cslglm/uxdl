package com.itheima.material.pojo;

import javax.persistence.Table;
import java.util.Date;

/**
 * 用户收藏实体类
 */
@Table(name = "tb_user_material")
public class Favourite {

    private String material_id;

    private Long user_id;

    private String category;//一级分类名称

    private Date collect_time;//收藏时间

    private Integer category_id; // 分类id

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public Date getCollect_time() {
        return collect_time;
    }

    public void setCollect_time(Date collect_time) {
        this.collect_time = collect_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}
