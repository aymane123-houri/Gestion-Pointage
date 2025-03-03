package com.example.rapportservice.entity;

import com.example.rapportservice.model.Employe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "rapports")
public class Rapport {

    @Id
    private String id ;
    private Long employeId ;
    private String periode;
    private double heures_travaillees ;
    private double heures_supplementaires ;
    private int retards ;
    private int absences ;

    private List<RapportDetail> details;

    @Transient
    private Employe employe;

    public Rapport(String id, Long employeId, String periode, int heures_travaillees, int heures_supplementaires, int retards, int absences, List<RapportDetail> details) {
        this.id = id;
        this.employeId = employeId;
        this.periode = periode;
        this.heures_travaillees = heures_travaillees;
        this.heures_supplementaires = heures_supplementaires;
        this.retards = retards;
        this.absences = absences;
        this.details = details;
    }
}
