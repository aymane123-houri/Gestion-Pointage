package com.example.congeservice.FeignClient;

import com.example.congeservice.model.Employe;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//, url = "http://localhost:8081"
@FeignClient(name = "EMPLOYE-SERVICE" )
public interface EmployeFeignClient {
    @GetMapping("/Employes/{id}")
    @CircuitBreaker(name = "employe-service", fallbackMethod = "fallbackMethod")
    @Retry(name = "employe-service", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "employe-service", fallbackMethod = "fallbackMethod")
    Employe getEmployeById(@PathVariable Long id);
    default Employe fallbackMethod(Long id, Throwable throwable){
        System.err.println("Fallback for id: " + id + ", exception: " + throwable.getMessage());
        return new Employe();
    }
}
