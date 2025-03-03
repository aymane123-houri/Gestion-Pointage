package com.example.rapportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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