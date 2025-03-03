package com.example.congeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CongeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CongeServiceApplication.class, args);
    }

}
