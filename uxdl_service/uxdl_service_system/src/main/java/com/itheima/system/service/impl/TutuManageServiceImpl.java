package com.itheima.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.pojo.Tag;
import com.itheima.system.dao.TutuManageMapper;
import com.itheima.system.service.TutuManageService;
import com.itheima.tutu.pojo.Tutu;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.DateUtils;
import com.itheima.uxdl.util.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TutuManageServiceImpl implements TutuManageService {

    @Autowired
    private TutuManageMapper tutuMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 条件分页查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public PageResult findPage(Map searchMap) {
        try {
            Example example = new Example(Tutu.class);
            Example.Criteria criteria = example.createCriteria();
            if (null != searchMap.get("name") && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            if (null != searchMap.get("status") && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }
            if (null != searchMap.get("update_admin") && !"".equals(searchMap.get("update_admin"))) {
                criteria.andEqualTo("update_admin", searchMap.get("update_admin"));
            }
            if (null != searchMap.get("start_time") && !"".equals(searchMap.get("start_time"))) {
                criteria.andGreaterThanOrEqualTo("update_time",searchMap.get("start_time"));
            }

            if (null != searchMap.get("end_time") && !"".equals(searchMap.get("end_time"))) {
                criteria.andLessThanOrEqualTo("update_time", searchMap.get("end_time"));
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
            Page<Tutu> page = (Page<Tutu>) tutuMapper.selectByExample(example);
            List<Tutu> result = page.getResult();
            if ( result== null || result.size()==0) {
                Tutu tutu = new Tutu();
                tutu.setId(10086);
                result.add(tutu);
            }
            return new PageResult(page.getTotal(), result);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 新增轮播图
     *
     * @param tutu
     */
    @Transactional
    @Override
    public Result add(Tutu tutu) {
        if (null != tutu.getName()) {
            Example example = new Example(Tutu.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("name", tutu.getName());
            int i = tutuMapper.selectCountByExample(example);
            if (i == 1) {
                return new Result(false, StatusCode.ERROR, "此轮播图名称已存在，请重新填写名称");
            }

            tutu.setUpdate_time(new Date());
            tutuMapper.insert(tutu);
            Tutu newTutu = tutuMapper.selectOneByExample(example);
            SetOperations set = redisTemplate.opsForSet();
            set.add(RedisConstant.MATE_PIC_DB_RESOURCES, tutu.getUrl());
            return new Result(true, StatusCode.OK, "Ok", newTutu);
        }
        redisTemplate.delete("adss");
        return new Result(false, StatusCode.ERROR, "轮播图名称不能为空");
    }

    /**
     * 删除轮播图
     *
     * @param tutuId
     */
    @Transactional
    @Override
    public void delete(String tutuId) {
        Tutu tutu = tutuMapper.selectByPrimaryKey(tutuId);
        if (null != tutu) {
            tutuMapper.deleteByPrimaryKey(tutuId);
            SetOperations set = redisTemplate.opsForSet();
            set.remove(RedisConstant.MATE_PIC_DB_RESOURCES,tutu.getName());
            QiniuUtils.deleteFileFromQiniu(tutu.getName());
        }
        redisTemplate.delete("adss");
    }

    /**
     * 编辑轮播图
     *
     * @param tutu
     */
    @Transactional
    @Override
    public Result update(Tutu tutu, String id) {
        String oldName = null;
        if (null != tutu.getUrl()) {
            Tutu oldTutu = tutuMapper.selectByPrimaryKey(id);
            oldName = oldTutu.getName();

        }
        tutu.setUpdate_time(new Date());
        tutu.setId(Integer.parseInt(id));
        tutuMapper.updateByPrimaryKeySelective(tutu);
        Tutu updateTutu = tutuMapper.selectByPrimaryKey(id);
        if (oldName != null) {
            QiniuUtils.deleteFileFromQiniu(oldName);
            SetOperations set = redisTemplate.opsForSet();
            set.remove(RedisConstant.MATE_PIC_DB_RESOURCES, oldName);
            set.add(RedisConstant.MATE_PIC_DB_RESOURCES, tutu.getName());
        }
        redisTemplate.delete("adss");
        return new Result(true, StatusCode.OK, "Ok", updateTutu);

    }

    /**
     * 修改轮播图权重值和跳转链接
     *
     * @param id
     * @param seq
     * @return
     */
    @Transactional
    @Override
    public Result updateWeight(String id, String seq, String link_url,String status) {
        Tutu tutu = new Tutu();
        tutu.setId(Integer.parseInt(id));
        if (null != seq) {
            tutu.setSeq(Integer.parseInt(seq));
        }
        if (null != link_url) {
            tutu.setLink_url(link_url);
        }
        if (null != status) {
            tutu.setStatus(Integer.parseInt(status));
        }
        tutuMapper.updateByPrimaryKeySelective(tutu);
        Tutu updateTutu = tutuMapper.selectByPrimaryKey(id);
        redisTemplate.delete("adss");
        return new Result(true, StatusCode.OK, "修改广告信息成功", updateTutu);
    }


}
