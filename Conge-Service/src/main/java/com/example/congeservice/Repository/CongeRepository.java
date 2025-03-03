package com.example.congeservice.Repository;

import com.example.congeservice.Entity.Conge;
import com.example.congeservice.Entity.StatusConge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CongeRepository extends JpaRepository<Conge, Long> {
    List<Conge> findByStatut(StatusConge statut);
    List<Conge> findByEmployeIdAndStatut(Long employeId, StatusConge statut);

    List<Conge> findByEmployeIdAndDateDebutBetween(Long employeId, LocalDate startDate, LocalDate endDate);
}
