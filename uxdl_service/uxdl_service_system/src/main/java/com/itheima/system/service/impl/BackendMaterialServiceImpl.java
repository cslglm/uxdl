package com.itheima.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.feign.NumInfoFeign;
import com.itheima.material.feign.SearchFeign;
import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Material;
import com.itheima.material.pojo.NumInfo;
import com.itheima.system.dao.CateGoryBMapper;
import com.itheima.system.dao.MaterialMapper;
import com.itheima.system.service.BackendMaterialService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class BackendMaterialServiceImpl implements BackendMaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CateGoryBMapper cateGoryBMapper;

    @Autowired
    private NumInfoFeign numInfoFeign;

    @Autowired
    private SearchFeign searchFeign;

    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public PageResult findByCondition(Map searchMap) {
        Example example = new Example(Material.class);
        Example.Criteria criteria = example.createCriteria();
        if (null != searchMap.get("key") && !"".equals(searchMap.get("key"))) {
            String key = (String) searchMap.get("key");
            if (key.length()== 18 && key.startsWith("SC")){
                criteria.andEqualTo("id", key);
            }else {
                criteria.andLike("name", "%" + key + "%");
            }
        }

        if (null != searchMap.get("status") && !"".equals(searchMap.get("status"))) {
            criteria.andEqualTo("status", searchMap.get("status"));
        }

        if (null != searchMap.get("download_num") && !"".equals(searchMap.get("download_num"))) {
            String download_num = (String) searchMap.get("download_num");
            String[] split = download_num.split("-");
            criteria.andGreaterThanOrEqualTo("download_num",split[0]);
            criteria.andLessThanOrEqualTo("download_num",split[1]);
        }

        if (null != searchMap.get("visit_num") && !"".equals(searchMap.get("visit_num"))) {
            String visit_num = (String) searchMap.get("visit_num");
            String[] split = visit_num.split("-");
            criteria.andGreaterThanOrEqualTo("visit_num",split[0]);
            criteria.andLessThanOrEqualTo("visit_num",split[1]);
        }

        if (null != searchMap.get("collection_num") && !"".equals(searchMap.get("collection_num"))) {
            String collection_num = (String) searchMap.get("collection_num");
            String[] split = collection_num.split("-");
            criteria.andGreaterThanOrEqualTo("favourite_num",split[0]);
            criteria.andLessThanOrEqualTo("favourite_num",split[1]);
        }

        if (null != searchMap.get("categoryId") && !"".equals(searchMap.get("categoryId"))) {
            Category category = cateGoryBMapper.selectByPrimaryKey(searchMap.get("categoryId"));
            Integer level = category.getLevel();
            if (level == 1) {
                criteria.andEqualTo("category1_id", searchMap.get("categoryId"));
            } else if (level == 2) {
                criteria.andEqualTo("category2_id", searchMap.get("categoryId"));
            } else {
                criteria.andEqualTo("category3_id", searchMap.get("categoryId"));
            }
        }

        if (null != searchMap.get("user_id") && !"".equals(searchMap.get("user_id"))) {
            criteria.andEqualTo("user_id", searchMap.get("user_id"));
        }

        if (null != searchMap.get("update_admin") && !"".equals(searchMap.get("update_admin"))) {
            criteria.andEqualTo("update_admin", searchMap.get("update_admin"));
        }

        if (null != searchMap.get("endTime") && !"".equals(searchMap.get("endTime"))) {
            criteria.andLessThanOrEqualTo("updata_time", searchMap.get("endTime"));
        }

        if (null != searchMap.get("startTime") && !"".equals(searchMap.get("startTime"))) {
            criteria.andGreaterThanOrEqualTo("updata_time", searchMap.get("startTime"));
        }

        Integer num = null;
        Integer size = null;
        if (StringUtils.isEmpty(searchMap.get("pageNum"))) {
            num = 1;
        } else {
            num = Integer.parseInt((String) searchMap.get("pageNum"));
        }

        if (StringUtils.isEmpty(searchMap.get("pageSize"))) {
            size = 10;
        } else {
            size = Integer.parseInt((String) searchMap.get("pageSize"));
        }

        PageHelper.startPage(num, size);

        Page<Material> pageInfo = (Page<Material>) materialMapper.selectByExample(example);

        List<Material> materialList = pageInfo.getResult();


        return new PageResult(pageInfo.getTotal(), materialList);
    }

    /**
     * 启用禁用素材
     *
     * @param id
     */
    @Transactional
    @Override
    public Result updateStatus(String id, String status) {
        Material material = new Material();
        material.setId(id);
        material.setStatus(Integer.parseInt(status));
        materialMapper.updateByPrimaryKeySelective(material);
        redisTemplate.delete("Details" + id);
        if (status.equals("0")){
            searchFeign.delete(id);
        }else {
            //添加作品
            Material materialPub = materialMapper.selectByPrimaryKey(id);
            searchFeign.add(materialPub);
        }
        return new Result(true, StatusCode.OK, "修改素材状态成功");
    }

    /**
     * 根据id查找素材
     *
     * @param id
     * @return
     */
    @Override
    public Material findOne(String id) {
        Material material = materialMapper.selectByPrimaryKey(id);
        return material;
    }

    /**
     * 查看虚拟数据，可选参数（素材名称，模糊查询）
     * 按照更新时间排序
     *
     * @param key
     * @return
     */
    @Override
    public Result checkVirtualData(String key, String pageNum, String pageSize) {
        Example example = new Example(Material.class);
        if (key != null) {
            example.createCriteria().andLike("name", "%" + key + "%");
        }

        Integer num = null;
        Integer size = null;
        if (StringUtils.isEmpty(pageNum)) {
            num = 1;
        } else {
            num = Integer.parseInt(pageNum);
        }

        if (StringUtils.isEmpty(pageSize)) {
            size = 10;
        } else {
            size = Integer.parseInt(pageSize);
        }
        example.orderBy("updata_time").desc();
        PageHelper.startPage(num, size);
        Page<Material> page = (Page<Material>) materialMapper.selectByExample(example);
        return new Result(true, StatusCode.OK, "获取素材数据成功", new PageResult(page.getTotal(), page.getResult()));
    }

    /**
     * 修改虚拟数据
     *
     * @param id
     * @param paramMap
     * @return
     */
    @Transactional
    @Override
    public Material updateVirtualData(String id, Map paramMap) {
        Material material = new Material();
        material.setId(id);
        if (paramMap.get("visit_num")!=null && !"".equals(paramMap.get("visit_num"))){
            material.setBasic_visit_num(Integer.parseInt((String) paramMap.get("visit_num")));
        }
        if (paramMap.get("download_num")!=null && !"".equals(paramMap.get("download_num"))){
            material.setBasic_download_num(Integer.parseInt((String) paramMap.get("download_num")));
        }
        if (paramMap.get("collection_num")!=null && !"".equals(paramMap.get("collection_num"))){
            material.setBasic_favourite_num(Integer.parseInt((String) paramMap.get("collection_num")));
        }
        materialMapper.updateByPrimaryKeySelective(material);
        Material updateMaterial = materialMapper.selectByPrimaryKey(id);
        searchFeign.delete(id);
        searchFeign.add(updateMaterial);

        return updateMaterial;
    }

    /**
     * 删除素材
     *
     * @param id
     */
    @Transactional
    @Override
    public void delete(String id) {
        Material material = materialMapper.selectByPrimaryKey(id);
        materialMapper.deleteByPrimaryKey(id);
        searchFeign.delete(id);
        SetOperations set = redisTemplate.opsForSet();
        set.remove(RedisConstant.MATE_PIC_DB_RESOURCES, material.getName());
        redisTemplate.delete("Details" + id);
    }
}
