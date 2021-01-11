package com.itheima.material.service;

import com.itheima.tutu.pojo.Tutu;

import java.util.List;

/**
 * 轮播图
 */
public interface TutuService {

    /**
     * 查询所有
     * @return
     */
    public List<Tutu> findAll();
}
