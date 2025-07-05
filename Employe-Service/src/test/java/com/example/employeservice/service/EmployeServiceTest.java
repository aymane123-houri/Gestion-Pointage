package com.example.employeservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.employeservice.entity.Employe;
import com.example.employeservice.repository.EmployeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private EmployeService employeService;

    private Employe employe1, employe2;

    @BeforeEach
    void setUp() {
        employe1 = new Employe(1L, "EMP1","EMP1", "EMP1", "EMP1@email.com",
                "1234", "0123456789", "123 Rue Larache","L123",
                new Date(2003/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());

        employe2 = new Employe(2L, "EMP2","EMP2", "EMP2", "EMP2@email.com",
                "1234", "0123456789", "123 Rue Larache","L1236",
                new Date(1992/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());
    }

    @Test
    void createEmploye_shouldSaveAndReturnEmploye() {
        // Given
        when(employeRepository.save(any(Employe.class))).thenReturn(employe1);

        // When
        Employe result = employeService.createEmploye(employe1);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        verify(employeRepository).save(employe1);
    }

    @Test
    void getEmployeById_shouldReturnEmploye_whenExists() {
        // Given
        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe1));

        // When
        Employe result = employeService.getEmployeById(1L);

        // Then
        assertThat(result).isEqualTo(employe1);
    }

    @Test
    void getEmployeById_shouldReturnNull_whenNotExists() {
        // Given
        when(employeRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Employe result = employeService.getEmployeById(99L);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void updateEmploye_shouldUpdateAllFields() {
        // Given
        Employe updatedEmploye = new Employe(null, "EMP4","EMP4", "EMP4", "EMP1@email.com",
                "1234", "0123456789", "123 Rue Larache","L123",
                new Date(2003/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "NEW_photo1.jpg".getBytes());

        when(employeRepository.findById(1L)).thenReturn(Optional.of(employe1));
        when(employeRepository.save(any(Employe.class))).thenReturn(updatedEmploye);

        // When
        Employe result = employeService.updateEmploye(updatedEmploye, 1L);

        // Then
        assertThat(result.getPrenom()).isEqualTo("Jean-Paul");
        assertThat(result.getSalaire()).isEqualTo(55000.0);
    }

    @Test
    void getLastMatricule_shouldReturnDefault_whenNoEmployees() {
        // Given
        when(employeRepository.findLastMatricule()).thenReturn(null);

        // When
        String result = employeService.getLastMatricule();

        // Then
        assertThat(result).isEqualTo("EMP0");
    }

    @Test
    void getNombreEmployes_shouldReturnCount() {
        // Given
        when(employeRepository.count()).thenReturn(2L);

        // When
        long result = employeService.getNombreEmployes();

        // Then
        assertThat(result).isEqualTo(2L);
    }
}