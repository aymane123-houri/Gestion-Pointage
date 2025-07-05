package com.example.employeservice.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.example.employeservice.entity.Employe;
import com.example.employeservice.service.EmployeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerControllerTest {

    @Mock
    private EmployeService employeService;

    @InjectMocks
    private EmployerController employerController;

    private Employe employe1, employe2;

    @BeforeEach
    void setUp() {
        employe1 = new Employe(1L, "EMP1","EMP1", "EMP1", "EMP1@email.com",
                "1234", "0123456789", "123 Rue Larache","L123",
                new Date(2003/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());

        employe2 = new Employe(null, "EMP2","EMP2", "EMP2", "EMP2@email.com",
                "1234", "0123456789", "123 Rue Larache","L1236",
                new Date(1992/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());
    }

    @Test
    void createEmploye_shouldReturnCreatedEmploye() {
        // Given
        when(employeService.createEmploye(any(Employe.class))).thenReturn(employe1);

        // When
        ResponseEntity<Employe> response = employerController.CreateEmploye(employe1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employe1);
    }

    @Test
    void getAllEmploye_shouldReturnAllEmployees() {
        // Given
        when(employeService.GetAllEmploye()).thenReturn(List.of(employe1, employe2));

        // When
        ResponseEntity<List<Employe>> response = employerController.GetAllEmploye();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getEmployeById_shouldReturnEmploye() {
        // Given
        when(employeService.getEmployeById(1L)).thenReturn(employe1);

        // When
        ResponseEntity<Employe> response = employerController.GetEmployeById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employe1);
    }

    @Test
    void updateEmploye_shouldReturnUpdatedEmploye() {
        // Given
        when(employeService.updateEmploye(any(Employe.class), eq(1L))).thenReturn(employe1);

        // When
        ResponseEntity<Employe> response = employerController.UpdateEmploye(employe1, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employe1);
    }

    @Test
    void getLastMatricule_shouldReturnString() {
        // Given
        when(employeService.getLastMatricule()).thenReturn("EMP002");

        // When
        ResponseEntity<String> response = employerController.getLastMatricule();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("EMP002");
    }

    @Test
    void getNombreEmployes_shouldReturnCount() {
        // Given
        when(employeService.getNombreEmployes()).thenReturn(2L);

        // When
        long response = employerController.getNombreEmployes();

        // Then
        assertThat(response).isEqualTo(2L);
    }
}