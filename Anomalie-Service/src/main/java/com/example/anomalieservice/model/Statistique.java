package com.example.anomalieservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statistique {
    private long totalEmployes;
    private long totalPointages;
    private long absences;
    private long retards;
    private long totalAnomalies;
}