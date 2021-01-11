package com.itheima.material.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.dao.*;
import com.itheima.material.feign.SearchFeign;
import com.itheima.material.pojo.*;
import com.itheima.material.service.MaterialService;
import com.itheima.material.service.NumInfoService;


import com.itheima.material.service.SearchService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private CopyRightMapper copyRightMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SearchService searchService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NumInfoService numInfoService;

    @Autowired
    private VisitHistoryMapper visitHistoryMapper;

    /**
     * 根据分类等级查询分类
     *
     * @param level
     * @return
     */
    @Override
    public List<Category> findCategoryByLevel(String level) {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("level", level).andEqualTo("status", 1);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        return categoryList;
    }

    /**
     * 根据id查询详情
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> findDetailsById(String id) {
        if (!StringUtils.isEmpty(id)) {
            Map<String, Object> detailsMap = (Map<String, Object>) redisTemplate.boundHashOps("Details").get( id);

            if (detailsMap != null && detailsMap.size() > 0) {
                return detailsMap;
            } else {
                Map<String, Object> resultMap = null;
                Material material = materialMapper.selectByPrimaryKey(id);
                if (material == null){
                    return null;
                }
                NumInfo numInfo = numInfoService.findByMaterialId(id);
                numInfoService.updateVisitNum(id);
                String jsonString = JSON.toJSONString(material);
                resultMap = JSON.parseObject(jsonString, Map.class);
                resultMap.put("author", userMapper.selectByPrimaryKey(material.getUser_id()).getName());
                resultMap.put("copyright", copyRightMapper.selectByPrimaryKey(material.getCopyright_id()));
                resultMap.put("category1Name", categoryMapper.selectByPrimaryKey(material.getCategory1_id()).getName());
                redisTemplate.boundHashOps("Details" ).putAll(resultMap);
                redisTemplate.boundHashOps("Details" ).getOperations().expire(id,20, TimeUnit.DAYS);
                resultMap.put("numInfo", numInfo);
                return resultMap;
            }
        }
        return null;
    }

    /**
     * 根据父类id查询一下所有
     *
     * @param id
     * @return
     */
    @Override
    public List<Category> findAllCategorys(String id) {
        if(id.equals("0")){
                List<Category> categoryList = new ArrayList<>();
                Example example = new Example(Category.class);
                example.createCriteria().andEqualTo("level", 1).andEqualTo("status",1);
                List<Category> parentList = categoryMapper.selectByExample(example);
                for (Category category : parentList) {
                    List<Category> categories = categoryMapper.getCategoryList(category.getId());
                    category.setCategoryList(categories);
                    categoryList.add(category);
                }
                return categoryList;
        }else {
            List<Category> categoryList = categoryMapper.getCategoryList(Integer.parseInt(id));
            return categoryList;
        }

    }


    /**
     * 根据条件查询素材
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Material> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return materialMapper.selectByExample(example);
    }

    /**
     * 根据素材Id查询版权信息
     * @param copyrightId
     * @return
     */
    @Override
    public Copyright findCopyrightByCId(Integer copyrightId) {
        Copyright copyright = copyRightMapper.selectByPrimaryKey(copyrightId);
        return copyright;

    }


    /**
     * 根据userId查询素材（作品）
     *
     * @param userId
     * @return
     */
    @Override
    public PageResult<Material> findMaterialByUserId(String userId, String pageNum, String pageSize) {
        Example example = new Example(Material.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_id", userId);
        PageHelper.startPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        Page<Material> materialInfo = (Page<Material>) materialMapper.selectByExample(example);

        return new PageResult<Material>(materialInfo.getTotal(), materialInfo.getResult());
    }

    /**
     * 新增素材信息
     *
     * @param material
     */
    @Transactional
    @Override
    public void addMaterial(Material material) {
        if (null == material.getId()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = simpleDateFormat.format(new Date());
            String ms = String.valueOf(System.currentTimeMillis());
            String id = "SC" + format + ms.substring(11);
            material.setId(id);
            material.setCreate_time(new Date());
        }
        material.setUpdata_time(new Date());
        materialMapper.insert(material);
        SetOperations set = redisTemplate.opsForSet();
        set.add(RedisConstant.MATE_PIC_DB_RESOURCES, material.getDownloadurl());
//        searchFeign.add(material);
        //numInfoFeign.add(material.getId());
//        jedisPool.getResource().sadd(RedisConstant.MATE_PIC_DB_RESOURCES,material.getDownloadurl());

    }

    /**
     * 修改素材状态为0
     *
     * @param materialId
     */
    @Transactional
    @Override
    public void delete(String materialId) {
        Material material = materialMapper.selectByPrimaryKey(materialId);
        material.setStatus(0);
        materialMapper.updateByPrimaryKeySelective(material);
//        searchFeign.delete(materialId);
        redisTemplate.delete("Details" + materialId);
    }

    /**
     * 编辑素材
     *
     * @param material
     */
    @Transactional
    @Override
    public void editMaterial(Material material) {
        material.setUpdata_time(new Date());
        materialMapper.updateByPrimaryKeySelective(material);
        Material material1 = materialMapper.selectByPrimaryKey(material.getId());
        /*searchFeign.delete(material.getId());
        searchFeign.add(material1);*/
        redisTemplate.delete("Details" + material.getId());
    }

    /**
     * 相似推荐
     *
     * @param id
     * @return
     */
    @Override
    public Map findRecommends(String id) {
        Material material = materialMapper.selectByPrimaryKey(id);
        Map searchMap = new HashMap();
        Integer cid = null;
        if (null != material.getCategory1_id()) {
            cid = material.getCategory1_id() ;
        }
        if (null != material.getCategory2_id()) {
            cid = material.getCategory2_id() ;
        }
        if (null != material.getCategory3_id()) {
            cid = material.getCategory3_id();
        }
        if (null != material.getCategory4_id()) {
            cid = material.getCategory4_id() ;
        }
        String s ="";
        if (cid != null) {
            s = String.valueOf(cid);
        }
        searchMap.put("category_id",s );
        searchMap.put("name",material.getName());
        Map map = searchService.searchMaterial(searchMap);
        return map;
    }

    /**
     * 添加访问记录
     * @param ip
     * @param id
     */
    @Transactional
    @Override
    public void addVisitHistory(String ip, String id) {
        Material material = materialMapper.selectByPrimaryKey(id);
        if (null == material){
            return;
        }
        Example example = new Example(VisitHistory.class);
        example.createCriteria().andEqualTo("user",ip).andEqualTo("material_id",id);
        VisitHistory visitHistory = visitHistoryMapper.selectOneByExample(example);
        if (null == visitHistory || "".equals(visitHistory)){
            visitHistory = new VisitHistory();
            Integer category1_id = material.getCategory1_id();
            Category category = categoryMapper.selectByPrimaryKey(category1_id);
            String categoryName = category.getName();
            if (null !=material.getCategory2_id()){
                visitHistory.setCategory_id(material.getCategory2_id());
            }else{
                visitHistory.setCategory_id(category1_id);
            }
            visitHistory.setCategory(categoryName);
            visitHistory.setUser(ip);
            visitHistory.setMaterial_id(id);
            visitHistory.setVisit_time(new Date());
            visitHistoryMapper.insert(visitHistory);
            numInfoService.updateVisitNum(id);
        }

    }


    /**
     * 根据materialId查找素材
     *
     * @param materialId
     * @return
     */
    @Override
    public Material findById(String materialId) {
        return materialMapper.selectByPrimaryKey(materialId);
    }

    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Material.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 素材id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }

            // 素材名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }

            //一级分类名称
            if (searchMap.get("category1_id") != null && !"".equals(searchMap.get("category1_id"))) {
                criteria.andLike("category1_id", "%" + searchMap.get("category1_id") + "%");
            }

            //二级分类名称
            if (searchMap.get("category2_id") != null && !"".equals(searchMap.get("category2_id"))) {
                criteria.andLike("category2_id", "%" + searchMap.get("category2_id") + "%");
            }

            //三级分类名称
            if (searchMap.get("category3_id") != null && !"".equals(searchMap.get("category3_id"))) {
                criteria.andLike("category3_id", "%" + searchMap.get("category3_id") + "%");
            }

            // userId
            if (searchMap.get("user_id") != null && !"".equals(searchMap.get("user_id"))) {
                criteria.andEqualTo("user_id", searchMap.get("user_id"));
            }


            // 素材状态 1-正常，2-下架，3-删除
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 价格（分）
            if (searchMap.get("price") != null) {
                criteria.andEqualTo("price", searchMap.get("price"));
            }

        }
        return example;
    }
}
