package com.example.horaireservice.service;


import com.example.horaireservice.entity.Horaire;
import com.example.horaireservice.entity.TypeHoraire;
import com.example.horaireservice.feignHoraire.EmployeFeignHoraire;
import com.example.horaireservice.model.Employe;
import com.example.horaireservice.repository.HoraireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoraireServiceTest {

    @Mock
    private HoraireRepository horaireRepository;

    @Mock
    private EmployeFeignHoraire employeFeignPointage;

    @InjectMocks
    private HoraireService horaireService;

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
    void createHoraire_shouldSaveAndReturnHoraire() {
        // Given
        when(horaireRepository.save(any(Horaire.class))).thenReturn(horaire);

        // When
        Horaire result = horaireService.createHoraire(horaire);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        verify(horaireRepository).save(horaire);
    }

    @Test
    void getHoraireById_shouldReturnHoraireWithEmploye() {
        // Given
        when(horaireRepository.findById(1L)).thenReturn(Optional.of(horaire));
        when(employeFeignPointage.getEmployeById(1L)).thenReturn(employe);

        // When
        Horaire result = horaireService.getHoraireById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmploye()).isNotNull();
        assertThat(result.getEmploye().getNom()).isEqualTo("Dupont");
    }

    @Test
    void getAllHoraire_shouldReturnAllWithEmployes() {
        // Given
        when(horaireRepository.findAll()).thenReturn(List.of(horaire));
        when(employeFeignPointage.getEmployeById(1L)).thenReturn(employe);

        // When
        List<Horaire> result = horaireService.getAllHoraire();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmploye()).isNotNull();
    }

    @Test
    void updateHoraire_shouldUpdateFields() {
        // Given
        Horaire updatedHoraire = new Horaire();
        updatedHoraire.setHeure_arrivee(LocalTime.of(9, 0));
        updatedHoraire.setHeure_depart(LocalTime.of(17, 30));
        updatedHoraire.setType(TypeHoraire.FLEXIBLE);
        updatedHoraire.setEmployeId(2L);

        when(horaireRepository.findById(1L)).thenReturn(Optional.of(horaire));
        when(horaireRepository.save(any(Horaire.class))).thenReturn(updatedHoraire);

        // When
        Horaire result = horaireService.updateHoraire(updatedHoraire, 1L);

        // Then
        assertThat(result.getHeure_arrivee()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getType()).isEqualTo(TypeHoraire.FLEXIBLE);
    }

    @Test
    void getHorairesParEmploye_shouldReturnHorairesForEmployee() {
        // Given
        when(horaireRepository.findByEmployeId(1L)).thenReturn(List.of(horaire));

        // When
        List<Horaire> result = horaireService.getHorairesParEmploye(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeId()).isEqualTo(1L);
    }

    @Test
    void rechercherEmployeParNom_shouldCallFeignClient() {
        // Given
        when(employeFeignPointage.rechercherEmployeParNom("Dupont")).thenReturn(List.of(employe));

        // When
        List<Employe> result = horaireService.rechercherEmployeParNom("Dupont");

        // Then
        assertThat(result).hasSize(1);
        verify(employeFeignPointage).rechercherEmployeParNom("Dupont");
    }
}