package com.itheima.material.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 版权信息实体类
 */
@Table(name = "tb_copyright")
public class Copyright implements Serializable {

    @Id
    private Integer id; //
    private String mrange; //  授权范围
    private String source; //  版权搜索
    private String licensing; //  授权方式

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMrange() {
        return mrange;
    }

    public void setMrange(String mrange) {
        this.mrange = mrange;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLicensing() {
        return licensing;
    }

    public void setLicensing(String licensing) {
        this.licensing = licensing;
    }

    @Override
    public String toString() {
        return "Copyright{" +
                "id=" + id +
                ", mrange='" + mrange + '\'' +
                ", source='" + source + '\'' +
                ", licensing='" + licensing + '\'' +
                '}';
    }
}
