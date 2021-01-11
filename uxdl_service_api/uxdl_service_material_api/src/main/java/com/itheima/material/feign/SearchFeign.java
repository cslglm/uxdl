package com.itheima.material.feign;

import com.itheima.material.pojo.Material;
import com.itheima.uxdl.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "material")
public interface SearchFeign {

    /**
     * ES添加素材信息Feign接口
     * @param material
     * @return
     */
    @PostMapping("/search/add")
    public Result add(@RequestBody Material material);

    /**
     * ES删除素材信息Feign接口
     * @param id
     * @return
     */
    @DeleteMapping("/search/delete/{id}")
    public Result delete(@PathVariable("id") String id);

}
