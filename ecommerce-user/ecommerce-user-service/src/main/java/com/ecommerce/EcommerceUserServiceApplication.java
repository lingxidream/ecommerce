package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author wyr
 * @version 1.0
 * @name: EcommerceUserServiceApplication
 * @description
 * @date 2021/3/9 16:53
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.ecommerce.user.mapper")
public class EcommerceUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceUserServiceApplication.class,args);
    }
}
