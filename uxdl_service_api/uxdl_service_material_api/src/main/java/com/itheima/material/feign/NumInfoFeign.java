package com.itheima.material.feign;


import com.itheima.material.pojo.NumInfo;
import com.itheima.uxdl.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "MATERIAL")
public interface NumInfoFeign {

    /**
     * 新增
     * @param materialId
     */
    @PostMapping("/num/add/{materialId}")
    public Result add(@PathVariable("materialId") String materialId);

    /**
     * 删除
     * @param materialId
     */
    @DeleteMapping("/num/delete/{materialId}")
    public Result delete(@PathVariable("materialId")String materialId);

    /**
     * 修改收藏数
     * @param materialId
     * @param index
     */
    @PutMapping("/num/update/collectionNum/{materialId}/{index}")
    public Result updateCollectionNum(@PathVariable("materialId")String materialId,@PathVariable("index")Integer index);

    /**
     * 修改访问数
     * @param materialId
     */
    @PutMapping("/num/update/visitNum/{materialId}")
    public Result updateVisitNum(@PathVariable("materialId")String materialId);

    /**
     * 修改下载数
     * @param materialId
     */
    @PutMapping("/num/update/downloadNum/{materialId}")
    public Result updateDownloadNum(@PathVariable("materialId")String materialId);

    /**
     * 根据素材Id查询
     * @param materialId
     * @return
     */
    @GetMapping("/num/{materialId}")
    public Result<NumInfo> findByMaterialId(@PathVariable("materialId")String materialId);

    /**
     * 修改状态
     * @param materialId
     */
    @PostMapping("/updateStatus/{materialId}/{status}")
    public void updateStatus(@PathVariable("materialId")String materialId,@PathVariable("status")String status);
}
