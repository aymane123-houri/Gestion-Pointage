package com.example.employeservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.employeservice.entity.Employe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class EmployeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeRepository employeRepository;

    private Employe employe1, employe2;

    @BeforeEach
    void setUp() {
        employe1 = new Employe(null, "EMP1000","EMP1", "EMP1", "EMP1@email.com",
                "1234", "0123456789", "123 Rue Larache","L123",
                new Date(2003/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());

        employe2 = new Employe(null, "EMP2000","EMP2", "EMP2", "EMP2@email.com",
                "1234", "0123456789", "123 Rue Larache","L1236",
                new Date(1992/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());

        entityManager.persist(employe1);
        entityManager.persist(employe2);
        entityManager.flush();
    }

    @Test
    void findLastMatricule_shouldReturnLastMatricule() {
        // When
        String lastMatricule = employeRepository.findLastMatricule();

        // Then
        assertThat(lastMatricule).isEqualTo("EMP2000");
    }

    @Test
    void findByNom_shouldReturnEmployeesWithSameName() {
        // Given
        Employe  employe3 = new Employe(null, "EMP3000","EMP3", "EMP3", "EMP3@email.com",
                "1234", "0123456789", "123 Rue Larache","L1234",
                new Date(2003/04/03),
                "M", "EMP001", "IT", new Date(),
                2000.00, "photo1.jpg".getBytes());
        entityManager.persist(employe3);

        // When
        List<Employe> result = employeRepository.findByNom("EMP3");

        // Then

        assertThat(result).extracting(Employe::getPrenom)
                .containsExactlyInAnyOrder( "EMP3");
    }

    @Test
    void findByDepartement_shouldReturnDepartmentEmployees() {
        // When
        List<Employe> result = employeRepository.findByDepartement("IT");

        // Then

        assertThat(result.get(0).getNom()).isEqualTo("EMP1");
    }
}