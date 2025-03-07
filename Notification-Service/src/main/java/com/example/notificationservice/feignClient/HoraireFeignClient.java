package com.example.notificationservice.feignClient;

import com.example.notificationservice.model.Horaire;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
//, url = "http://localhost:8084"
@FeignClient(name = "HORAIRE-SEVICE" )
public interface HoraireFeignClient {
    @GetMapping("/Horaires/employe/{employeId}")
    @CircuitBreaker(name = "employe-service", fallbackMethod = "fallbackMethod")
    @Retry(name = "employe-service", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "employe-service", fallbackMethod = "fallbackMethod")
    List<Horaire> getHoraire(@PathVariable("employeId") Long employeId);


    default List<Horaire> fallbackMethod(Long id, Throwable throwable){
        System.err.println("Fallback for id: " + id + ", exception: " + throwable.getMessage());
        return new ArrayList<>();
    }

}
