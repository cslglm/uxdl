package com.itheima.user.controller;

import com.itheima.material.pojo.Material;
import com.itheima.user.config.TokenDecode;
import com.itheima.user.service.UsercenterService;
import com.itheima.user.util.FastDFSClient;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.FastDFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心
 */
@RestController
@RequestMapping("/usercenter")
public class UsercenterController {

    @Autowired
    private UsercenterService usercenterService;

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登陆成功后查询个人作品
     *
     * @param pageNum
     * @param pageSize
     * @param sort
     * @param category_id
     * @return
     */
    @GetMapping("/personal/works")
    public Result findPersonalWorks(String pageNum, String pageSize, String sort, String category_id) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return new Result(true, StatusCode.ERROR, "用户未登录");
        }
        String id = userInfo.get("id");
        PageResult pageResult = usercenterService.findPersonalWorks(id, pageNum, pageSize, sort, category_id);
        return new Result(true, StatusCode.OK, "获取个人作品成功", pageResult);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/userInfo")
    public  Map<String, String> getUserInfo(){
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        return userInfo;
    }

    /**
     * 根据作品id查找作品，编辑回显
     *
     * @param id
     * @return
     */
    @GetMapping("/works/{id}")
    public Result findMaterialById(@PathVariable("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return new Result(false, StatusCode.ERROR, "id不能为空");
        }
        Material material = usercenterService.findMaterialById(id);
        return new Result(true, StatusCode.OK, "查询成功", material);
    }


    /**
     * 用户编辑作品信息
     *
     * @param id
     * @param material
     * @return
     */
    @PutMapping("/works/{id}")
    public Result editMaterial(@PathVariable("id") String id, @RequestBody Material material) {
        Result result = usercenterService.editMaterial(id, material);
        return result;
    }

    /**
     * 新增作品（上传压缩包）信息
     *
     * @param material
     * @return
     */
    @PostMapping("/works/template")
    public Result addMaterial(@RequestBody Material material) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        material.setUser_id(userInfo.get("id"));
        material.setUser(userInfo.get("nickName"));
        Result result = usercenterService.addMaterial(material);
        return result;
    }

    /**
     * 新增作品（上传摄影图片）信息
     *
     * @param material
     * @return
     */
    @PostMapping("/photography")
    public Result addPhotoGraphy(@RequestBody Material material) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        material.setUser_id(userInfo.get("id"));
        material.setUser(userInfo.get("nickName"));
        Result result = usercenterService.addMaterial(material);
        return result;
    }

    /**
     * 文件下载
     *
     * @param id
     * @return
     */
    @PostMapping("/download/{id}")
    public Result download(@PathVariable("id") String id/*,HttpServletResponse response*/) {
        String userId = tokenDecode.getUserInfo().get("id");
        Material material = usercenterService.findMaterialById(id);
        String url = material.getDownloadurl();
        usercenterService.addDownloadHistory(Long.parseLong(userId), id);
        /*if (StringUtils.isEmpty(url) || url.equals("undefined")) {
            return new Result(true,StatusCode.OK,"文件不存在");
        }
       try {

//            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String group = url.split("/")[3];
            System.out.println(group);
            String fileName = url.split(group+"/")[1];
            System.out.println(fileName);
            InputStream inputStream = FastDFSClient.downFile(group, fileName);
            response.setContentType("application/force-download");// 设置强制下载不打开
//            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(material.getName(), "UTF-8") );
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            inputStream.close();
            return new Result(true, StatusCode.OK, "文件下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return new Result(true, StatusCode.OK, "文件下载成功", url);
//        return new Result(false, StatusCode.ERROR, "文件下载失败");
    }


    /**
     * 查找下载记录
     *
     * @return
     */
    @GetMapping("/download_history")
    public Result downloadHistory(String category_id,String pageNum,String pageSize) {
        String userId = tokenDecode.getUserInfo().get("id");
        Map downloadHistory = usercenterService.findDownloadHistory(Long.parseLong(userId),category_id,pageNum,pageSize);
        return new Result(true, StatusCode.OK, "查询下载记录成功", downloadHistory);
    }

    /**
     * 删除下载记录
     *
     * @param id
     * @return
     */
    @DeleteMapping("/download_history/{id}")
    public Result deleteDownloadHistory(@PathVariable("id") String id) {
        usercenterService.deleteDownloadHistory(id);
        return new Result(true, StatusCode.OK, "删除下载记录成功");
    }

    /**
     * 上传文件（压缩包、摄影图片、封面）
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile file) {

        try {
            if (file == null) {
                throw new RuntimeException("文件不存在");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("文件不存在");
            }
            Map<String, Object> resultMap = new HashMap<>();
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String source_size = null;
            if (ext.equals("jpg") || ext.equals("png")) {
                BufferedImage image = ImageIO.read(file.getInputStream());
                int height = image.getHeight();
                int width = image.getWidth();
                source_size = width + "px*" + height+"px";
            }
            long size = file.getSize();
            double spec_str = size / 1024 / 1024;
            String spec = spec_str + "M";

            byte[] fileContent = file.getBytes();

            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, fileContent, ext);
            String[] fileInfo = FastDFSClient.upload(fastDFSFile);
            String url = FastDFSClient.getTrackerUrl() + fileInfo[0] + "/" + fileInfo[1];
            resultMap.put("spec", spec);
            resultMap.put("source_type", ext);
            resultMap.put("source_size", source_size);
            resultMap.put("url", url);
            SetOperations set = redisTemplate.opsForSet();
            set.add(RedisConstant.MATE_PIC_RESOURCES, fileInfo[0] + "," + fileInfo[1]);
            return new Result<>(true, StatusCode.OK, "上传成功", resultMap);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result<>(false, StatusCode.ERROR, "上传失败");
        }
    }

    /**
     * 批量上传文件（压缩包、摄影图片、封面）
     *
     * @param files
     * @return
     */
    @PostMapping("/uploadbulk")
    public Result uploadBulk(@RequestBody MultipartFile[] files) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            try {
                String originalFilename = file.getOriginalFilename();
//                Map<String, Object> resultMap = new HashMap<>();
                String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                String[] name = originalFilename.split("\\.");
                String source_size = null;
                if (ext.equals("jpg") || ext.equals("png")) {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    int height = image.getHeight();
                    int width = image.getWidth();
                    source_size = width + "px*" + height+"px";
                }
                long size = file.getSize();
                double spec_str =0;
                String spec ="0M";
                if (size>1024*1024) {
                    spec_str = size / 1024 / 1024;
                    spec = spec_str + "M";
                }else {
                    spec_str= size/1024;
                    spec = spec_str + "KB";
                }

                byte[] fileContent = file.getBytes();

                FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, fileContent, ext);
                String[] fileInfo = FastDFSClient.upload(fastDFSFile);
                String url = FastDFSClient.getTrackerUrl() + fileInfo[0] + "/" + fileInfo[1];
               /* resultMap.put("spec", spec);
                resultMap.put("source_type", ext);
                resultMap.put("source_dpi", source_dpi);
                resultMap.put("url", url);
                resultMap.put("originalFilename",originalFilename);*/

                String sql = "insert into tb_material (id,name,show_image,details_image,spec,`status`, downloadurl,source_file,source_type,source_size,isdel,musage,draft) " +
                        "VALUES('SC20201128154111"+i+"','"+name[0]+"','"+url+"','"+url+"','"+spec+"','1','"+url+"','"+originalFilename+"','"+ext+"','"+source_size+"','0','上线数据','0');";
                list.add(sql);
            } catch (IOException e) {
                e.printStackTrace();
                return new Result<>(false, StatusCode.ERROR, "上传失败");
            }

        }
        return new Result<>(true, StatusCode.OK, "上传成功", list);
    }
    /*public Result upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".") + 1;
            String suffix = originalFilename.substring(index);
            String fileName = null;
            Map<String, Object> resultMap = new HashMap<>();
            String spec = null;
            if (suffix.equals("jpg") || suffix.equals("png")) {
                BufferedImage image = ImageIO.read(file.getInputStream());
                int height = image.getHeight();
                int width = image.getWidth();
                spec = width + "*" + height;
            } else {
                long size = file.getSize();
                double spec_str = size / 1024 / 1024;
                spec = spec_str + "M";
            }

            resultMap.put("spec", spec);
            resultMap.put("filetype", suffix);
            fileName = UUID.randomUUID().toString() + suffix;
            resultMap.put("downloadurl", "http://qh70pkdu3.hn-bkt.clouddn.com/" + fileName);

            QiniuUtils.upload2Qiniu(file.getBytes(), fileName);
            SetOperations set = redisTemplate.opsForSet();
            set.add(RedisConstant.MATE_PIC_RESOURCES,  fileName);

            return new Result(true, StatusCode.OK, "上传文件成功", resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.ERROR, "上传文件失败");
        }
    }*/

    /**
     * 删除作品（逻辑删除）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/works/{id}")
    public Result delById(@PathVariable("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return new Result(false, StatusCode.ERROR, "id不能为空");
        }
        usercenterService.delById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 获取个人收藏作品
     *
     * @return
     */
    @GetMapping("/works/favorites")
    public Result getFavorites(String category_id,String pageNum,String pageSize) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return new Result(true, StatusCode.ERROR, "用户未登录");
        }
        String id = userInfo.get("id");
        Map favorites = usercenterService.getFavorites(id, category_id, pageNum, pageSize);
        return new Result(true, StatusCode.OK, "查询成功", favorites);
    }

    /**
     * 获取个人收藏作品id
     *
     * @return
     */
    @GetMapping("/works/favoritesId")
    public Result getFavoritesId() {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return new Result(true, StatusCode.ERROR, "用户未登录");
        }
        String id = userInfo.get("id");
        List<Integer> ids = usercenterService.getFavoritesId(id);
        return new Result(true, StatusCode.OK, "查询成功", ids);
    }


    /**
     * 取消收藏
     *
     * @param id
     * @return
     */
    @DeleteMapping("/works/favorites/{id}")
    public Result delFavorites(@PathVariable("id") String id) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return new Result(true, StatusCode.ERROR, "用户未登录");
        }
        String uid = userInfo.get("id");
        usercenterService.delFavorites(uid, id);
        return new Result(true, StatusCode.OK, "取消成功");
    }

    /**
     * 添加收藏
     *
     * @param id
     * @return
     */
    @PostMapping("/works/favorites/{id}")
    public Result addFavorites(@PathVariable("id") String id) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        if (userInfo == null || userInfo.size() == 0) {
            return new Result(true, StatusCode.ERROR, "用户未登录");
        }
        String uid = userInfo.get("id");
        usercenterService.addFavorites(uid, id);
        return new Result(true, StatusCode.OK, "添加成功");
    }
}
