package com.example.administrateurservice.repository;

import com.example.administrateurservice.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrateurRepository extends JpaRepository<Administrateur,Long> {
    Administrateur getAdministrateurByEmail(String email);
}
