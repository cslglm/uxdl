package com.itheima.system;

import com.itheima.system.config.TokenDecode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.itheima.system.dao"})
@EnableFeignClients(basePackages = {"com.itheima"})
public class SysApplication {
    public static void main(String[] args) {
        SpringApplication.run(SysApplication.class,args);
    }

    @Bean
    public TokenDecode tokenDecode(){
        return new TokenDecode();
    }
}
