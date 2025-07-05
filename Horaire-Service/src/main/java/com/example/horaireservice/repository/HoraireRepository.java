package com.example.horaireservice.repository;

import com.example.horaireservice.entity.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HoraireRepository extends JpaRepository<Horaire,Long> {

    List<Horaire> findByEmployeId(Long employeId);
}
