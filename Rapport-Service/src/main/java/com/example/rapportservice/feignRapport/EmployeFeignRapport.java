package com.example.rapportservice.feignRapport;

import com.example.rapportservice.model.Employe;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
//, url = "http://localhost:8081"
@FeignClient(name = "EMPLOYE-SERVICE" )
public interface EmployeFeignRapport {
    @GetMapping("/Employes/{id}")
    @CircuitBreaker(name = "employe-service", fallbackMethod = "fallbackMethod")
    @Retry(name = "employe-service", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "employe-service", fallbackMethod = "fallbackMethod")
    Employe getEmployeById(@PathVariable Long id);

    default Employe fallbackMethod(Long id, Throwable throwable){
        System.err.println("Fallback for id: " + id + ", exception: " + throwable.getMessage());
        return new Employe();
    }

    @GetMapping("/Employes")
    @CircuitBreaker(name = "employe-service", fallbackMethod = "fallbackMethod2")
    @Retry(name = "employe-service", fallbackMethod = "fallbackMethod2")
    @RateLimiter(name = "employe-service", fallbackMethod = "fallbackMethod2")
    List<Employe> getTousLesEmployes();

    // Méthode de fallback pour getTousLesEmployes
    default List<Employe> fallbackMethod2(Throwable throwable){
        System.err.println("Fallback pour obtenir tous les employés, exception: " + throwable.getMessage());
        return new ArrayList<>(); // Retourne une liste vide par défaut
    }
}
