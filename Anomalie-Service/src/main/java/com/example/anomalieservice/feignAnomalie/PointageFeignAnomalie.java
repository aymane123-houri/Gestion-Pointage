package com.example.anomalieservice.feignAnomalie;

import com.example.anomalieservice.model.Employe;
import com.example.anomalieservice.model.Pointage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//, url = "http://localhost:8083"
@FeignClient(name = "POINTAGE-SERVICE" )
public interface PointageFeignAnomalie {

    @GetMapping("/Pointages/{id}")
    @CircuitBreaker(name = "pointage-service", fallbackMethod = "fallbackMethod")
    @Retry(name = "pointage-service", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "pointage-service", fallbackMethod = "fallbackMethod")
    Pointage getpointageById(@PathVariable String id);


    default Pointage fallbackMethod(String id, Throwable throwable){
        System.err.println("Fallback for id: " + id + ", exception: " + throwable.getMessage());
        return new Pointage();
    }

    // Récupérer les pointages d'un employé pour une date donnée

    @CircuitBreaker(name = "pointage-service", fallbackMethod = "fallbackMethod2")
    @Retry(name = "pointage-service", fallbackMethod = "fallbackMethod2")
    @RateLimiter(name = "pointage-service", fallbackMethod = "fallbackMethod2")
    @GetMapping("/Pointages/employe/{employeId}/{date}")
    List<Pointage> getPointagesParEmployeEtDate(@PathVariable Long employeId, @PathVariable String date);
    // Méthode de secours pour la gestion des erreurs

    default List<Pointage> fallbackMethod2(Long employeId, String date, Throwable throwable) {
        if (throwable instanceof feign.FeignException.NotFound) {
            System.out.println("📌 Aucun pointage trouvé pour l'employé " + employeId + " à la date " + date);
            return Collections.emptyList(); // Retourne une liste vide au lieu de null
        }

        System.err.println("⚠️ Fallback pour obtenir les pointages de l'employé " + employeId + " à la date " + date + ", exception: " + throwable.getMessage());
        return Collections.emptyList(); // Toujours retourner une liste vide en cas d'erreur
    }

    @CircuitBreaker(name = "pointage-service", fallbackMethod = "fallbackMethod3")
    @Retry(name = "pointage-service", fallbackMethod = "fallbackMethod3")
    @RateLimiter(name = "pointage-service", fallbackMethod = "fallbackMethod3")
    @GetMapping("/Pointages/api/pointages")
    List<Pointage> getAllPointages(@RequestParam String debut, @RequestParam String fin);


    default List<Pointage> fallbackMethod3(String debut,String fin, Throwable throwable) {
        if (throwable instanceof feign.FeignException.NotFound) {
            System.out.println("Aucun pointage trouvé entre " + debut + " et  date " + fin);
            return null; // Retourne null si la ressource n'existe pas
        }

        System.err.println("Fallback pour obtenir les pointages entre " + debut + " et date fin " + fin + ", exception: " + throwable.getMessage());
        return null; // Retourne null en cas d'échec
    }
}
