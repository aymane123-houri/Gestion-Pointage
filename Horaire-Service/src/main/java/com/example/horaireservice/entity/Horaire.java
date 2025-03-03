package com.example.horaireservice.entity;


import com.example.horaireservice.model.Employe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Horaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private Long employe_id;
    private Long employeId;

    private LocalTime heure_arrivee;
    private LocalTime heure_depart;
    @Enumerated(EnumType.STRING)
    private TypeHoraire type;

    @Transient
    private Employe employe;
}
