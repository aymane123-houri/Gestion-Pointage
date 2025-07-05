package com.example.anomalieservice.controller;


import com.example.anomalieservice.entity.Anomalie;
import com.example.anomalieservice.entity.StatutAnomalie;
import com.example.anomalieservice.entity.type_anomalie;
import com.example.anomalieservice.service.AnomalieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalieControllerTest {

    @Mock
    private AnomalieService anomalieService;

    @InjectMocks
    private AnomalieController anomalieController;

    private Anomalie anomalie;

    @BeforeEach
    void setUp() {
        anomalie = new Anomalie();
        anomalie.setId("1");
        anomalie.setEmploye_id(1L);
        anomalie.setType(type_anomalie.RETARD);
        anomalie.setStatut(StatutAnomalie.EN_ATTENTE);
        anomalie.setDescription("Retard de 15 minutes");
    }

    @Test
    void createAnomalie_shouldReturnCreatedAnomalie() {
        // Given
        when(anomalieService.createAnomalie(any(Anomalie.class))).thenReturn(anomalie);

        // When
        ResponseEntity<Anomalie> response = anomalieController.CreateAnomalie(anomalie);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(anomalie);
    }

    @Test
    void getAllAnomalie_shouldReturnListOfAnomalies() {
        // Given
        when(anomalieService.getAllAnomalie()).thenReturn(List.of(anomalie));

        // When
        ResponseEntity<List<Anomalie>> response = anomalieController.GetAllAnomalie();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo("1");
    }

    @Test
    void getAnomalieById_shouldReturnAnomalie() {
        // Given
        when(anomalieService.getAnomalieById("1")).thenReturn(anomalie);

        // When
        ResponseEntity<Anomalie> response = anomalieController.GetAnomalieById("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(anomalie);
    }

    @Test
    void updateAnomalie_shouldReturnUpdatedAnomalie() {
        // Given
        when(anomalieService.updateAnomalie(any(Anomalie.class), eq("1"))).thenReturn(anomalie);

        // When
        ResponseEntity<Anomalie> response = anomalieController.UpdateAnomalie(anomalie, "1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(anomalie);
    }

    @Test
    void validerAnomalie_shouldReturnValidatedAnomalie() {
        // Given
        Anomalie validated = new Anomalie();
        validated.setStatut(StatutAnomalie.VALIDE);

        when(anomalieService.validerAnomalie("1")).thenReturn(validated);

        // When
        ResponseEntity<?> response = anomalieController.validerAnomalie("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(validated);
    }

    @Test
    void validerAnomalie_shouldReturnBadRequestWhenError() {
        // Given
        when(anomalieService.validerAnomalie("1"))
                .thenThrow(new RuntimeException("Erreur de validation"));

        // When
        ResponseEntity<?> response = anomalieController.validerAnomalie("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAnomaliesDuJour_shouldReturnDailyAnomalies() {
        // Given
        when(anomalieService.findAnomaliesByDate(any(), any()))
                .thenReturn(List.of(anomalie));

        // When
        List<Anomalie> result = anomalieController.getAnomaliesDuJour();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("1");
    }

    @Test
    void getRetardsByEmploye_shouldCallServiceWithCorrectParams() {
        // Given
        when(anomalieService.getAnomaliesByEmployeAndPeriode(1L, 2023, 1))
                .thenReturn(List.of(anomalie));

        // When
        List<Anomalie> result = anomalieController.getRetardsByEmploye(1L, 2023, 1);

        // Then
        assertThat(result).hasSize(1);
        verify(anomalieService).getAnomaliesByEmployeAndPeriode(1L, 2023, 1);
    }
}