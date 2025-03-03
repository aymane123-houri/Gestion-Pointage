package com.example.horaireservice.repository;

import com.example.horaireservice.entity.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HoraireRepository extends JpaRepository<Horaire,Long> {
    // Trouver les horaires d'un employé pour une date spécifique
    //List<Horaire> findByEmployeIdAndDateHeureEntree(Long employeId, LocalDate date);
    // Recherche des horaires par employéId
    List<Horaire> findByEmployeId(Long employeId);
}
