package com.example.employeservice.repository;

import com.example.employeservice.entity.Departement;
import com.example.employeservice.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartementRepository extends JpaRepository<Departement, Long> {
}
