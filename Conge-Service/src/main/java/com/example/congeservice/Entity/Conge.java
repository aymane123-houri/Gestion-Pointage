package com.example.congeservice.Entity;

import com.example.congeservice.model.Administrateur;
import com.example.congeservice.model.Employe;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Conge{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String type; // "PAYE", "SANS_SOLDE", "MALADIE", "MATERNITE", etc.


    @Column(nullable = false)
    private LocalDate dateDebut;
    @Column(nullable = false)
    private LocalDate dateFin;
    @Column(nullable = false)
    private int nombreJours; // Calculé automatiquement en fonction des dates
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusConge statut; // "EN_ATTENTE", "APPROUVE", "REFUSE"

    private String commentaire; // Motif ou remarque



    private LocalDateTime dateValidation; // Date de validation/refus

    private boolean affecteSurRapport; // Indique si ce congé est pris en compte dans les rapports d'absence
    @Column(updatable = false)
    private LocalDateTime dateCreation; // Date de création du congé

    // Getters et Setters
    @Column(nullable = false)
    private Long employeId;
    @Column(nullable = false)
    private Long administrateurId;

    @Transient
    private Employe employe; // L'employé concerné
    @Transient
    private Administrateur validateur; // L’admin qui valide le congé

}
