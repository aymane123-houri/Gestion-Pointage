package com.example.rapportservice.feignRapport;

import com.example.rapportservice.model.Pointage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "pointage-service" , url = "http://localhost:8083")
public interface PointageFeignClient {

    @CircuitBreaker(name = "pointage-service", fallbackMethod = "fallbackMethod2")
    @Retry(name = "pointage-service", fallbackMethod = "fallbackMethod2")
    @RateLimiter(name = "pointage-service", fallbackMethod = "fallbackMethod2")
    @GetMapping("/Pointages/employe/{employeId}/{date}")
    List<Pointage> getPointagesParEmployeEtDate(@PathVariable Long employeId, @PathVariable String date);
    // Méthode de secours pour la gestion des erreurs
    default List<Pointage> fallbackMethod2(Long employeId, String date, Throwable throwable) {
        if (throwable instanceof feign.FeignException.NotFound) {
            System.out.println("Aucun pointage trouvé pour l'employé " + employeId + " à la date " + date);
            return null; // Retourne null si la ressource n'existe pas
        }

        System.err.println("Fallback pour obtenir les pointages de l'employé " + employeId + " à la date " + date + ", exception: " + throwable.getMessage());
        return null; // Retourne null en cas d'échec
    }
}
