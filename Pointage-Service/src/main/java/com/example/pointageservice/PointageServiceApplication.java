package com.example.pointageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class PointageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointageServiceApplication.class, args);
    }

}
