package com.example.employeservice.repository;

import com.example.employeservice.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe, Long> {

    @Query("SELECT e.matricule FROM Employe e ORDER BY e.id DESC LIMIT 1")
    String findLastMatricule();

    List<Employe> findByNom(String nom);
    List<Employe> findByDepartement(String departement);
}
