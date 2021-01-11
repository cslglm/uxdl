package com.itheima.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.pojo.Material;
import com.itheima.system.dao.MaterialMapper;
import com.itheima.system.dao.UserMapper;
import com.itheima.system.service.UserService;
import com.itheima.user.pojo.TbUser;
import com.itheima.uxdl.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MaterialMapper materialMapper;

    /**
     * 根据条件查询用户
     *
     * @param searchMap
     * @return
     */
    @Override
    public PageResult findUserByCondition(Map searchMap) {


        Example example = new Example(TbUser.class);

        Example.Criteria criteria = example.createCriteria();
        if (searchMap.get("socails") != null) {
            criteria.orEqualTo("phone", searchMap.get("socails"))
                    .orEqualTo("qq", searchMap.get("socails"))
                    .orEqualTo("weixin", searchMap.get("socails"));
        }else{
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }


            if (searchMap.get("name") != null) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }

            if (searchMap.get("status") != null ) {
                if (searchMap.get("status").equals("1") || searchMap.get("status").equals("0")){
                    criteria.andEqualTo("status", searchMap.get("status"));
                }
            }

            if (searchMap.get("update_admin") != null) {
                criteria.andLike("update_admin", "%" + searchMap.get("update_admin") + "%");
            }

            if (searchMap.get("statrtTime") != null) {
                criteria.andGreaterThanOrEqualTo("create_time", searchMap.get("statrtTime"));
            }

            if (searchMap.get("endTime") != null) {
                criteria.andLessThanOrEqualTo("create_time", searchMap.get("endTime"));
            }
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
            if (Integer.parseInt((String) searchMap.get("pageSize")) > 50) {
                size = 50;
            } else {
                size = Integer.parseInt((String) searchMap.get("pageSize"));
            }
        }

        PageHelper.startPage(num, size);

        Page<TbUser> tbUsers = (Page<TbUser>) userMapper.selectByExample(example);

        return new PageResult(tbUsers.getTotal(), tbUsers.getResult());
    }

    /**
     * 切换用户状态
     *
     * @param userId
     */
    @Transactional
    @Override
    public void updateStatus(String userId, String adminName) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userId);
        Integer status = tbUser.getStatus();
        if (status != 1) {
            tbUser.setStatus(1);
        } else {
            tbUser.setStatus(0);
        }
        tbUser.setUpdate_admin(adminName);
        tbUser.setUpdate_time(new Date());
        userMapper.updateByPrimaryKeySelective(tbUser);
    }

    /**
     * 根据用户id查询用户相关数据
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> findUserDate(String userId, String pageNum, String pageSize) {
        Map<String, Object> resultMap = new HashMap<>();
        TbUser tbUser = userMapper.selectByPrimaryKey(userId);
        if (tbUser != null) {
            resultMap.put("user", tbUser);
        }
        Example example = new Example(Material.class);
        example.createCriteria().andEqualTo("user_id", userId);
        Integer num = null;
        if (StringUtils.isEmpty(pageNum)) {
            num = 1;
        } else {
            num = Integer.parseInt(pageNum);
        }
        Integer size = null;
        if (StringUtils.isEmpty(pageSize)) {
            size = 20;
        } else {
            size = Integer.parseInt(pageSize);
        }
        PageHelper.startPage(num, size);
        Page<Material> pageInfo = (Page<Material>) materialMapper.selectByExample(example);
        PageResult materialList = new PageResult(pageInfo.getTotal(), pageInfo.getResult());
        if (materialList != null) {
            resultMap.put("userMeterial", materialList);
        } else {
            resultMap.put("userMeterial", null);
        }
        return resultMap;
    }


}
