package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wyr
 * @version 1.0
 * @name: EcommerceGoodsWebApplication
 * @description
 * @date 2021/3/5 17:07
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class EcommerceGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceGoodsWebApplication.class,args);
    }
}
