package com.itheima.material.service;


import com.itheima.material.pojo.NumInfo;

/**
 * 访问数，下载数，收藏数业务处理
 */
public interface NumInfoService {

    /**
     * 导入全部（初始化）
     */
    public void importAll();

    /**
     * 新增
     * @param materialId
     */
    public void add(String materialId);

    /**
     * 删除
     * @param materialId
     */
    public void delete(String materialId);

    /**
     * 修改收藏数
     * @param materialId
     * @param index
     */
    public void updateCollectionNum(String materialId,Integer index);

    /**
     * 修改访问数
     * @param materialId
     */
    public void updateVisitNum(String materialId);

    /**
     * 修改下载数
     * @param materialId
     */
    public void updateDownloadNum(String materialId);

    /**
     * 根据素材Id查询
     * @param materialId
     * @return
     */
    public NumInfo findByMaterialId(String materialId);

    /**
     * 更改素材状态
     * @param materialId
     */
    void updateStatus(String materialId,String status);
}
