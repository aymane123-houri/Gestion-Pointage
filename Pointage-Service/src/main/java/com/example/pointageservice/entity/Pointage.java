package com.example.pointageservice.entity;

import com.example.pointageservice.model.Employe;
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
@Document(collection = "pointage")
public class Pointage {

        @Id
        private String id;

        private Long employeId; // Référence à l'employé (par ID de MongoDB)

        private LocalDateTime dateHeureEntree; // Heure de pointage d'entrée
        private LocalDateTime  dateHeureSortie; // Heure de sortie (peut être null au début)
        private String statut; // "Présent", "Absent", "En retard", etc.

        @Transient
        private Employe employe;



}