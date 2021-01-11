package com.itheima.material.pojo;

import javax.persistence.Table;

@Table(name = "tb_keyword")
public class KeywordCount {

    private String keyword;//关键字
    private String kdate;//时间
    private Long kcount;//计数

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKdate() {
        return kdate;
    }

    public void setKdate(String kdate) {
        this.kdate = kdate;
    }

    public Long getKcount() {
        return kcount;
    }

    public void setKcount(Long kcount) {
        this.kcount = kcount;
    }
}
