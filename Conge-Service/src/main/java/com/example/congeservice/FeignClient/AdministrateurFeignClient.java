package com.example.congeservice.FeignClient;

import com.example.congeservice.model.Administrateur;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//, url = "http://localhost:8087"
@FeignClient(name = "ADMINISTRATEUR-SERVICE" )
public interface AdministrateurFeignClient {
    @GetMapping("/Administrateurs/{id}")
    @CircuitBreaker(name = "administrateur-service", fallbackMethod = "fallbackMethod")
    @Retry(name = "administrateur-service", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "administrateur-service", fallbackMethod = "fallbackMethod")
    Administrateur getAdministrateurById(@PathVariable Long id);
    default Administrateur fallbackMethod(Long id, Throwable throwable){
        System.err.println("Fallback for id: " + id + ", exception: " + throwable.getMessage());
        return new Administrateur();
    }
}
