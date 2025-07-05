package com.example.pointageservice.service;


import com.example.pointageservice.entity.Pointage;
import com.example.pointageservice.feignPointage.EmployeFeignPointage;
import com.example.pointageservice.model.Employe;
import com.example.pointageservice.repository.PointageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointageServiceTest {

    @Mock
    private PointageRepository pointageRepository;

    @Mock
    private EmployeFeignPointage employeFeignPointage;

    @InjectMocks
    private PointageService pointageService;

    private Pointage pointage;
    private Employe employe;

    @BeforeEach
    void setUp() {
        pointage = new Pointage();
        pointage.setId("1");
        pointage.setEmployeId(1L);
        pointage.setDateHeureEntree(LocalDateTime.now().minusHours(2));
        pointage.setStatut("Présent");

        employe = new Employe();
        employe.setId(1L);
        employe.setNom("Dupont");
        employe.setPrenom("Jean");
    }

    @Test
    void createPointage_shouldCreateEntryWhenNoOpenPointage() {
        // Given
        when(pointageRepository.findByEmployeIdAndDateHeureEntreeBetween(anyLong(), any(), any()))
                .thenReturn(List.of());
        when(pointageRepository.save(any(Pointage.class))).thenReturn(pointage);

        // When
        Pointage result = pointageService.createPointage(1L);

        // Then
        assertThat(result.getDateHeureEntree()).isNotNull();
        assertThat(result.getDateHeureSortie()).isNull();
    }

    @Test
    void createPointage_shouldUpdateExitWhenOpenPointageExists() {
        // Given
        Pointage existingPointage = new Pointage();
        existingPointage.setId("2");
        existingPointage.setDateHeureEntree(LocalDateTime.now().minusHours(3));

        when(pointageRepository.findByEmployeIdAndDateHeureEntreeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(existingPointage));
        when(pointageRepository.save(any(Pointage.class))).thenReturn(existingPointage);

        // When
        Pointage result = pointageService.createPointage(1L);

        // Then
        assertThat(result.getDateHeureSortie()).isNotNull();
    }

    @Test
    void getPointageById_shouldReturnPointageWithEmploye() {
        // Given
        when(pointageRepository.findById("1")).thenReturn(Optional.of(pointage));
        when(employeFeignPointage.getEmployeById(1L)).thenReturn(employe);

        // When
        Pointage result = pointageService.getPointageById("1");

        // Then
        assertThat(result.getEmploye()).isNotNull();
        assertThat(result.getEmploye().getNom()).isEqualTo("Dupont");
    }

    @Test
    void updatePointage_shouldValidateEmployeId() {
        // Given
        Pointage updated = new Pointage();
        updated.setEmployeId(99L); // Invalid ID

        when(pointageRepository.findById("1")).thenReturn(Optional.of(pointage));
        when(employeFeignPointage.getEmployeById(99L)).thenReturn(null);

        // When / Then
        assertThatThrownBy(() -> pointageService.updatePointage(updated, "1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("n'existe pas");
    }

    @Test
    void getPointagesParEmployeEtDate_shouldReturnFilteredList() {
        // Given
        LocalDate date = LocalDate.now();
        when(pointageRepository.findByEmployeIdAndDateHeureEntreeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(pointage));

        // When
        List<Pointage> result = pointageService.getPointagesParEmployeEtDate(1L, date);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    void enregistrerPointageEntree_shouldPreventDuplicateEntry() {
        // Given
        pointage.setDateHeureEntree(LocalDateTime.now());
        when(pointageRepository.findTopByEmployeIdOrderByDateHeureEntreeDesc(1L))
                .thenReturn(Optional.of(pointage));

        // When / Then
        assertThatThrownBy(() -> pointageService.enregistrerPointageEntree(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà pointé");
    }
}