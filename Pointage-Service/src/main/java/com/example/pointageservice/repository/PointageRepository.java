package com.example.pointageservice.repository;

import com.example.pointageservice.entity.Pointage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PointageRepository extends MongoRepository<Pointage,String> {
    Optional<Pointage> findTopByEmployeIdOrderByDateHeureEntreeDesc(Long employeId);
    List<Pointage> findByEmployeIdAndDateHeureEntreeAfter(Long employeId, LocalDateTime dateHeureEntree);

    // Méthode pour récupérer les pointages d'un employé à partir de son ID

    // Rechercher des pointages d'un employé entre une plage horaire (date de début et de fin)
    // Méthode pour récupérer les pointages d'un employé pour une date donnée
    List<Pointage> findByEmployeIdAndDateHeureEntreeBetween(Long employeId, LocalDateTime startDate, LocalDateTime endDate);
    ;
    List<Pointage> findByEmployeId(Long employeId);

    List<Pointage> findByDateHeureEntreeBetween(LocalDateTime debut, LocalDateTime fin);
}
