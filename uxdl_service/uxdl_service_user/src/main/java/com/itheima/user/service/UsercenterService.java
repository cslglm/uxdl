package com.itheima.user.service;


import com.itheima.material.pojo.Material;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;

import java.util.List;
import java.util.Map;

/**
 * 个人中心
 */
public interface UsercenterService {

    /**
     * 获取用户个人作品
     * @param id
     * @param pageNum
     * @param pageSize
     * @param sort
     * @param category_id
     * @return
     */
    public PageResult findPersonalWorks(String id,String pageNum, String pageSize, String sort, String category_id);

    /**
     * 根据id查询作品
     * @param id
     * @return
     */
    public Material findMaterialById(String id);

    /**
     * 删除作品（逻辑删除）
     * @param id
     */
    public void delById(String id);

    /**
     * 获取用户个人收藏
     * @return
     */
    public Map getFavorites(String id, String category_id, String pageNum, String pageSize);

    /**
     * 取消收藏
     * @param id
     */
    public void delFavorites(String uid,String id);

    /**
     * 添加收藏
     * @param uid
     * @param id
     */
    void addFavorites(String uid, String id);

    /**
     * 获取用户收藏Id
     * @param id
     * @return
     */
    List<Integer> getFavoritesId(String id);

    /**
     * 编辑作品信息
     * @param id
     * @param material
     * @return
     */
    Result editMaterial(String id,Material material);

    /**
     * 新增作品（上传压缩包）信息
     * @param material
     */
    Result addMaterial(Material material);

    /**
     * 添加下载记录
     * @param userId
     * @param materialId
     */
    void addDownloadHistory(Long userId,String materialId);

    /**
     * 根据用户id查找下载记录
     * @param userId
     * @param category_id
     * @return
     */
    Map findDownloadHistory(Long userId, String category_id,String pageNum,String pageSize);

    /**
     * 删除下载记录
     * @param id
     */
    void deleteDownloadHistory(String id);
}
