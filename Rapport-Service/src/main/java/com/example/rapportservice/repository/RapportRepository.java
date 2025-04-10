package com.example.rapportservice.repository;

import com.example.rapportservice.entity.Rapport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RapportRepository extends MongoRepository<Rapport,String> {
    List<Rapport> findByEmployeId(Long employeId);
    Rapport findByEmployeIdAndPeriode(Long employeId, String periode);

    List<Rapport> findByPeriode(String periode);

    // Trouver tous les rapports pour une année donnée
    List<Rapport> findByPeriodeStartingWith(String annee);
}
