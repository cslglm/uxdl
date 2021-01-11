package com.itheima.material.controller;


import com.itheima.material.pojo.Material;
import com.itheima.material.service.ESManagerService;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
public class ESManagerController {

    @Autowired
    private ESManagerService esManagerService;

    @GetMapping("/create")
    public Result createIndexAndMapping() {
        esManagerService.createIndexAndMapping();
        return new Result(true, StatusCode.OK,"创建索引库成功");
    }

    @PostMapping("/importAll")
    public Result importAll() {
        esManagerService.importAll();
        return new Result(true,StatusCode.OK,"导入索引库成功");
    }

    @PostMapping("/add")
    public Result add(@RequestBody Material material) {
        esManagerService.importDataByMaterialId(material);
        System.out.println("ES增加素材成功");
        return new Result(true,StatusCode.OK,"增加素材成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String id) {
        esManagerService.delDataByMaterialId(id);
        return new Result(true,StatusCode.OK,"删除素材成功");
    }
}
