package com.itheima.material.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.io.Serializable;
import java.util.Date;

/**
 * ES创建索引实体类
 */
@Document(indexName = "materialinfo", type = "docs")
public class MaterialInfo implements Serializable {

    @Id
    @Field(index = true, store = true, type = FieldType.Keyword)
    private String id;


    @Field(index = true, store = true, type = FieldType.Text, analyzer = "ik_smart")
    private String name;                 //名称

    @Field(index = true, store = true, type = FieldType.Text, analyzer = "ik_smart")
    private String category1_name;           //一级分类名称

    @Field(index = true, store = true, type = FieldType.Text, analyzer = "ik_smart")
    private String category2_name;           //二级分类名称

    @Field(index = true, store = true, type = FieldType.Text, analyzer = "ik_smart")
    private String category3_name;           //三级分类名称

    @Field(index = true, store = true, type = FieldType.Text, analyzer = "ik_smart")
    private String category4_name;           //四级分类名称

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String user;//用户名

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer category1_id;        //一级分类

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer category2_id;        //二级分类

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer category3_id;        //三级分类

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer category4_id;        //三级分类

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String show_image;           //展示图片

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer download_num;        //下载量

    @Field(index = true, store = true, type = FieldType.Date)
    private Date create_time;            //创建时间

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer favourite_num;      //收藏数

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer visit_num;           //访问数

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer basic_visit_num;           //虚拟访问量

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer basic_favourite_num;           //虚拟收藏量

    @Field(index = true, store = true, type = FieldType.Integer)
    private Integer basic_download_num;           //虚拟下载量

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String source_type;           //源文件格式

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String source_size;           //源文件大小

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String source_dpi;           //源文件分辨率

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String tags;           //标签

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String downloadurl;           //下载链接

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String description;           //作品描述

    @Field(index = true, store = true, type = FieldType.Keyword)
    private String musage;           //作品用途


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCategory1_name() {
        return category1_name;
    }

    public void setCategory1_name(String category1_name) {
        this.category1_name = category1_name;
    }

    public String getCategory2_name() {
        return category2_name;
    }

    public void setCategory2_name(String category2_name) {
        this.category2_name = category2_name;
    }

    public String getCategory3_name() {
        return category3_name;
    }

    public void setCategory3_name(String category3_name) {
        this.category3_name = category3_name;
    }

    public String getCategory4_name() {
        return category4_name;
    }

    public void setCategory4_name(String category4_name) {
        this.category4_name = category4_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusage() {
        return musage;
    }

    public void setMusage(String musage) {
        this.musage = musage;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory1_id() {
        return category1_id;
    }

    public void setCategory1_id(Integer category1_id) {
        this.category1_id = category1_id;
    }



    public Integer getCategory2_id() {
        return category2_id;
    }

    public void setCategory2_id(Integer category2_id) {
        this.category2_id = category2_id;
    }

    public Integer getCategory3_id() {
        return category3_id;
    }

    public void setCategory3_id(Integer category3_id) {
        this.category3_id = category3_id;
    }

    public Integer getCategory4_id() {
        return category4_id;
    }

    public void setCategory4_id(Integer category4_id) {
        this.category4_id = category4_id;
    }

    public String getShow_image() {
        return show_image;
    }

    public void setShow_image(String show_image) {
        this.show_image = show_image;
    }


    public Integer getDownload_num() {
        return download_num;
    }

    public void setDownload_num(Integer download_num) {
        this.download_num = download_num;
    }

    public Date getUpdate_time() {
        return create_time;
    }

    public void setUpdate_time(Date update_time) {
        this.create_time = update_time;
    }

    public Integer getFavourite_num() {
        return favourite_num;
    }

    public void setFavourite_num(Integer favourite_num) {
        this.favourite_num = favourite_num;
    }

    public Integer getVisit_num() {
        return visit_num;
    }

    public void setVisit_num(Integer visit_num) {
        this.visit_num = visit_num;
    }

    public Integer getBasic_visit_num() {
        return basic_visit_num;
    }

    public void setBasic_visit_num(Integer basic_visit_num) {
        this.basic_visit_num = basic_visit_num;
    }

    public Integer getBasic_favourite_num() {
        return basic_favourite_num;
    }

    public void setBasic_favourite_num(Integer basic_favourite_num) {
        this.basic_favourite_num = basic_favourite_num;
    }

    public Integer getBasic_download_num() {
        return basic_download_num;
    }

    public void setBasic_download_num(Integer basic_download_num) {
        this.basic_download_num = basic_download_num;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getSource_size() {
        return source_size;
    }

    public void setSource_size(String source_size) {
        this.source_size = source_size;
    }

    public String getSource_dpi() {
        return source_dpi;
    }

    public void setSource_dpi(String source_dpi) {
        this.source_dpi = source_dpi;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
