package com.example.rapportservice.feignRapport;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
//, url = "http://localhost:8089"
@FeignClient(name = "CONGE-SERVICE")
public interface CongeFeignClient {

    @GetMapping("Conges/employe/{employeId}/en-conge")
    boolean isEmployeEnConge(@PathVariable Long employeId, @RequestParam String date);
}
