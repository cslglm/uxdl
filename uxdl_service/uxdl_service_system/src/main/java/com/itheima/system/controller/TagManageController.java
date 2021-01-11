package com.itheima.system.controller;


import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Tag;
import com.itheima.system.config.TokenDecode;
import com.itheima.system.service.CategoryBService;
import com.itheima.system.service.TagManageService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tags")
public class TagManageController {

    @Autowired
    private TagManageService tagManageService;

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    private CategoryBService categoryBService;

    /**
     * 标签条件分页查询
     * @param searchMap
     * @return
     */
    @GetMapping
    public Result findPage(@RequestParam Map searchMap) {
        PageResult pageResult = tagManageService.findPage(searchMap);
        return new Result(true, StatusCode.OK,"查询标签成功", pageResult);
    }

    /**
     * 新建标签
     * @param name
     * @return
     */
    @PostMapping
    public Result add(String name,Integer category_id) {
        String username = this.getAdminName();
        if (username == null){
            return new Result(false,StatusCode.ERROR,"获取管理员身份失败");
        }
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(category_id)) {
            return new Result(false,StatusCode.ERROR,"标签分类或名称不能为空");
        }
        tagManageService.add(name,username,category_id);
        return new Result(true,StatusCode.OK,"新增标签成功");
    }

    /**
     * 删除标签
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id) {
        tagManageService.delete(id);
        return new Result(true,StatusCode.OK,"删除标签成功");
    }


    /**
     * 根据等级查询分类信息
     * @param level
     * @return
     */
    @GetMapping("/category_level/{level}")
    public Result findCategory(@PathVariable("level") String level) {
       List<Category> categoryList = categoryBService.findByLevel(level);
       return new Result(true,StatusCode.OK,"查询分类信息成功",categoryList);
    }


    /**
     * 编辑标签
     * @param name
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Result update(String category_id,String name,@PathVariable("id") String id) {
        String username = getAdminName();
        if (username == null){
            return new Result(false,StatusCode.ERROR,"获取管理员身份失败");
        }
        tagManageService.update(name,id,username,category_id);
        return new Result(true,StatusCode.OK,"编辑标签成功");
    }

    /**
     * 获取管理员名称
     * @return
     */
    public String getAdminName(){
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return null;
        }
        String username = userInfo.get("username");
        return username;
    }
}
