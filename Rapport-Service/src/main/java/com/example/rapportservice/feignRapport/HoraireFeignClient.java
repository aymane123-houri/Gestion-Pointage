package com.example.rapportservice.feignRapport;


import com.example.rapportservice.model.Horaire;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@FeignClient(name = "horaire-service", url = "http://localhost:8084")
public interface HoraireFeignClient {

    @GetMapping("/Horaires")
    @CircuitBreaker(name = "horaire-service", fallbackMethod = "fallbackMethod")
    @Retry(name = "horaire-service", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "horaire-service", fallbackMethod = "fallbackMethod")
    List<Horaire> getAllHoraires();

    // Le fallback doit correspondre aux paramètres : employeId et Throwable
    default List<Horaire> fallbackMethod(Throwable throwable){
        System.err.println("Fallback pour obtenir les horaires de les employés " + ", exception: " + throwable.getMessage());
        return new ArrayList<>(); // Retourne une liste vide pour les horaires
    }

}
