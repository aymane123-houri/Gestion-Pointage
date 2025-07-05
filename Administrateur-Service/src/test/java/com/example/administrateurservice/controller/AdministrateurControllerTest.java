package com.example.administrateurservice.controller;

import com.example.administrateurservice.entity.Administrateur;
import com.example.administrateurservice.entity.RoleAdministrateur;
import com.example.administrateurservice.service.AdministrateurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AdministrateurControllerTest {

    @Mock
    private AdministrateurService administrateurService;

    @InjectMocks
    private AdministrateurController administrateurController;

    private Administrateur admin1, admin2, newAdmin;

    @BeforeEach
    void setUp() {
        admin1 = new Administrateur(1L, "Aymane", "Houri", "LA12347", "aymane.houri@gmail.com", "123456", RoleAdministrateur.ADMINISTRATEUR, "0987654321", LocalDateTime.now());
        admin2 = new Administrateur(2L, "Aymane", "Houri", "LA12347", "aymane.houri@gmail.com", "123456", RoleAdministrateur.ADMINISTRATEUR, "0987654321", LocalDateTime.now());
        newAdmin = new Administrateur(null, "New", "Admin","L12", "new.admin@email.com",
                "password789", RoleAdministrateur.ADMINISTRATEUR, "0612345678", LocalDateTime.now());
    }

    @Test
    void createAdministrateur_shouldReturnCreatedAdmin() {
        // Given
        when(administrateurService.createAdministrateur(newAdmin)).thenReturn(admin1);

        // When
        ResponseEntity<Administrateur> response = administrateurController.CreateAdministrateur(newAdmin);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(admin1);
    }

    @Test
    void getAllAdministrateurs_shouldReturnAllAdmins() {
        // Given
        when(administrateurService.getAllAdministrateurs()).thenReturn(List.of(admin1, admin2));

        // When
        ResponseEntity<List<Administrateur>> response = administrateurController.GetAllAdministrateurs();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(admin1, admin2);
    }

    @Test
    void getAdministrateurById_shouldReturnAdmin_whenExists() {
        // Given
        when(administrateurService.getAdministrateurById(1L)).thenReturn(admin1);

        // When
        ResponseEntity<Administrateur> response = administrateurController.GetAdministrateurById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(admin1);
    }

    @Test
    void updateAdministrateur_shouldReturnUpdatedAdmin() {
        // Given
        Administrateur updatedAdmin = new Administrateur(1L, "Updated", "Name","L1",
                "updated@email.com", "newpass",
                RoleAdministrateur.ADMINISTRATEUR, "9999999999", LocalDateTime.now());

        when(administrateurService.updateAdministrateur(updatedAdmin, 1L)).thenReturn(updatedAdmin);

        // When
        ResponseEntity<Administrateur> response = administrateurController.UpdateAdministrateur(updatedAdmin, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedAdmin);
    }

    @Test
    void deleteAdministrateur_shouldCallService() {
        // When
        administrateurController.DeleteAdministrateur(1L);

        // Then
        verify(administrateurService).deleteAdministrateur(1L);
    }

    @Test
    void getAdministrateurByEmail_shouldReturnAdmin() {
        // Given
        when(administrateurService.getAdministrateurByEmail("aymane.houri@gmail.com"))
                .thenReturn(admin1);

        // When
        ResponseEntity<Administrateur> response = administrateurController.GetAdministrateurByEmail("aymane.houri@gmail.com");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(admin1);
    }
}