package com.itheima.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Tag;
import com.itheima.system.dao.CateGoryBMapper;
import com.itheima.system.dao.TagManageMapper;
import com.itheima.system.service.TagManageService;
import com.itheima.uxdl.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TagManageServiceImpl implements TagManageService {

    @Autowired
    private TagManageMapper tagManageMapper;

    @Autowired
    private CateGoryBMapper cateGoryBMapper;

    /**
     * 标签条件分页查找
     * @param searchMap
     * @return
     */
    @Override
    public PageResult findPage(Map searchMap) {
        Example example  = new Example(Tag.class);
        Example.Criteria criteria = example.createCriteria();

        if(null != searchMap.get("name") && !"".equals(searchMap.get("name"))) {
            criteria.andLike("name","%"+searchMap.get("name")+"%");
        }
        if(null != searchMap.get("id") && !"".equals(searchMap.get("id"))) {
            criteria.andEqualTo("id",searchMap.get("id"));
        }
        if(null != searchMap.get("update_admin") && !"".equals(searchMap.get("update_admin"))) {
            criteria.andEqualTo("update_admin",searchMap.get("update_admin"));
            System.out.println(searchMap.get("update_admin"));
        }
        if(null != searchMap.get("start_time") && !"".equals(searchMap.get("start_time"))) {
            criteria.andGreaterThanOrEqualTo("update_time",searchMap.get("start_time"));
        }

        if(null != searchMap.get("end_time") && !"".equals(searchMap.get("end_time"))) {
            criteria.andLessThanOrEqualTo("update_time",searchMap.get("end_time"));
        }


        Integer num = null;
        Integer size = null;

        if (StringUtils.isEmpty(searchMap.get("pageNum"))){
            num = 1;
        }else {
            num = Integer.parseInt((String) searchMap.get("pageNum"));
        }


        if (StringUtils.isEmpty(searchMap.get("pageSize"))){
            size = 10;
        }else {
            size = Integer.parseInt((String) searchMap.get("pageSize"));
            if (size>50) {
                size =50;
            }
        }

        PageHelper.startPage(num,size);
        Page<Tag> page = (Page<Tag>) tagManageMapper.selectByExample(example);
        List<Tag> tagList = page.getResult();
        List<Tag> result = new ArrayList<>();
        if (tagList!=null && tagList.size()>0) {
            for (Tag tag : tagList) {
                if (null != tag.getCategory_id() && !StringUtils.isEmpty(tag.getCategory_id())) {
                    Category category = cateGoryBMapper.findCategoryById(tag.getCategory_id());
                    tag.setCategory(category);
                }
                result.add(tag);
            }
        }

        return new PageResult(page.getTotal(),result);
    }

    /**
     * 新建标签
     * @param name
     * @param username
     * @param categoryId
     */
    @Transactional
    @Override
    public void add(String name, String username, int categoryId) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCategory_id(categoryId);
        tag.setUpdate_time(new Date());
        tag.setUpdate_admin(username);
        tagManageMapper.insert(tag);
    }

    /**
     * 删除标签
     * @param tagId
     */
    @Transactional
    @Override
    public void delete(String tagId) {
        Tag tag = tagManageMapper.selectByPrimaryKey(tagId);
        if (tag == null) {
            throw  new RuntimeException("标签不存在");
        }
        tagManageMapper.deleteByPrimaryKey(tagId);
    }

    /**
     * 编辑标签
     * @param category_id
     * @param name
     * @param id
     * @param username
     */
    @Transactional
    @Override
    public void update( String name, String id, String username,String category_id) {
        Tag tag = new Tag();
        if (name == null) {
            throw new RuntimeException("标签名称不能为空");
        }
        if (category_id !=null) {
            Category category = cateGoryBMapper.selectByPrimaryKey(category_id);
            if (category == null) {
                throw new RuntimeException("分类不存在");
            }
            tag.setCategory_id(Integer.parseInt(category_id));
        }


        tag.setName(name);
        tag.setId(Integer.parseInt(id));
        tag.setUpdate_admin(username);
        tag.setUpdate_time(new Date());
        tagManageMapper.updateByPrimaryKeySelective(tag);
    }
}
