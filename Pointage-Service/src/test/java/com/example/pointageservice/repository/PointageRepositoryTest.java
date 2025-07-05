package com.example.pointageservice.repository;


import com.example.pointageservice.entity.Pointage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class PointageRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PointageRepository pointageRepository;

    private Pointage pointage1, pointage2;

    @BeforeEach
    void setUp() {
        pointage1 = new Pointage();
        pointage1.setEmployeId(1L);
        pointage1.setDateHeureEntree(LocalDateTime.now().minusHours(2));
        pointage1.setDateHeureSortie(LocalDateTime.now().minusHours(1));
        pointage1.setStatut("Présent");

        pointage2 = new Pointage();
        pointage2.setEmployeId(1L);
        pointage2.setDateHeureEntree(LocalDateTime.now().minusDays(1));
        pointage2.setStatut("En cours");

        mongoTemplate.save(pointage1);
        mongoTemplate.save(pointage2);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(Pointage.class);
    }

    @Test
    void findTopByEmployeIdOrderByDateHeureEntreeDesc_shouldReturnLatestPointage() {
        // When
        Optional<Pointage> result = pointageRepository.findTopByEmployeIdOrderByDateHeureEntreeDesc(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(pointage1.getId());
    }

    @Test
    void findByEmployeIdAndDateHeureEntreeBetween_shouldFilterByDateRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now();

        // When
        List<Pointage> result = pointageRepository.findByEmployeIdAndDateHeureEntreeBetween(1L, start, end);

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void findByDateHeureEntreeBetween_shouldReturnPointagesInDateRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusHours(3);
        LocalDateTime end = LocalDateTime.now().minusMinutes(30);

        // When
        List<Pointage> result = pointageRepository.findByDateHeureEntreeBetween(start, end);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pointage1.getId());
    }
}