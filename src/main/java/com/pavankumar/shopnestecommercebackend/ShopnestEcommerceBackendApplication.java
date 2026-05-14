package com.pavankumar.shopnestecommercebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableRetry
public class ShopnestEcommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopnestEcommerceBackendApplication.class, args);
    }

}
