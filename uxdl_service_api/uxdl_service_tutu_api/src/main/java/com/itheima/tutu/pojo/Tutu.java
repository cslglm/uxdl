package com.itheima.tutu.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 轮播图实体类
 */
@Table(name = "tb_ad")
public class Tutu implements Serializable {
    @Id
    private Integer id;  //
    private String name;  //  图片名称
    private Date start_time;  //  开始时间
    private Date end_time;  //  截至时间
    private Integer status;  //  图片状态
    private String image;  //  图片地址
    private String url;  //  七牛云URL
    private String remarks;  //  备注
    private String update_admin;//更新人
    private Date update_time;//更新时间
    private Integer seq;//排序号
    private String link_url;//点击轮播图后跳转的地址

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

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    @Override
    public String toString() {
        return "Tutu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", status=" + status +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", remarks='" + remarks + '\'' +
                ", update_admin=" + update_admin +
                ", update_time=" + update_time +
                ", seq=" + seq +
                ", link_url='" + link_url + '\'' +
                '}';
    }
}
