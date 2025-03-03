package com.example.anomalieservice.feignAnomalie;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "conge-service", url = "http://localhost:8089")
public interface CongeFeignClient {

    @GetMapping("Conges/employe/{employeId}/en-conge")
    boolean isEmployeEnConge(@PathVariable Long employeId, @RequestParam String date);
}
