package com.itheima.material.service;

import com.itheima.material.pojo.Copyright;
import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Material;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

public interface MaterialService {


    /**
     * 根据分类等级查询分类
     * @param level
     * @return
     */
    public List<Category> findCategoryByLevel(@PathVariable String level);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    Map<String,Object> findDetailsById(String id);

    /**
     * 根据父类id查询以下所有
     * @param id
     * @return
     */
    List<Category> findAllCategorys(String id);


    /**
     *条件查询
     * @param searchMap
     * @return
     */
    List<Material> findList(Map<String,Object> searchMap);

    /**
     * 根据CopyrightID查询Copyright
     * @param copyrightId
     * @return
     */
    Copyright findCopyrightByCId(Integer copyrightId);





    /**
     * 根据id查找
     * @param materialId
     * @return
     */
    Material findById(String materialId);


    /**
     * 根据userId查询素材（作品）
     * @param userId
     * @return
     */
    PageResult<Material> findMaterialByUserId(String userId, String pageNum, String pageSize);

    /**
     * 编辑素材信息
     * @param material
     */
    void addMaterial(Material material);

    /**
     * 删除素材（逻辑删除）
     * @param materialId
     */
    void delete(String materialId);

    /**
     * 编辑素材
     * @param material
     */
    void editMaterial(Material material);


    /**
     * 相似推荐
     * @param id
     * @return
     */
    Map findRecommends(String id);

    /**
     * 添加访问记录
     * @param ip
     * @param id
     */
    void addVisitHistory(String ip, String id);
}
