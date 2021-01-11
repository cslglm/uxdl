package com.itheima.material.service.impl;


import com.itheima.material.dao.TutuMapper;
import com.itheima.material.service.TutuService;
import com.itheima.tutu.pojo.Tutu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class TutuServiceImpl implements TutuService {

    @Autowired
    private TutuMapper tutuMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Tutu> findAll() {
        List<Tutu> adss = (List<Tutu>) redisTemplate.boundValueOps("adss").get();
        if (adss != null && adss.size() > 0){
            return adss;
        }else {
            Example example = new Example(Tutu.class);
            Example.Criteria criteria = example.createCriteria();
        /*criteria.andGreaterThanOrEqualTo("end_time",new Date());
        criteria.andLessThanOrEqualTo("start_time",new Date());*/
            criteria.andEqualTo("status",1);
            List<Tutu> tutus = tutuMapper.selectByExample(example);
            redisTemplate.boundValueOps("adss").set(tutus);
            return tutus;
        }
    }


}
