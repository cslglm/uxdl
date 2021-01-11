package com.itheima.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.system.dao.AdminMapper;
import com.itheima.system.pojo.SysAdmin;
import com.itheima.system.service.AdminService;
import com.itheima.uxdl.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;



    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public PageResult findByCondition(Map searchMap) {
        Example example = new Example(SysAdmin.class);

        Example.Criteria criteria = example.createCriteria();

        if (searchMap.get("username") != null){
            criteria.andLike("username","%"+searchMap.get("username")+"%");
        }

        if (searchMap.get("phone") != null){
            criteria.andEqualTo("phone",searchMap.get("phone"));
        }
        if (searchMap.get("status") != null){
            criteria.andEqualTo("status",Integer.parseInt((String) searchMap.get("status")));
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
        }

        PageHelper.startPage(num,size);

        Page<SysAdmin> sysAdmins = (Page<SysAdmin>) adminMapper.selectByExample(example);

        return new PageResult(sysAdmins.getTotal(),sysAdmins.getResult());
    }

    /**
     * 切换用户状态
     * @param id
     */
    @Transactional
    @Override
    public void updateStatus(String id) {
        SysAdmin sysAdmin = adminMapper.selectByPrimaryKey(id);
        Integer status = sysAdmin.getStatus();
        if (status != 1){
            sysAdmin.setStatus(1);
        }else {
            sysAdmin.setStatus(0);
        }
        adminMapper.updateByPrimaryKey(sysAdmin);
    }

    /**
     * 新增管理员
     * @param sysAdmin
     */
    @Transactional
    @Override
    public void addAdmin(SysAdmin sysAdmin) {
        String pwd = new BCryptPasswordEncoder().encode(sysAdmin.getPassword());
        sysAdmin.setPassword(pwd);
        adminMapper.insert(sysAdmin);
    }

    /**
     * 编辑用户数据回显（根据id查询用户）
     * @param id
     * @return
     */
    @Override
    public SysAdmin findById(String id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    /**
     * 编辑用户
     * @param sysAdmin
     */
    @Transactional
    @Override
    public void update(SysAdmin sysAdmin) {
        adminMapper.updateByPrimaryKeySelective(sysAdmin);
    }

    /**
     * 删除管理员
     * @param id
     */
    @Transactional
    @Override
    public void delAdmin(String id) {
        adminMapper.deleteByPrimaryKey(id);
    }
}
