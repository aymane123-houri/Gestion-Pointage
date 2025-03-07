package com.example.notificationservice.feignClient;

import com.example.notificationservice.model.Administrateur;
import com.example.notificationservice.model.Horaire;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("http://localhost:4200/")
@FeignClient(name = "ADMINISTRATEUR-SERVICE")
//@FeignClient(name = "administrateur-service" , url = "http://administrateur-service:8085")
public interface AdministratorFeignClient {

        @GetMapping("/Administrateurs/email/{email}")
        @CircuitBreaker(name = "receptionist-service", fallbackMethod = "fallbackMethod")
        @Retry(name = "receptionist-service", fallbackMethod = "fallbackMethod")
        @RateLimiter(name = "receptionist-service", fallbackMethod = "fallbackMethod")
        Administrateur getAdministrator(@PathVariable String email);




        default Administrateur fallbackMethod(String email, Throwable throwable){
            System.err.println("Fallback for email: " + email + ", exception: " + throwable.getMessage());
            return new Administrateur();
        }

        @GetMapping("/Administrateurs")
        @CircuitBreaker(name = "administrateur-service", fallbackMethod = "fallbackMethod")
        @Retry(name = "administrateur-service", fallbackMethod = "fallbackMethod")
        @RateLimiter(name = "administrateur-service", fallbackMethod = "fallbackMethod")
        List<Administrateur> getAllAdministrateurs();

    default List<Administrateur> fallbackMethod2(Throwable throwable){
        System.err.println("Fallback for : " + ", exception: " + throwable.getMessage());
        return new ArrayList<>();
    }

    }

