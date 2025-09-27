package com.example.wangpicture;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@MapperScan("com.example.wangpicture.infrastructure.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class WangTuApplication {

    public static void main(String[] args) {
        SpringApplication.run(WangTuApplication.class, args);
    }

}
