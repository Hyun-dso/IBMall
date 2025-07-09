package com.itbank.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan("com.itbank.mall.mapper")
@SpringBootApplication
public class IbmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(IbmallApplication.class, args);
	}

}
