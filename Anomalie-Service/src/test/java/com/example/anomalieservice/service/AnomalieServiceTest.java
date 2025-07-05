package com.example.anomalieservice.service;


import com.example.anomalieservice.entity.Anomalie;
import com.example.anomalieservice.entity.StatutAnomalie;
import com.example.anomalieservice.entity.type_anomalie;
import com.example.anomalieservice.feignAnomalie.*;
import com.example.anomalieservice.model.*;
import com.example.anomalieservice.repository.AnomalieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalieServiceTest {

    @Mock
    private AnomalieRepository anomalieRepository;

    @Mock
    private EmployeFeignAnomalie employeFeignAnomalie;

    @Mock
    private PointageFeignAnomalie pointageFeignAnomalie;

    @Mock
    private HoraireFeignAnomalie horaireFeignAnomalie;

    @Mock
    private CongeFeignClient congeFeignClient;

    @InjectMocks
    private AnomalieService anomalieService;

    private Anomalie anomalie;
    private Employe employe;
    private Horaire horaire;
    private Pointage pointage;

    @BeforeEach
    void setUp() {
        // Initialisation des objets de test
        anomalie = new Anomalie();
        anomalie.setId("1");
        anomalie.setEmploye_id(1L);
        anomalie.setType(type_anomalie.RETARD);
        anomalie.setStatut(StatutAnomalie.EN_ATTENTE);
        anomalie.setDescription("Retard de 15 minutes");

        employe = new Employe();
        employe.setId(1L);
        employe.setNom("Dupont");
        employe.setPrenom("Jean");

        horaire = new Horaire();
        horaire.setEmployeId(1L);
        horaire.setHeure_arrivee(LocalTime.of(8, 0));
        horaire.setHeure_depart(LocalTime.of(16, 30));

        pointage = new Pointage();
        pointage.setEmployeId(1L);
        pointage.setDateHeureEntree(LocalDateTime.now().withHour(9).withMinute(15)); // 1h15 de retard
    }

    @Test
    void createAnomalie_shouldSuccessWhenEmployeExists() {
        // Given
        when(employeFeignAnomalie.getEmployeById(1L)).thenReturn(employe);
        when(anomalieRepository.save(any(Anomalie.class))).thenReturn(anomalie);

        // When
        Anomalie result = anomalieService.createAnomalie(anomalie);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        verify(employeFeignAnomalie).getEmployeById(1L);
    }

    @Test
    void createAnomalie_shouldThrowWhenEmployeNotFound() {
        // Given
        when(employeFeignAnomalie.getEmployeById(1L)).thenReturn(null);

        // When / Then
        assertThatThrownBy(() -> anomalieService.createAnomalie(anomalie))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("n'existe pas");
    }

    @Test
    void validerAnomalie_shouldUpdateStatusToValid() {
        // Given
        when(anomalieRepository.findById("1")).thenReturn(Optional.of(anomalie));
        when(anomalieRepository.save(any(Anomalie.class))).thenReturn(anomalie);

        // When
        Anomalie result = anomalieService.validerAnomalie("1");

        // Then
        assertThat(result.getStatut()).isEqualTo(StatutAnomalie.VALIDE);
        assertThat(result.getDateValidation()).isNotNull();
    }

    @Test
    void validerAnomalie_shouldThrowWhenAnomalieNotFound() {
        // Given
        when(anomalieRepository.findById("99")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> anomalieService.validerAnomalie("99"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("non trouvée");
    }

    @Test
    void detecterAnomalies_shouldDetectRetard() {
        // Given
        when(horaireFeignAnomalie.getAllHoraires()).thenReturn(List.of(horaire));
        when(pointageFeignAnomalie.getPointagesParEmployeEtDate(anyLong(), anyString()))
                .thenReturn(List.of(pointage));
        when(congeFeignClient.isEmployeEnConge(anyLong(), anyString())).thenReturn(false);

        // When
        anomalieService.detecterAnomalies();

        // Then
        verify(anomalieRepository).save(argThat(a ->
                a.getType() == type_anomalie.RETARD &&
                        a.getDescription().contains("75 minutes")
        ));
    }

    @Test
    void detecterAnomalies_shouldDetectAbsence() {
        // Given
        when(horaireFeignAnomalie.getAllHoraires()).thenReturn(List.of(horaire));
        when(pointageFeignAnomalie.getPointagesParEmployeEtDate(anyLong(), anyString()))
                .thenReturn(Collections.emptyList());
        when(congeFeignClient.isEmployeEnConge(anyLong(), anyString())).thenReturn(false);

        // When
        anomalieService.detecterAnomalies();

        // Then
        verify(anomalieRepository).save(argThat(a ->
                a.getType() == type_anomalie.ABSENCE
        ));
    }

    @Test
    void detecterAnomalies_shouldIgnoreWeekends() {
        // Given - Configurer un dimanche (week-end)
        LocalDate weekendDate = LocalDate.of(2025, 6, 1); // Dimanche 1er juin 2025
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(weekendDate);

            Horaire horaireWeekend = new Horaire();
            horaireWeekend.setEmployeId(1L);
            horaireWeekend.setHeure_arrivee(LocalTime.of(8, 0));
            horaireWeekend.setHeure_depart(LocalTime.of(16, 30));

            when(horaireFeignAnomalie.getAllHoraires()).thenReturn(List.of(horaireWeekend));
            when(pointageFeignAnomalie.getPointagesParEmployeEtDate(anyLong(), anyString()))
                    .thenReturn(Collections.emptyList());
            when(congeFeignClient.isEmployeEnConge(anyLong(), anyString())).thenReturn(false);

            // When
            anomalieService.detecterAnomalies();

            // Then - Vérifier qu'aucune anomalie n'est enregistrée
            verify(anomalieRepository, never()).save(any());
        }
    }


}