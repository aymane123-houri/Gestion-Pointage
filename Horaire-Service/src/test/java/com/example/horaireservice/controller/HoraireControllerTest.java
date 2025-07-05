
package com.example.horaireservice.controller;

import com.example.horaireservice.entity.Horaire;
import com.example.horaireservice.entity.TypeHoraire;
import com.example.horaireservice.model.Employe;
import com.example.horaireservice.service.HoraireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoraireControllerTest {

    @Mock
    private HoraireService horaireService;

    @InjectMocks
    private HoraireController horaireController;

    private Horaire horaire;
    private Employe employe;

    @BeforeEach
    void setUp() {
        horaire = new Horaire();
        horaire.setId(1L);
        horaire.setEmployeId(1L);
        horaire.setHeure_arrivee(LocalTime.of(8, 0));
        horaire.setHeure_depart(LocalTime.of(16, 30));
        horaire.setType(TypeHoraire.FIXE);

        employe = new Employe();
        employe.setId(1L);
        employe.setNom("Dupont");
        employe.setPrenom("Jean");
    }

    @Test
    void createHoraire_shouldReturnCreatedHoraire() {
        // Given
        when(horaireService.createHoraire(any(Horaire.class))).thenReturn(horaire);

        // When
        ResponseEntity<Horaire> response = horaireController.createHoraire(horaire);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(horaire);
    }

    @Test
    void getHoraireById_shouldReturnHoraire() {
        // Given
        when(horaireService.getHoraireById(1L)).thenReturn(horaire);

        // When
        ResponseEntity<Horaire> response = horaireController.getHoraireById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(horaire);
    }

    @Test
    void getAllHoraire_shouldReturnList() {
        // Given
        when(horaireService.getAllHoraire()).thenReturn(List.of(horaire));

        // When
        ResponseEntity<List<Horaire>> response = horaireController.getAllHoraire();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void updateHoraire_shouldReturnUpdatedHoraire() {
        // Given
        when(horaireService.updateHoraire(any(Horaire.class), eq(1L))).thenReturn(horaire);

        // When
        ResponseEntity<Horaire> response = horaireController.updateHoraire(horaire, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(horaire);
    }

    @Test
    void getHorairesParEmploye_shouldReturnList() {
        // Given
        when(horaireService.getHorairesParEmploye(1L)).thenReturn(List.of(horaire));

        // When
        List<Horaire> response = horaireController.getHorairesParEmploye(1L);

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void rechercherEmployeParNom_shouldReturnEmployes() {
        // Given
        when(horaireService.rechercherEmployeParNom("Dupont")).thenReturn(List.of(employe));

        // When
        ResponseEntity<List<Employe>> response = horaireController.rechercherEmployeParNom("Dupont");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}