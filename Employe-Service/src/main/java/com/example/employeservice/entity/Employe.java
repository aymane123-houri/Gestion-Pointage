package com.example.employeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String matricule;  // Identifiant unique de l'employé
    private String nom;
    private String prenom;
    @Column(unique = true, nullable = false)
    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;
    @Column(unique = true, nullable = false)
    private String cin; // Carte d’identité nationale
    private Date dateNaissance;
    private String genre; // Homme / Femme / Autre
    private String poste; // Fonction occupée
    private String departement; // Département de l'employé
    private Date dateEmbauche; // Date d'entrée dans l'entreprise
    private Double salaire;
    //private String statut; // Actif, En congé, Démissionnaire...
    //private String photoProfil; // URL de la photo
    @Lob
    private byte[] photoProfil;

}
