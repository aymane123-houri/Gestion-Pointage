package com.example.anomalieservice.feignAnomalie;

import com.example.anomalieservice.model.Horaire;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//, url = "http://localhost:8084"
@FeignClient(name = "HORAIRE-SERVICE")
public interface HoraireFeignAnomalie {

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
