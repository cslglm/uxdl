package com.itheima.material.pojo;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_download_history")
public class DownloadHistory {

    private String material_id;

    private Long user_id;

    private Date download_time;//第一次下载时间

    private String category;//一级分类名称

    private Integer category_id; //分类id

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDownload_time() {
        return download_time;
    }

    public void setDownload_time(Date download_time) {
        this.download_time = download_time;
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
