package com.itheima.material.controller;

import com.itheima.material.pojo.Category;
import com.itheima.material.pojo.Copyright;
import com.itheima.material.pojo.Material;
import com.itheima.material.service.MaterialService;
import com.itheima.material.service.SearchService;
import com.itheima.user.feign.UsercenterFeign;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private UsercenterFeign usercenterFeign;

    /**
     * 分类级别信息查询
     * @param level
     * @return
     */
    @GetMapping("/categorys/level/{level}")
    public Result findCategoryByLevel(@PathVariable("level") String level){
        if (StringUtils.isEmpty(level)){
            return new Result(false,StatusCode.ERROR,"level不能为空");
        }
        List<Category> categoryList = materialService.findCategoryByLevel(level);
        return new Result(true,StatusCode.OK,"查询成功",categoryList);
    }

    /**
     * 详情页
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findDetailsById(@PathVariable("id")String id, HttpServletRequest request){
        Map<String, String> userInfo = usercenterFeign.getUserInfo();
        if (userInfo!=null) {
            String userId = userInfo.get("id");
            materialService.addVisitHistory(userId,id);
        }else {
            String ip = request.getHeader("x-forwarded-for");
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                if (ip.indexOf(",") != -1) {
                    ip = ip.split(",")[0];
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            materialService.addVisitHistory(ip, id);
        }
        Map<String, Object> map = materialService.findDetailsById(id);
        return new Result(true, StatusCode.OK,"查询成功",map);
    }

    /**
     * 查询所有可用分类
     * @return
     */
    @GetMapping("/categorys/{id}")
    public Result findAllCategorys(@PathVariable("id") String id){
        List<Category> categoryList= materialService.findAllCategorys(id);
        return new Result(true,StatusCode.OK,"查询成功",categoryList);
    }

    /**
     * 条件查询
     * @param paramMap
     * @return
     */
    @GetMapping("/search")
    public Result search(@RequestParam Map<String,String> paramMap) {

        Map<String, Object> resultMap = searchService.searchMaterial(paramMap);
        return new Result(true,StatusCode.OK,"查询素材成功",resultMap);
    }

    /**
     * 首页精选（根据下载量来展示）
     * @param category1Id
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/choiceness/{category1Id}")
    public Result choiceness(@PathVariable("category1Id") String category1Id, String page, String size) {
        Map paramMap = new HashMap();
//        paramMap.put("category_id",category1Id);
        paramMap.put("pageNum",page);
        paramMap.put("pageSize",size);
        paramMap.put("sortField","download_num");
        Map resultMap = searchService.searchMaterial(paramMap);
        return new Result(true,StatusCode.OK,"查询素材成功",resultMap);
    }

    /**
     * 相似推荐
     * @param id
     * @return
     */
    @GetMapping("/recommends/{id}")
    public Result recommends(@PathVariable("id") String id) {
       Map resultMap = materialService.findRecommends(id);
        return new Result(true,StatusCode.OK,"查询素材成功",resultMap);
    }


}
