package com.example.horaireservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HoraireServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoraireServiceApplication.class, args);
    }

}
