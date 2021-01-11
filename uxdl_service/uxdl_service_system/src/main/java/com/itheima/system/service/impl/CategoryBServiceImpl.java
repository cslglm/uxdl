package com.itheima.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.feign.SearchFeign;
import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Material;
import com.itheima.system.dao.CateGoryBMapper;
import com.itheima.system.dao.MaterialMapper;
import com.itheima.system.service.CategoryBService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class CategoryBServiceImpl implements CategoryBService {

    @Autowired
    private CateGoryBMapper cateGoryBMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private SearchFeign searchFeign;

    /**
     * 获取所有分类
     *
     * @param num
     * @param size
     * @return
     */
    @Override
    public PageResult findAllCategory(Integer num, Integer size, String condition) {
        if (StringUtils.isEmpty(condition)) {
            PageHelper.startPage(num, size);
            Page<Category> pageInfo = cateGoryBMapper.getCategoryList();
            return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
        } else {
            Example example = new Example(Category.class);
            example.createCriteria().orLike("name", "%" + condition + "%").orEqualTo("level", condition);
            List<Category> categories = cateGoryBMapper.selectByExample(example);
            if (categories == null || categories.size() == 0) {
                return null;
            }
            List<Category> categoryList = new ArrayList<>();
            for (Category category : categories) {
                Category result = cateGoryBMapper.findCategoryById(category.getId());
                categoryList.add(result);
            }
            return new PageResult(Long.parseLong(String.valueOf(categoryList.size())), categoryList);
        }
    }

    /**
     * 根据id查找菜单(方法处理繁琐，研究一下递归)
     *
     * @param id
     * @return
     */
    @Override
    public Category findCategoryById(String id) {
        try {
            Integer.parseInt(id);
        } catch (Exception e) {
            throw new RuntimeException("id不合法");
        }
        Category category = cateGoryBMapper.selectByPrimaryKey(id);
        Category result = null;
        switch (category.getLevel()) {
            case 1:
                result = category;
                break;
            case 2:
                Category category21 = cateGoryBMapper.selectByPrimaryKey(category.getParent_id());
                List<Category> categoryList = new ArrayList<>();
                categoryList.add(category);
                category21.setCategoryList(categoryList);
                result = category21;
                break;
            case 3:
                Category category32 = cateGoryBMapper.selectByPrimaryKey(category.getParent_id());
                Category category31 = cateGoryBMapper.selectByPrimaryKey(category32.getParent_id());
                List<Category> categoryList32 = new ArrayList<>();
                categoryList32.add(category);
                List<Category> categoryList31 = new ArrayList<>();
                categoryList31.add(category32);
                category32.setCategoryList(categoryList32);
                category31.setCategoryList(categoryList31);
                result = category31;
                break;
            default:
                Category category43 = cateGoryBMapper.selectByPrimaryKey(category.getParent_id());
                Category category42 = cateGoryBMapper.selectByPrimaryKey(category43.getParent_id());
                Category category41 = cateGoryBMapper.selectByPrimaryKey(category42.getParent_id());
                List<Category> categoryList43 = new ArrayList<>();
                categoryList43.add(category);
                List<Category> categoryList42 = new ArrayList<>();
                categoryList42.add(category43);
                List<Category> categoryList41 = new ArrayList<>();
                categoryList41.add(category42);
                category43.setCategoryList(categoryList43);
                category42.setCategoryList(categoryList42);
                category41.setCategoryList(categoryList41);
                result = category41;
                break;
        }
        return result;
    }

    /**
     * 新增一级分类
     *
     * @param categoryNames
     * @param status
     */
    @Override
    @Transactional
    public void addCategoryLevel1(String[] categoryNames, String status, String username) {
        if (!status.equals("0") && !status.equals("1")) {
            status = "1";
        }
        Integer parentId = null;
        Category result = null;
        for (int i = 0; i < categoryNames.length; i++) {
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("name", categoryNames[i]);
            result = cateGoryBMapper.selectOneByExample(example);
            if (result == null) {
                String categoryName = categoryNames[i];
                Category category = new Category();
                category.setName(categoryName);
                category.setUpdate_time(new Date());
                category.setStatus(Integer.parseInt(status));
                category.setLevel(i + 1);
                if (parentId!=null) {
                    category.setParent_id(parentId);
                }
                category.setUpdate_admin(username);
                category.setStatus(1);
                cateGoryBMapper.insertCategory(category);
                if (i < categoryNames.length - 1) {
                    parentId = category.getId();
                }
            } else {
                parentId = result.getId();
            }

        }
    }

    /**
     * 编辑分类
     *
     * @param parent_id
     * @param name
     * @param categoryId
     * @param status
     * @param username
     */
    @Transactional
    @Override
    public void updateCateGory(String name, String categoryId, String status, String username, String parent_id) {

        Category category = new Category();
        try {
            Integer.parseInt(categoryId);
        } catch (Exception e) {
            throw new RuntimeException("id不合法");
        }
        category.setId(Integer.parseInt(categoryId));
        category.setName(name);
        try {
            category.setParent_id(Integer.parseInt(parent_id));
        } catch (Exception e) {
            category.setStatus(Integer.parseInt(status));
            category.setUpdate_admin(username);
            category.setUpdate_time(new Date());
            cateGoryBMapper.updateByPrimaryKeySelective(category);
        }
        category.setStatus(Integer.parseInt(status));
        category.setUpdate_admin(username);
        category.setUpdate_time(new Date());
        cateGoryBMapper.updateByPrimaryKeySelective(category);
    }

    /**
     * 菜单状态切换
     *
     * @param id
     */
    @Transactional
    @Override
    public Result updateStatus(String id, String username, String status) {
        try {
            Integer.parseInt(id);
        } catch (Exception e) {
            throw new RuntimeException("id不合法");
        }
        Result result = null;
        if (status.equals("1")) {
            //启用
            Category category = cateGoryBMapper.selectByPrimaryKey(id);
            category.setStatus(1);
            category.setUpdate_time(new Date());
            category.setUpdate_admin(username);
            switch (category.getLevel()) {
                case 1:
                    cateGoryBMapper.updateByPrimaryKeySelective(category);
                    //需要添加ES相对应数据
                    this.addSearchMaterial(1, id);
                    this.enableMate(category);
                    result = new Result(true, StatusCode.OK, "分类启用成功");
                    break;
                case 2:
                    Category category21 = cateGoryBMapper.selectByPrimaryKey(category.getParent_id());
                    if (category21.getStatus() == 0) {
                        result = new Result(false, StatusCode.ERROR, "父分类未启用，不能启用子分类");
                    } else {
                        cateGoryBMapper.updateByPrimaryKeySelective(category);
                        //需要添加ES相对应数据
                        this.addSearchMaterial(2, id);
                        this.enableMate(category);
                        result = new Result(true, StatusCode.OK, "分类启用成功");
                    }
                    break;
                case 3:
                    Category category32 = cateGoryBMapper.selectByPrimaryKey(category.getParent_id());
                    Category category31 = cateGoryBMapper.selectByPrimaryKey(category32.getParent_id());
                    if (category31.getStatus() == 0 || category32.getStatus() == 0) {
                        result = new Result(false, StatusCode.ERROR, "父分类未启用，不能启用子分类");
                    } else {
                        cateGoryBMapper.updateByPrimaryKeySelective(category);
                        //需要添加ES相对应数据
                        this.addSearchMaterial(3, id);
                        this.enableMate(category);
                        result = new Result(true, StatusCode.OK, "分类启用成功");
                    }
                    break;
                case 4:
                    Category category43 = cateGoryBMapper.selectByPrimaryKey(category.getParent_id());
                    Category category42 = cateGoryBMapper.selectByPrimaryKey(category43.getParent_id());
                    Category category41 = cateGoryBMapper.selectByPrimaryKey(category42.getParent_id());
                    if (category43.getStatus() == 0 || category42.getStatus() == 0 || category41.getStatus() == 0) {
                        result = new Result(false, StatusCode.ERROR, "父分类未启用，不能启用子分类");
                    } else {
                        cateGoryBMapper.updateByPrimaryKeySelective(category);
                        //需要添加ES相对应数据
                        this.addSearchMaterial(4, id);
                        this.enableMate(category);
                        result = new Result(true, StatusCode.OK, "分类启用成功");
                    }
                    break;
            }
            redisTemplate.delete("allList");
            return result;
        } else {
            //禁用
//           Category category = cateGoryBMapper.selectByPrimaryKey(id);
//           category.setStatus(0);
            Category category = cateGoryBMapper.findCategoryById(Integer.parseInt(id));
            category.setStatus(0);
            category.setUpdate_time(new Date());
            category.setUpdate_admin(username);
            cateGoryBMapper.updateByPrimaryKeySelective(category);
            List<Category> categoryList2 = category.getCategoryList();
            if (categoryList2 != null && categoryList2.size() > 0) {
                for (Category category2 : categoryList2) {
                    category2.setStatus(0);
                    category2.setUpdate_time(new Date());
                    category2.setUpdate_admin(username);
                    cateGoryBMapper.updateByPrimaryKeySelective(category2);
                    List<Category> categoryList3 = category2.getCategoryList();
                    if (categoryList3 != null && categoryList3.size() > 0) {
                        for (Category category3 : categoryList3) {
                            category3.setStatus(0);
                            category3.setUpdate_time(new Date());
                            category3.setUpdate_admin(username);
                            cateGoryBMapper.updateByPrimaryKeySelective(category3);
                            List<Category> categoryList4 = category3.getCategoryList();
                            if (categoryList4 != null && categoryList4.size() > 0) {
                                for (Category category4 : categoryList4) {
                                    category4.setStatus(0);
                                    category4.setUpdate_time(new Date());
                                    category4.setUpdate_admin(username);
                                    cateGoryBMapper.updateByPrimaryKeySelective(category4);
                                }
                            }
                        }
                    }
                }
            }
            //需要删除ES相对应数据
            Integer level = category.getLevel();
            this.deleteSearchMaterial(level, id);
            this.enableMate(category);
            redisTemplate.delete("allList");
            return new Result(true, StatusCode.OK, "分类禁用成功");
        }
    }

    public void deleteSearchMaterial(int level, String id) {
        Example example = new Example(Material.class);
        example.createCriteria().andEqualTo("category" + level + "_id", id);
        List<Material> materials = materialMapper.selectByExample(example);
        for (Material material : materials) {
            searchFeign.delete(material.getId());
        }
    }

    public void addSearchMaterial(int level, String id) {
        Example example = new Example(Material.class);
        example.createCriteria().andEqualTo("category" + level + "_id", id);
        if (level != 4) {
            level += 1;
            example.createCriteria().andEqualTo("category" + level + "_id", null);
        }
        List<Material> materials = materialMapper.selectByExample(example);
        for (Material material : materials) {
            searchFeign.add(material);
        }
    }

    /**
     * 启用/禁用素材
     *
     * @param category
     */
    private void enableMate(Category category) {
        Material material1 = new Material();
        material1.setStatus(category.getStatus());
        Example example = new Example(Material.class);
        switch (category.getLevel()) {
            case 1:
                example.createCriteria().andEqualTo("category1_id", category.getId());
                break;
            case 2:
                example.createCriteria().andEqualTo("category2_id", category.getId());
                break;
            case 3:
                example.createCriteria().andEqualTo("category3_id", category.getId());
                break;
            case 4:
                example.createCriteria().andEqualTo("category4_id", category.getId());
                break;
        }
        materialMapper.updateByExampleSelective(material1, example);
    }

    /**
     * 删除分类
     *
     * @param id
     */
    @Transactional
    @Override
    public void delCategory(String id) {
        try {
            Integer.parseInt(id);
        } catch (Exception e) {
            throw new RuntimeException("id不合法");
        }
        Example example = new Example(Material.class);
        example.createCriteria().orEqualTo("category1_id", id).
                orEqualTo("category2_id", id).
                orEqualTo("category3_id", id).
                orEqualTo("category4_id", id);
        int i = materialMapper.selectCountByExample(example);
        if (i == 0) {
            return;
        }
        Category category = cateGoryBMapper.findCategoryById(Integer.parseInt(id));
        List<Category> categoryList2 = category.getCategoryList();
        if (categoryList2 != null && categoryList2.size() > 0) {
            for (Category category2 : categoryList2) {
                List<Category> categoryLis3 = category2.getCategoryList();
                if (categoryLis3 != null && categoryLis3.size() > 0) {
                    for (Category category3 : categoryLis3) {
                        List<Category> categoryList4 = category3.getCategoryList();
                        if (categoryList4 != null && categoryList4.size() > 0) {
                            for (Category category4 : categoryList4) {
                                cateGoryBMapper.deleteByPrimaryKey(category4.getId());
                            }
                        }
                        cateGoryBMapper.deleteByPrimaryKey(category3.getId());
                    }
                }
                cateGoryBMapper.deleteByPrimaryKey(category2.getId());
            }
            cateGoryBMapper.deleteByPrimaryKey(category.getId());
        }
        //需要删除ES相对应数据
        this.delMate(category);
        this.deleteSearchMaterial(category.getLevel(), id);
        redisTemplate.delete("allList");
    }

    /**
     * 根据分类等级查询分类信息
     *
     * @param level
     * @return
     */
    @Override
    public List<Category> findByLevel(String level) {
        try {
            Integer.parseInt(level);
        } catch (Exception e) {
            throw new RuntimeException("level不合法");
        }
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("level", level);
        List<Category> categories = cateGoryBMapper.selectByExample(example);
        if (categories == null || categories.size() == 0) {
            return null;
        }
        List<Category> categoryList = new ArrayList<>();
        for (Category category : categories) {
            Category result = cateGoryBMapper.findCategoryById(category.getId());
            categoryList.add(result);
        }
        return categoryList;
    }

    /**
     * 逻辑删除素材
     *
     * @param category
     */
    private void delMate(Category category) {
        Material material1 = new Material();
        material1.setIsdel(1);
        Example example = new Example(Material.class);
        switch (category.getLevel()) {
            case 1:
                example.createCriteria().andEqualTo("category1_id", category.getId());
                break;
            case 2:
                example.createCriteria().andEqualTo("category2_id", category.getId());
                break;
            case 3:
                example.createCriteria().andEqualTo("category3_id", category.getId());
                break;
            case 4:
                example.createCriteria().andEqualTo("category4_id", category.getId());
                break;
        }
        materialMapper.updateByExampleSelective(material1, example);
    }

}
