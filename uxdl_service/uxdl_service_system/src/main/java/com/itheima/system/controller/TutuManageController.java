package com.itheima.system.controller;

import com.itheima.system.config.TokenDecode;
import com.itheima.system.service.TutuManageService;
import com.itheima.system.util.FastDFSClient;
import com.itheima.tutu.pojo.Tutu;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.FastDFSFile;
import com.itheima.uxdl.util.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ads")
public class TutuManageController {

    @Autowired
    private TutuManageService tutuManageService;

    @Autowired
    private TokenDecode tokenDecode;

    /**
     * 条件分页查询
     *
     * @param searchMap
     * @return
     */
    @GetMapping
    public Result findPage(@RequestParam Map searchMap) {

        PageResult pageResult = tutuManageService.findPage(searchMap);
        return new Result(true, StatusCode.OK, "查询轮播图成功", pageResult);
    }

    /**
     * 新增轮播图
     *
     * @param tutu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Tutu tutu) {
        String username = tokenDecode.getUserInfo().get("username");
        tutu.setUpdate_admin(username);
        Result result = tutuManageService.add(tutu);
        return result;
    }

    /**
     * 删除轮播图
     *
     * @param tutuId
     * @return
     */
    @DeleteMapping("/{tutuId}")
    public Result delete(@PathVariable("tutuId") String tutuId) {
        tutuManageService.delete(tutuId);
        return new Result(true, StatusCode.OK, "删除轮播图成功");
    }

    /**
     * 编辑轮播图
     *
     * @param tutu
     * @return
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Tutu tutu ,@PathVariable("id") String id) {
        Result updateResult = tutuManageService.update(tutu, id);
        return updateResult;
    }

    /**
     * 上传广告视频或图片文件
     * @param file
     * @return
     */
    @PostMapping("/materials")
    public Result upload(@RequestBody MultipartFile file) {
        /*try {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.indexOf(".") + 1);
            String fileName = UUID.randomUUID() + "." + suffix;
            QiniuUtils.upload2Qiniu(file.getBytes(),fileName);
            String url = "http://qh70pkdu3.hn-bkt.clouddn.com/" + fileName;
            SetOperations set = redisTemplate.opsForSet();
            set.add(RedisConstant.MATE_PIC_RESOURCES,fileName );
            return new Result(true,StatusCode.OK,"上传文件成功",url);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"上传文件失败");
        }*/
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
            String source_dpi = null;

            BufferedImage image = ImageIO.read(file.getInputStream());
            int height = image.getHeight();
            int width = image.getWidth();
            source_dpi = width + "*" + height;

            long size = file.getSize();
            double size_dou = 0;
            String source_size = null;
            if (size < 1024) {
                source_size = size + "B";
            } else if (size < 1024 * 1024) {
                size_dou = size / 1024;
                source_size = size_dou + "KB";

            } else {
                size_dou = size / 1024 / 1024;
                source_size = size_dou + "M";

            }
            byte[] fileContent = file.getBytes();
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, fileContent, ext);
            String[] fileInfo = FastDFSClient.upload(fastDFSFile);
            String url = FastDFSClient.getTrackerUrl() + fileInfo[0] + "/" + fileInfo[1];
            resultMap.put("source_size", source_size);
            resultMap.put("source_dpi", source_dpi);
            resultMap.put("url", url);
            return new Result<>(true, StatusCode.OK, "上传成功", resultMap);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result<>(false, StatusCode.ERROR, "上传失败");
        }
    }

    /**
     * 修改轮播图的权重值和跳转链接
     * @param id
     * @param seq
     * @return
     */
    @PatchMapping("/{id}")
    public Result updateWeight (@PathVariable("id")String id,String seq,String link_url,String status) {
       Result weightResult = tutuManageService.updateWeight(id,seq,link_url,status);
       return weightResult;
    }

}
