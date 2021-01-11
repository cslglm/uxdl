package com.itheima.user.job;

import com.itheima.user.util.FastDFSClient;
import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.util.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 自定义Job，实现定时清理垃圾图片
 */
@Component
@Configuration
@EnableScheduling
public class ClearImgJob {

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void clearImg(){
        //根据Redis中保存的两个set集合进行差值计算，获得垃圾图片名称集合
        SetOperations set = redisTemplate.opsForSet();

        Set difference = set.difference(RedisConstant.MATE_PIC_RESOURCES,
                RedisConstant.MATE_PIC_DB_RESOURCES);

        if(difference != null){
            for (Object file : difference) {
                String fileName = (String) file;
                String[] split = fileName.split(",");
                try {
                    FastDFSClient.deleteFile(split[0],split[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                QiniuUtils.deleteFileFromQiniu((String) file);
                set.remove(RedisConstant.MATE_PIC_RESOURCES,file);
            }
        }
    }
}