package com.example.congeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur {


    private Long id;


    private String nom;


    private String prenom;


    private String cin;


    private String email;


    private String motDePasse; // Stocké en hashé (BCrypt)

    private RoleAdministrateur role;

    private String telephone;

    private LocalDateTime dateCreation = LocalDateTime.now();


}