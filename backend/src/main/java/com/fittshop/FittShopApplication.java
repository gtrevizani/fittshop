package com.fittshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FittShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(FittShopApplication.class, args);
    }
}
