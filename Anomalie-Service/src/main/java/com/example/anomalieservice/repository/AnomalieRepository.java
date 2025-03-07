package com.example.anomalieservice.repository;


import com.example.anomalieservice.entity.Anomalie;
import com.example.anomalieservice.entity.type_anomalie;
import feign.Param;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public interface AnomalieRepository extends MongoRepository<Anomalie,String> {

    List<Anomalie> findByEmployeId(Long employeId);
    List<Anomalie> findByStatut(String statut);
    long countBydateValidationBetween(LocalDate debut, LocalDate fin);
    // Recherche des anomalies par plage de dates (entre startOfDay et endOfDay)
    // Recherche des anomalies par plage de dates (entre startOfDay et endOfDay)

    @Query("{ 'dateValidation': { $gte: ?0, $lte: ?1 } }")
    List<Anomalie> findByDateValidationBetween(Instant startOfDay, Instant endOfDay);

    @Query("{ 'employe_id': ?0, 'dateValidation': { $gte: ?1, $lte: ?2 }, 'type': { $in: ['RETARD', 'Départ_anticipe'] } }")
    List<Anomalie> findRetardsAndDepartAnticipeByEmployeAndDate(Long employe_id, Instant startDate, Instant endDate);


}
