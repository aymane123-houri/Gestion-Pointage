package com.example.anomalieservice.repository;


import com.example.anomalieservice.entity.Anomalie;
import com.example.anomalieservice.entity.StatutAnomalie;
import com.example.anomalieservice.entity.type_anomalie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AnomalieRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AnomalieRepository anomalieRepository;

    private Anomalie anomalie1, anomalie2;

    @BeforeEach
    void setUp() {
        // Création de deux anomalies de test
        anomalie1 = new Anomalie();
        anomalie1.setId("1");
        anomalie1.setEmploye_id(1L);
        anomalie1.setType(type_anomalie.RETARD);
        anomalie1.setStatut(StatutAnomalie.EN_ATTENTE);

        anomalie1.setDescription("Retard de 15 minutes");

        anomalie2 = new Anomalie();
        anomalie2.setId("2");
        anomalie2.setEmploye_id(2L);
        anomalie2.setType(type_anomalie.ABSENCE);
        anomalie2.setStatut(StatutAnomalie.VALIDE);

        anomalie2.setDescription("Absence non justifiée");

        mongoTemplate.save(anomalie1);
        mongoTemplate.save(anomalie2);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(Anomalie.class);
    }

    /*@Test
    void findByDateValidationBetween_shouldReturnAnomaliesInDateRange() {
        // Given
        Instant start = LocalDateTime.now().minusDays(2).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC);

        // When
        List<Anomalie> result = anomalieRepository.findByDateValidationBetween(start, end);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Anomalie::getId)
                .containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void findRetardsAndDepartAnticipeByEmployeAndDate_shouldFilterCorrectly() {
        // Given - Créer des anomalies spécifiques pour le test
        Anomalie retard = new Anomalie();
        retard.setId("3");
        retard.setEmploye_id(1L);
        retard.setType(type_anomalie.RETARD);
        retard.setStatut(StatutAnomalie.EN_ATTENTE);


        Anomalie absence = new Anomalie();
        absence.setId("4");
        absence.setEmploye_id(1L);
        absence.setType(type_anomalie.ABSENCE); // Ne devrait pas être retournée
        absence.setStatut(StatutAnomalie.VALIDE);


        mongoTemplate.save(retard);
        mongoTemplate.save(absence);

        // When
        Instant start = LocalDateTime.now().minusDays(2).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        List<Anomalie> result = anomalieRepository.findRetardsAndDepartAnticipeByEmployeAndDate(
                1L, start, end);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(Anomalie::getType)
                .containsExactly(type_anomalie.RETARD);
    }*/

    @Test
    void whenFindAll_thenReturnAllAnomalies() {
        // When
        List<Anomalie> result = anomalieRepository.findAll();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void whenFindById_thenReturnAnomalie() {
        // When
        Anomalie found = anomalieRepository.findById("1").orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getDescription()).isEqualTo("Retard de 15 minutes");
    }
}