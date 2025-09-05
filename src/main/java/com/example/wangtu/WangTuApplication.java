package com.example.wangtu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.example.wangtu.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class WangTuApplication {

    public static void main(String[] args) {
        SpringApplication.run(WangTuApplication.class, args);
    }

}
