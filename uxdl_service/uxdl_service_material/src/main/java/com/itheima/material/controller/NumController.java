package com.itheima.material.controller;

import com.itheima.material.pojo.NumInfo;
import com.itheima.material.service.NumInfoService;

import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/num")
public class NumController {

    @Autowired
    private NumInfoService numInfoService;

    @PostMapping("/importAll")
    public Result importAll(){
        numInfoService.importAll();
        return new Result(true, StatusCode.OK,"导入成功");
    }

    /**
     * 新增
     * @param materialId
     */
    @PostMapping("/add/{materialId}")
    public Result add(@PathVariable("materialId") String materialId){
        numInfoService.add(materialId);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /**
     * 删除
     * @param materialId
     */
    @DeleteMapping("/delete/{materialId}")
    public Result delete(@PathVariable("materialId")String materialId){
        numInfoService.delete(materialId);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    /**
     * 修改收藏数
     * @param materialId
     * @param index
     */
    @PutMapping("/update/collectionNum/{materialId}/{index}")
    public Result updateCollectionNum(@PathVariable("materialId")String materialId,@PathVariable("index")Integer index){
        numInfoService.updateCollectionNum(materialId,index);
        return new Result(true, StatusCode.OK,"收藏修改成功");
    }

    /**
     * 修改访问数
     * @param materialId
     */
    @PutMapping("/update/visitNum/{materialId}")
    public Result updateVisitNum(@PathVariable("materialId")String materialId){
        numInfoService.updateVisitNum(materialId);
        return new Result(true, StatusCode.OK,"修改访问数成功");
    }

    /**
     * 修改下载数
     * @param materialId
     */
    @PutMapping("/update/downloadNum/{materialId}")
    public Result updateDownloadNum(@PathVariable("materialId")String materialId){
        numInfoService.updateDownloadNum(materialId);
        return new Result(true, StatusCode.OK,"修改下载数成功");
    }

    /**
     * 根据素材Id查询
     * @param materialId
     * @return
     */
    @GetMapping("/{materialId}")
    public Result<NumInfo> findByMaterialId(@PathVariable("materialId")String materialId){
        NumInfo numInfo =  numInfoService.findByMaterialId(materialId);
        return new Result(true, StatusCode.OK,"查询成功",numInfo);
    }

    @PostMapping("/updateStatus/{materialId}/{status}")
    public void updateStatus(@PathVariable("materialId")String materialId,@PathVariable("status")String status){
        numInfoService.updateStatus(materialId,status);
    }


}
