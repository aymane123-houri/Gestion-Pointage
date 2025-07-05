package com.example.employeservice.controller;

import com.example.employeservice.entity.Departement;
import com.example.employeservice.entity.Employe;
import com.example.employeservice.service.DepartementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Departements")
public class DepartmentController {
    private final DepartementService departementService;

    public DepartmentController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @PostMapping
    public ResponseEntity<Departement> CreateDepartement(@RequestBody Departement departement){
        System.out.println("Données reçues: " + departement);

        Departement departement1= departementService.createDepartement(departement);
        return ResponseEntity.ok(departement1);
    }

    @GetMapping
    public ResponseEntity<List<Departement>> GetAllDepartement(){
        List<Departement> departements = departementService.GetAllDepartement();
        return ResponseEntity.ok(departements);
    }
}
