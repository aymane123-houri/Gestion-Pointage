package com.example.administrateurservice.repository;

import com.example.administrateurservice.entity.Administrateur;
import com.example.administrateurservice.entity.RoleAdministrateur;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdministrateurRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    private Administrateur admin1, admin2;

    @BeforeEach
    void setUp() {
        admin1 = new Administrateur(null, "Aymane", "Houri", "LA12347", "aymane.houri@gmail.com", "123456", RoleAdministrateur.ADMINISTRATEUR, "0987654321", LocalDateTime.now());
        admin2 = new Administrateur(null, "Ayoub", "Houri", "LA1234712", "houriayoub@gmail.com", "1234567", RoleAdministrateur.ADMINISTRATEUR, "0987654312", LocalDateTime.now());

        entityManager.persist(admin1);
        entityManager.persist(admin2);
        entityManager.flush();
    }

    @Test
    void getAdministrateurByEmail_shouldReturnAdmin_whenEmailExists() {
        // When
        Administrateur found = administrateurRepository.getAdministrateurByEmail(admin1.getEmail());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(admin1.getEmail());
        assertThat(found).usingRecursiveComparison().isEqualTo(admin1);
    }

    @Test
    void getAdministrateurByEmail_shouldReturnNull_whenEmailNotExists() {
        // When
        Administrateur found = administrateurRepository.getAdministrateurByEmail("nonexistent@email.com");

        // Then
        assertThat(found).isNull();
    }

}