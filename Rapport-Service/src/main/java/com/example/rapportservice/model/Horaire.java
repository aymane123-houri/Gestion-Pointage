package com.example.rapportservice.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Horaire {

    private Long id;
    //private Long employe_id;
    private Long employeId;

    private LocalTime heure_arrivee;
    private LocalTime heure_depart;

    private TypeHoraire type;

    private Employe employe;
}
