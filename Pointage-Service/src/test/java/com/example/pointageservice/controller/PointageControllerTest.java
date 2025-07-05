package com.example.pointageservice.controller;


import com.example.pointageservice.entity.Pointage;
import com.example.pointageservice.model.Employe;
import com.example.pointageservice.service.PointageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointageControllerTest {

    @Mock
    private PointageService pointageService;

    @InjectMocks
    private PointageController pointageController;

    private Pointage pointage;
    private Employe employe;

    @BeforeEach
    void setUp() {
        pointage = new Pointage();
        pointage.setId("1");
        pointage.setEmployeId(1L);
        pointage.setDateHeureEntree(LocalDateTime.now());

        employe = new Employe();
        employe.setId(1L);
        employe.setNom("Test");
    }

    @Test
    void enregistrerPointage_shouldHandleEntry() {
        // Given
        when(pointageService.createPointage(1L)).thenReturn(pointage);

        // When
        ResponseEntity<?> response = pointageController.enregistrerPointage(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).contains("entrée");
    }

    @Test
    void getHistoriquePointages_shouldReturnList() {
        // Given
        when(pointageService.getHistoriquePointages(1L)).thenReturn(List.of(pointage));

        // When
        ResponseEntity<?> response = pointageController.getHistoriquePointages(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) response.getBody()).hasSize(1);
    }

    @Test
    void getPointages_shouldFilterByDate() {
        // Given
        String date = "2023-01-01";
        when(pointageService.getPointagesParEmployeEtDate(1L, LocalDate.parse(date)))
                .thenReturn(List.of(pointage));

        // When
        ResponseEntity<List<Pointage>> response = pointageController.getPointages(1L, date);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getPointages_shouldReturnNotFoundForEmptyList() {
        // Given
        String date = "2023-01-01";
        when(pointageService.getPointagesParEmployeEtDate(1L, LocalDate.parse(date)))
                .thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Pointage>> response = pointageController.getPointages(1L, date);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllPointages_shouldFilterByDateRange() {
        // Given
        when(pointageService.getAllPointages("2023-01-01", "2023-01-31"))
                .thenReturn(List.of(pointage));

        // When
        List<Pointage> result = pointageController.getAllPointages("2023-01-01", "2023-01-31");

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void getPointagesAujourdhui_shouldUseCurrentDateWhenNotProvided() {
        // Given
        when(pointageService.getPointagesAujourdhui(1L, LocalDate.now()))
                .thenReturn(List.of(pointage));

        // When
        ResponseEntity<List<Pointage>> response = pointageController.getPointagesAujourdhui(1L, null);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}