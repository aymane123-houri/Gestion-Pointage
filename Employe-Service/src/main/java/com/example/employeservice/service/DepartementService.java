package com.example.employeservice.service;

import com.example.employeservice.entity.Departement;
import com.example.employeservice.entity.Employe;
import com.example.employeservice.repository.DepartementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {
    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }


    public Departement createDepartement(Departement departement){
        return departementRepository.save(departement);
    }

    public Departement getDepartementById(Long id){
        return departementRepository.findById(id).orElse(null);
    }

    public List<Departement> GetAllDepartement(){
        return departementRepository.findAll();
    }

    public void deleteDepartement(Long id){
        departementRepository.deleteById(id);
    }

    public Departement updateDepartment(Departement departement, Long id) {
        return departementRepository.findById(id).map(departement1 -> {
                    departement1.setNom_departement(departement.getNom_departement());


                    //employeRepository1.setPassword(passwordEncoder.encode(user.getPassword()));
                    return departementRepository.save(departement1);
                }

        ).orElseThrow((() -> new RuntimeException("departement not found")));
    }

}
