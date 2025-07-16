package com.itbank.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@MapperScan("com.itbank.mall.mapper")
@SpringBootApplication
@MapperScan("com.itbank.mall.mapper")
public class IbmallApplication {
    public static void main(String[] args) {
        SpringApplication.run(IbmallApplication.class, args);
    }
}
