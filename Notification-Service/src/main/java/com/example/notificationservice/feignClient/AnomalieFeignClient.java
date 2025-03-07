package com.example.notificationservice.feignClient;

import com.example.notificationservice.model.Anomalie;
import com.example.notificationservice.model.Employe;
import com.example.notificationservice.model.Horaire;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
// , url = "http://localhost:8086"
@FeignClient(name = "ANOMALIE-SERVICE")
public interface AnomalieFeignClient {
        @GetMapping("/Anomalie")
        @CircuitBreaker(name = "anoamlie-service", fallbackMethod = "fallbackMethod")
        @Retry(name = "anomalie-service", fallbackMethod = "fallbackMethod")
        @RateLimiter(name = "anomalie-service", fallbackMethod = "fallbackMethod")
        List<Anomalie> getAllAnomalie();
        @GetMapping("/Anomalie/{id}")
        @CircuitBreaker(name = "anomalie-service", fallbackMethod = "fallbackMethod2")
        @Retry(name = "anomalie-service", fallbackMethod = "fallbackMethod2")
        @RateLimiter(name = "anomalie-service", fallbackMethod = "fallbackMethod2")
        Anomalie getAnomalieById(@PathVariable String id);



        @GetMapping("/Anomalie/anomalies/duJour")
        @CircuitBreaker(name = "anomalie-service", fallbackMethod = "fallbackMethod3")
        @Retry(name = "anomalie-service", fallbackMethod = "fallbackMethod3")
        @RateLimiter(name = "anomalie-service", fallbackMethod = "fallbackMethod3")
        List<Anomalie> getAnomaliesDuJour();

        default List<Anomalie> fallbackMethod(Throwable throwable){
                System.err.println("Fallback pour obtenir les anomalie de les employés " + ", exception: " + throwable.getMessage());
                return new ArrayList<>(); // Retourne une liste vide pour les horaires
        }
        default Anomalie fallbackMethod2(String id, Throwable throwable){
                System.err.println("Fallback for id: " + id + ", exception: " + throwable.getMessage());
                return new Anomalie();
        }

        default List<Anomalie> fallbackMethod3(Throwable throwable){
                System.err.println("Fallback pour obtenir les employes Absente " + ", exception: " + throwable.getMessage());
                return new ArrayList<>(); // Retourne une liste vide pour les horaires
        }
}
