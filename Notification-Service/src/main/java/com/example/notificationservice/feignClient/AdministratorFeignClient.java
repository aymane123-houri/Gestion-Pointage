package dcc.tp2.securityservice.feignClient;

import dcc.tp2.securityservice.model.Administrateur;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@CrossOrigin("http://localhost:4200/")
@FeignClient(name = "ADMINISTRATEUR-SERVICE")
//@FeignClient(name = "administrateur-service" , url = "http://administrateur-service:8085")
public interface AdministratorFeign {

        @GetMapping("/Administrateurs/email/{email}")
        @CircuitBreaker(name = "receptionist-service", fallbackMethod = "fallbackMethod")
        @Retry(name = "receptionist-service", fallbackMethod = "fallbackMethod")
        @RateLimiter(name = "receptionist-service", fallbackMethod = "fallbackMethod")
        Administrateur getAdministrator(@PathVariable String email);

        default Administrateur fallbackMethod(String email, Throwable throwable){
            System.err.println("Fallback for email: " + email + ", exception: " + throwable.getMessage());
            return new Administrateur();
        }

    }

