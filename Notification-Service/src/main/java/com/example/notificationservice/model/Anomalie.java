package com.example.notificationservice.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Anomalie {

    private String id ;
    private String pointage_id ;
    private Long employe_id ;

    private type_anomalie type ;

    private String description ;
    //private statut_animalie statut ;

    private StatutAnomalie statut;

    private LocalDateTime dateValidation; ;

    private Long validePar; // ID du validateur (RH ou Manager)

    private Employe employe;

    private Pointage pointage;

    private List<Employe> employesAbsents;

    // Méthode getter pour obtenir les employés absents
    public List<Employe> getEmployesAbsents() {
        return employesAbsents;
    }

}
