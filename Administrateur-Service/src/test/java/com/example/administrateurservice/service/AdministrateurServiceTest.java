package com.example.administrateurservice.service;

import com.example.administrateurservice.entity.Administrateur;
import com.example.administrateurservice.entity.RoleAdministrateur;
import com.example.administrateurservice.repository.AdministrateurRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AdministrateurServiceTest {

    @Mock
    private AdministrateurRepository administrateurRepository;

    @InjectMocks
    private AdministrateurService administrateurService;

    private Administrateur admin1, admin2, newAdmin;

    @BeforeEach
    void setUp() {
        admin1 = new Administrateur(1L, "Aymane", "Houri", "LA12347", "aymane.houri@gmail.com", "123456", RoleAdministrateur.ADMINISTRATEUR, "0987654321", LocalDateTime.now());
        admin2 = new Administrateur(2L, "Ayoub", "Houri", "LA1234712", "houriayoub@gmail.com", "1234567", RoleAdministrateur.ADMINISTRATEUR, "0987654312", LocalDateTime.now());
        newAdmin = new Administrateur(null, "New", "Admin","L12", "new.admin@email.com",
                "password789", RoleAdministrateur.ADMINISTRATEUR, "0612345678", LocalDateTime.now());
    }

    @Test
    void createAdministrateur_shouldSaveAndReturnAdmin() {
        // Given
        when(administrateurRepository.save(any(Administrateur.class))).thenReturn(admin1);

        // When
        Administrateur result = administrateurService.createAdministrateur(newAdmin);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(administrateurRepository).save(newAdmin);
    }

    @Test
    void getAdministrateurById_shouldReturnAdmin_whenExists() {
        // Given
        when(administrateurRepository.findById(1L)).thenReturn(Optional.of(admin1));

        // When
        Administrateur result = administrateurService.getAdministrateurById(1L);

        // Then
        assertThat(result).isEqualTo(admin1);
    }

    @Test
    void getAdministrateurById_shouldReturnNull_whenNotExists() {
        // Given
        when(administrateurRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Administrateur result = administrateurService.getAdministrateurById(99L);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void getAllAdministrateurs_shouldReturnAllAdmins() {
        // Given
        when(administrateurRepository.findAll()).thenReturn(List.of(admin1, admin2));

        // When
        List<Administrateur> result = administrateurService.getAllAdministrateurs();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(admin1, admin2);
    }

    @Test
    void updateAdministrateur_shouldUpdateAndReturnAdmin_whenExists() {
        // Given
        Administrateur updatedAdmin = new Administrateur(1L, "Updated", "Name","L1",
                "updated@email.com", "newpass",
                RoleAdministrateur.ADMINISTRATEUR, "9999999999", LocalDateTime.now());

        when(administrateurRepository.findById(1L)).thenReturn(Optional.of(admin1));
        when(administrateurRepository.save(any(Administrateur.class))).thenReturn(updatedAdmin);

        // When
        Administrateur result = administrateurService.updateAdministrateur(updatedAdmin, 1L);

        // Then
        assertThat(result).isEqualTo(updatedAdmin);
        verify(administrateurRepository).save(admin1);
    }

    @Test
    void updateAdministrateur_shouldThrowException_whenNotExists() {
        // Given
        when(administrateurRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> administrateurService.updateAdministrateur(newAdmin, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("administrateur not found");
    }

    @Test
    void deleteAdministrateur_shouldCallDelete() {
        // When
        administrateurService.deleteAdministrateur(1L);

        // Then
        verify(administrateurRepository).deleteById(1L);
    }

    @Test
    void getAdministrateurByEmail_shouldReturnAdmin_whenEmailExists() {
        // Given
        when(administrateurRepository.getAdministrateurByEmail("aymane.houri@gmail.com"))
                .thenReturn(admin1);

        // When
        Administrateur result = administrateurService.getAdministrateurByEmail("aymane.houri@gmail.com");

        // Then
        assertThat(result).isEqualTo(admin1);
    }

}