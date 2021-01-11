package com.itheima.system.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "sys_menu")
public class SysMenu implements Serializable {
    @Id
    private Integer id;//
    private String name;//  菜单名称
    private String linkurl;//  页面地址
    private String path;//  路径
    private Integer priority;//  优先级
    private String icon;//  图标
    private String description;//  描述
    private Integer parent_id;//  父级id
    private Integer level;//  菜单等级
    private Integer status;//

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

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SysMenu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", linkurl='" + linkurl + '\'' +
                ", path='" + path + '\'' +
                ", priority=" + priority +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", parent_id=" + parent_id +
                ", level=" + level +
                ", status=" + status +
                '}';
    }
}
