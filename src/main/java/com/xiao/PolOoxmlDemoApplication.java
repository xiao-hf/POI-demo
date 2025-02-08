package com.xiao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xiao.mapper")
@SpringBootApplication
public class PolOoxmlDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolOoxmlDemoApplication.class, args);
    }

}
