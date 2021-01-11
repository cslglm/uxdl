package com.itheima.system.controller;

import com.itheima.material.pojo.Material;
import com.itheima.system.service.BackendMaterialService;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 虚拟数据管理
 */
@RestController
@RequestMapping("/virtual_data")
public class VirtualDataController {

    @Autowired
    private BackendMaterialService backendMaterialService;

    /**
     * 查看虚拟数据，可选参数（素材名称，模糊查询）
     * 按照更新时间排序
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result checkVirtualData(String name,String pageNum,String pageSize) {
       Result result = backendMaterialService.checkVirtualData(name,pageNum,pageSize);
       return result;
    }

    /**
     * 修改虚拟数据
     * @param id
     * @param paramMap
     * @return
     */
    @PatchMapping("/{id}")
    public Result updateVirtualData(@PathVariable("id") String id, @RequestParam Map paramMap){
       Material material = backendMaterialService.updateVirtualData(id,paramMap);
       return new Result(true, StatusCode.OK,"修改虚拟数据成功",material);
    }
}
