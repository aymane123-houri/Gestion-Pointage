package com.example.anomalieservice.entity;

import com.example.anomalieservice.model.Administrateur;
import com.example.anomalieservice.model.Employe;
import com.example.anomalieservice.model.Pointage;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "Anomalie")
public class Anomalie {

    @Id
    private String id ;
    private String pointage_id ;
    private Long employe_id ;
    @Enumerated(EnumType.STRING)
    private type_anomalie type ;

    private String description ;
    //private statut_animalie statut ;

    @Enumerated(EnumType.STRING)
    private StatutAnomalie statut;

    private LocalDateTime dateValidation;

    private Long validePar; // ID du validateur (RH ou Manager)
    @Transient
    private Employe employe;
    @Transient
    private Pointage pointage;

    @Transient
    private Administrateur administrateur;


}
