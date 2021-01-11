package com.itheima.system.controller;



import com.itheima.material.pojo.Category;
import com.itheima.system.config.TokenDecode;
import com.itheima.system.service.CategoryBService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/categorys")
public class CategoryBController {

    @Autowired
    private CategoryBService categoryBService;

    @Autowired
    private TokenDecode tokenDecode;



    /**
     * 查询所有分类
     * @return
     */
    @GetMapping
    public Result findAllCategorys(String pageNum,String pageSize,String condition){
        Integer num = null;
        Integer size = null;
        if (StringUtils.isEmpty(pageNum)){
            num = 1;
        }else {
            num = Integer.parseInt(pageNum);
        }

        if (StringUtils.isEmpty(pageNum)){
            size = 20;
        }else {
            size = Integer.parseInt(pageSize);
        }
        PageResult pageResult = categoryBService.findAllCategory(num,size,condition);
        return new Result(true,StatusCode.OK,"获取分类成功",pageResult);
    }

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findCategoryById(@PathVariable("id") String id){
        if (StringUtils.isEmpty(id)){
            return new Result(false,StatusCode.ERROR,"请输入Id");
        }
        Category category = categoryBService.findCategoryById(id);
        return new Result(true,StatusCode.OK,"分类查询成功",category);
    }

    /**
     * 新增分类
     * @param names
     * @param status
     * @return
     */
    @PostMapping
    public Result addCategoryLevel1(String[] names,String status){
        if (names == null || names.length == 0){
            return new Result(false,StatusCode.ERROR,"新增分类失败");
        }
        if (StringUtils.isEmpty(status)){
            status = "1";
        }
        if (names.length>1) {
            for (int i = 0; i <names.length; i++) {
                if (names[i].length()>10) {
                    return new Result(false,StatusCode.ERROR,"分类名称不能超过十个字符");
                }
            }
        }else if (names.length ==1) {
            if (names[0].length()>10) {
                return new Result(false,StatusCode.ERROR,"分类名称不能超过十个字符");
            }
        }
        String username = getAdminName();

        categoryBService.addCategoryLevel1(names,status,username);
        return new Result(true,StatusCode.OK,"新增分类成功");
    }

    /**
     * 编辑分类
     * @param name
     * @param categoryId
     * @param status
     * @return
     */
    @PutMapping("/{categoryId}")
    public Result updateCategory(String name,@PathVariable("categoryId") String categoryId,String status,String parent_id){
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(categoryId) ||name.length()>10){
            return new Result(true, StatusCode.ERROR, "分类名称或Id不合法");
        }
        if (!status.equals("0") && !status.equals("1")) {
            status = "1";
        }
        String username = getAdminName();
        if (username == null){
            return new Result(false,StatusCode.ERROR,"获取管理员身份失败");
        }
        categoryBService.updateCateGory(name,categoryId,status,username,parent_id);
        return new Result(true,StatusCode.OK,"编辑分类成功");
    }

    /**
     * 启（禁）用分类
     * @param id
     * @return
     */
    @PatchMapping("/{id}")
    public Result updateCategoryStatus(@PathVariable("id") String id,String status){
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(status)){
            return new Result(false,StatusCode.ERROR,"id或status不能为空");
        }
        if (!"0".equals(status)&& !"1".equals(status)) {
            return new Result(false,StatusCode.ERROR,"status不合法");
        }
        String username = getAdminName();
        if (username == null){
            return new Result(false,StatusCode.ERROR,"获取管理员身份失败");
        }
        Result resul = categoryBService.updateStatus(id,username,status);
        return resul;
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delCategory(@PathVariable("id") String id){
        if (StringUtils.isEmpty(id)){
            return new Result(false,StatusCode.ERROR,"id不能为空");
        }
        categoryBService.delCategory(id);
        return new Result(true,StatusCode.OK,"删除分类成功");
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
