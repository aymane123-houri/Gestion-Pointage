package com.example.congeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {

    private Long id;

    private String matricule;  // Identifiant unique de l'employé
    private String nom;
    private String prenom;

    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;

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
