package com.itheima.material.controller;

import com.itheima.material.service.TutuService;
import com.itheima.tutu.pojo.Tutu;

import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ids")
public class TutuController {

    @Autowired
    private TutuService tutuService;

    @GetMapping
    public Result findAll(){
        List<Tutu> list = tutuService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",list);
    }
}
