package com.itheima.uxdl.entity;

public class RedisConstant {

    //数据库里不一定存在的数据
    public static final String MATE_PIC_RESOURCES = "MatePicResources";
    //数据库里存在的数据
    public static final String MATE_PIC_DB_RESOURCES = "MatePicDbResources";
    //更新下载量、访问量、收藏数时保存的 id的集合名称(用于将更新后的数据同步到es)
    public static final String DFV_NUM_ID ="DFV_NumberId";
}
