package com.example.rapportservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rapport_details")
public class RapportDetail {
    private Long employeId;

    private LocalDate jour;
    private double heuresTravaillees;
    private double heuresSupplementaires;
    private boolean enRetard;
    private boolean absent;
    private String statutJour; // Peut être "Travail", "Congé", "Week-end", "Jour férié"

    public RapportDetail(LocalDate jour, double heuresTravaillees, double heuresSupplementaires, boolean enRetard, boolean absent, String statutJour) {
        this.jour = jour;
        this.heuresTravaillees = heuresTravaillees;
        this.heuresSupplementaires = heuresSupplementaires;
        this.enRetard = enRetard;
        this.absent = absent;
        this.statutJour = statutJour;
    }
}