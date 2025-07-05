package com.example.horaireservice.repository;


import com.example.horaireservice.entity.Horaire;
import com.example.horaireservice.entity.TypeHoraire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HoraireRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HoraireRepository horaireRepository;

    private Horaire horaire1, horaire2;

    @BeforeEach
    void setUp() {
        horaire1 = new Horaire();
        horaire1.setEmployeId(1L);
        horaire1.setHeure_arrivee(LocalTime.of(8, 0));
        horaire1.setHeure_depart(LocalTime.of(16, 30));
        horaire1.setType(TypeHoraire.FIXE);

        horaire2 = new Horaire();
        horaire2.setEmployeId(1L);
        horaire2.setHeure_arrivee(LocalTime.of(9, 0));
        horaire2.setHeure_depart(LocalTime.of(17, 30));
        horaire2.setType(TypeHoraire.FLEXIBLE);

        entityManager.persist(horaire1);
        entityManager.persist(horaire2);
        entityManager.flush();
    }

    @Test
    void findByEmployeId_shouldReturnHorairesForEmployee() {
        // When
        List<Horaire> result = horaireRepository.findByEmployeId(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Horaire::getType)
                .containsExactlyInAnyOrder(TypeHoraire.FIXE, TypeHoraire.FLEXIBLE);
    }

    @Test
    void findByEmployeId_shouldReturnEmptyList_whenNoMatches() {
        // When
        List<Horaire> result = horaireRepository.findByEmployeId(99L);

        // Then
        assertThat(result).isEmpty();
    }
}