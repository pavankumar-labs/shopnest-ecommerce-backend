package com.pavankumar.shopnestecommercebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EntityScan
@EnableAsync
@SpringBootApplication
public class ShopnestEcommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopnestEcommerceBackendApplication.class, args);
    }

}
