package com.example.anomalieservice.model;

import jakarta.persistence.Id;
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
public class Pointage {

        private String id;

        private Long employeId; // Référence à l'employé (par ID de MongoDB)

        private LocalDateTime dateHeureEntree; // Heure de pointage d'entrée
        private LocalDateTime dateHeureSortie; // Heure de sortie (peut être null au début)
        private String statut; // "Présent", "Absent", "En retard", etc.



}