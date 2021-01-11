package com.itheima.system.controller;

import com.itheima.material.feign.SearchFeign;
import com.itheima.material.pojo.Material;
import com.itheima.system.service.BackendMaterialService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/materials")
public class BackendMaterialController {

    @Autowired
    private BackendMaterialService backendMaterialService;

    @Autowired
    private SearchFeign searchFeign;

    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @GetMapping
    public Result findByCondition(@RequestParam Map searchMap) {
        PageResult pageResult = backendMaterialService.findByCondition(searchMap);
        return new Result(true, StatusCode.OK,"查询素材成功",pageResult);
    }

    /**
     * 删除素材
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        backendMaterialService.delete(id);
        return new Result(true,StatusCode.OK,"删除素材成功");
    }

    /**
     * 启用禁用素材
     * @param id
     * @return
     */
    @PatchMapping("/{id}")
    public Result updateStatus(@PathVariable("id") String id,String status) {
       Result updateResult = backendMaterialService.updateStatus(id,status);
        return updateResult;
    }

    /**
     * 查看某一个素材详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result check(@PathVariable("id")String id) {
       Material material =  backendMaterialService.findOne(id);
       return new Result(true,StatusCode.OK,"查询素材成功",material);
    }



}
