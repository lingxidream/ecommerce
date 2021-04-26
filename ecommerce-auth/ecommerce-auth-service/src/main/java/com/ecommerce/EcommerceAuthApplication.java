package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wyr
 * @version 1.0
 * @name: EcommerceAuthApplication
 * @description
 * @date 2021/3/10 14:56
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class EcommerceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceAuthApplication.class,args);
    }
}
