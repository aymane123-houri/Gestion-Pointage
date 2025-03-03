package com.example.rapportservice.repository;

import com.example.rapportservice.entity.Rapport;
import com.example.rapportservice.entity.RapportDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RapportDetailsRepository extends MongoRepository<RapportDetail,String> {
    List<RapportDetail> findByJourAndEmployeId(LocalDate jour, Long employeId);
    List<RapportDetail> findByEmployeIdAndJourBetween(Long employeId, LocalDate start, LocalDate end);
    @Query("{ 'jour' : { $eq: ?0 } }")
    List<RapportDetail> findByJour(LocalDate jour);
}
