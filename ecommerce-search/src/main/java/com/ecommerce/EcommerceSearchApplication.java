package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author wyr
 * @version 1.0
 * @name: EcommerceApplication
 * @description
 * @date 2021/3/2 15:32
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class EcommerceSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceSearchApplication.class,args);
    }
}
