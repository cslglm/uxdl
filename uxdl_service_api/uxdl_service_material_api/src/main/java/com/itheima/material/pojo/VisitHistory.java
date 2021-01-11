package com.itheima.material.pojo;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_visit_history")
public class VisitHistory {
    private String material_id;

    private String user;//用户ip

    private String category;//一级分类名称

    private Date visit_time;//第一次访问时间

    private Integer category_id; //分类id

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getVisit_time() {
        return visit_time;
    }

    public void setVisit_time(Date visit_time) {
        this.visit_time = visit_time;
    }

}
