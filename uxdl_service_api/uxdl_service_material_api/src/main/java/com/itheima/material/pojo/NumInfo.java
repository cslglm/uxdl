package com.itheima.material.pojo;



import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Mongo实体类
 */
public class NumInfo implements Serializable {

    @Id
    private String _id;//id(素材Id)
    private Integer collectionNum;//收藏数
    private Integer visitNum;//访问数
    private Integer downloadNum;//下载数
    private Integer isDel;//对应的素材是否删除

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(Integer collectionNum) {
        this.collectionNum = collectionNum;
    }

    public Integer getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(Integer visitNum) {
        this.visitNum = visitNum;
    }

    public Integer getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(Integer downloadNum) {
        this.downloadNum = downloadNum;
    }

    @Override
    public String toString() {
        return "NumInfo{" +
                "_id='" + _id + '\'' +
                ", collectionNum=" + collectionNum +
                ", visitNum=" + visitNum +
                ", downloadNum=" + downloadNum +
                '}';
    }
}
