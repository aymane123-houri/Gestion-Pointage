package com.example.anomalieservice.service;

import com.example.anomalieservice.entity.Anomalie;
import com.example.anomalieservice.entity.StatutAnomalie;
import com.example.anomalieservice.entity.type_anomalie;
import com.example.anomalieservice.feignAnomalie.CongeFeignClient;
import com.example.anomalieservice.feignAnomalie.EmployeFeignAnomalie;
import com.example.anomalieservice.feignAnomalie.HoraireFeignAnomalie;
import com.example.anomalieservice.feignAnomalie.PointageFeignAnomalie;
import com.example.anomalieservice.model.Employe;
import com.example.anomalieservice.model.Horaire;
import com.example.anomalieservice.model.Pointage;
import com.example.anomalieservice.model.Statistique;
import com.example.anomalieservice.repository.AnomalieRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnomalieService {

    private final AnomalieRepository anomalieRepository;
    private EmployeFeignAnomalie employeFeignAnomalie;
    private PointageFeignAnomalie pointageFeignAnomalie;

    private HoraireFeignAnomalie horaireFeignAnomalie;

    private CongeFeignClient congeFeignClient;

    public AnomalieService(AnomalieRepository anomalieRepository, EmployeFeignAnomalie employeFeignAnomalie, PointageFeignAnomalie pointageFeignAnomalie, HoraireFeignAnomalie horaireFeignAnomalie, CongeFeignClient congeFeignClient) {
        this.anomalieRepository = anomalieRepository;
        this.employeFeignAnomalie = employeFeignAnomalie;
        this.pointageFeignAnomalie = pointageFeignAnomalie;
        this.horaireFeignAnomalie = horaireFeignAnomalie;
        this.congeFeignClient = congeFeignClient;
    }

    public Anomalie createAnomalie(Anomalie anomalie){
        Employe employe = employeFeignAnomalie.getEmployeById(anomalie.getEmploye_id());
        if (employe == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'employé avec l'ID " + anomalie.getEmploye_id() + " n'existe pas.");
        }
       return anomalieRepository.save(anomalie);
    }


    public Anomalie getAnomalieById(String id){
        Anomalie anomalie = anomalieRepository.findById(id).orElseThrow(() -> new RuntimeException("Anomalie non trouvée avec l'ID : " + id));
        //anomalie.setPointage(pointageFeignAnomalie.getpointageById(anomalie.getPointage_id()));
        anomalie.setEmploye(employeFeignAnomalie.getEmployeById(anomalie.getEmploye_id()));
        return anomalie;
    }

    public List<Anomalie> getAllAnomalie(){
        List<Anomalie> anomalieList= anomalieRepository.findAll();
        /*for(Anomalie a: anomalieList){
            a.setPointage(pointageFeignAnomalie.getpointageById(a.getPointage_id()));
        }*/
        for(Anomalie a: anomalieList){
            a.setEmploye(employeFeignAnomalie.getEmployeById(a.getEmploye_id()));
        }
        return anomalieList;
    }

    public Anomalie updateAnomalie(Anomalie anomalie,String id){
        return anomalieRepository.findById(id).map(anomalie1 -> {
            anomalie1.setDescription(anomalie.getDescription());
            anomalie1.setStatut(anomalie.getStatut());
            anomalie1.setType(anomalie.getType());
            anomalie1.setDateValidation(anomalie.getDateValidation());
            anomalie1.setPointage_id(anomalie.getPointage_id());
            anomalie1.setEmploye_id(anomalie.getEmploye_id());
                    return anomalieRepository.save(anomalie1);
                }

        ).orElseThrow((() -> new RuntimeException("anomalie not found")));

    }

    public void deleteAnomalie(String id){
        anomalieRepository.deleteById(id);
    }

    public void detecterAnomalies() {
        List<Horaire> horaires = horaireFeignAnomalie.getAllHoraires(); // Récupère les horaires
        for (Horaire h : horaires) {
            System.out.println("Horaire -> Employé ID: " + h.getEmployeId() +
                    ", Heure d'arrivée: " + h.getHeure_arrivee() +
                    ", Heure de départ: " + h.getHeure_depart());
        }




        LocalDate today = LocalDate.now();
        String todayStr = today.toString(); // Conversion LocalDate → String

        for (Horaire horaire : horaires) {
            Long employeId = horaire.getEmployeId();
            System.out.println("Aujourd'hui : " + today);

            // 🔹 Vérification si l'employé est en congé 🔹
            boolean enConge = congeFeignClient.isEmployeEnConge(employeId, todayStr);
            if (enConge) {
                System.out.println("✅ Employé " + employeId + " est en congé APPROUVÉ aujourd'hui. Aucune anomalie enregistrée.");
                continue; // Ignorer cet employé
            } else {
                System.out.println("🚨 Employé " + employeId + " n'est PAS en congé approuvé. Vérification des anomalies...");
            }



            List<Pointage> pointages = pointageFeignAnomalie.getPointagesParEmployeEtDate(employeId, todayStr);
            if (pointages == null || pointages.isEmpty()) {
                 if (!isWeekend(today)) { // 🔥 Vérifie si c'est un samedi ou dimanche
                     enregistrerAnomalie(employeId, type_anomalie.ABSENCE, "L'employé est absent un jour de travail.");
                 } else {
                     System.out.println("🛑 Absence ignorée pour " + employeId + " (Week end)");
                        }
                    }
            /*if (pointages == null || pointages.isEmpty()) {
                // Absence non justifiée
                enregistrerAnomalie(employeId, type_anomalie.ABSENCE, "L'employé n'a pas pointé aujourd'hui");

                continue;
            }*/

           /* for (Pointage pointage : pointages) {
                if (pointage.getDateHeureEntree() != null &&
                        pointage.getDateHeureEntree().toLocalTime().isAfter(horaire.getHeure_arrivee())) {
                    enregistrerAnomalie(employeId, type_anomalie.RETARD, "L'employé est arrivé en retard");
                }

                if (pointage.getDateHeureSortie() != null &&
                        pointage.getDateHeureSortie().toLocalTime().isBefore(horaire.getHeure_depart())) {
                    enregistrerAnomalie(employeId, type_anomalie.Départ_anticipe, "L'employé est parti avant l'heure prévue");
                }
            }*/
            for (Pointage pointage : pointages) {
                if (pointage.getDateHeureEntree() != null &&
                        pointage.getDateHeureEntree().toLocalTime().isAfter(horaire.getHeure_arrivee())) {

                    long minutesDeRetard = Duration.between(horaire.getHeure_arrivee(), pointage.getDateHeureEntree().toLocalTime()).toMinutes();

                    enregistrerAnomalie(employeId, type_anomalie.RETARD, "L'employé est arrivé en retard de " + minutesDeRetard + " minutes.");
                }

                if (pointage.getDateHeureSortie() != null &&
                        pointage.getDateHeureSortie().toLocalTime().isBefore(horaire.getHeure_depart())) {

                    long minutesAnticipation = Duration.between(pointage.getDateHeureSortie().toLocalTime(), horaire.getHeure_depart()).toMinutes();

                    enregistrerAnomalie(employeId, type_anomalie.Départ_anticipe, "L'employé est parti avant l'heure prévue de " + minutesAnticipation + " minutes.");
                }
            }
        }
    }

    private void enregistrerAnomalie(Long employeId, type_anomalie type, String description) {
        Anomalie anomalie = new Anomalie();
        anomalie.setEmploye_id(employeId);
        anomalie.setType(type); // Utilisation correcte de l'Enum
        anomalie.setDescription(description);
        anomalie.setStatut(StatutAnomalie.EN_ATTENTE);
        anomalie.setDateValidation(LocalDateTime.now());
        anomalieRepository.save(anomalie);
    }


    public Anomalie validerAnomalie(String id) {


        // Récupérer l'anomalie depuis MongoDB
        Anomalie anomalie = anomalieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anomalie non trouvée avec l'ID: " + id));

        // Vérifier que l'anomalie est bien "EN_ATTENTE"
        if (!anomalie.getStatut().equals(StatutAnomalie.EN_ATTENTE)) {
            throw new RuntimeException("L'anomalie n'est pas en attente, elle ne peut pas être validée.");
        }

        // Mettre à jour le statut de l'anomalie
        anomalie.setStatut(StatutAnomalie.VALIDE);
        anomalie.setDateValidation(LocalDateTime.now());

        // Sauvegarde dans MongoDB
        Anomalie savedAnomalie = anomalieRepository.save(anomalie);


        return savedAnomalie;
    }



    public List<Anomalie> findAnomaliesByDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        // Convertir LocalDateTime en ZonedDateTime (UTC)
        Instant startUtc = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
        Instant endUtc = endOfDay.atZone(ZoneId.systemDefault()).toInstant();

        System.out.println("🕒 Start of Day (UTC): " + startUtc);
        System.out.println("🕒 End of Day (UTC): " + endUtc);

        // Récupérer les anomalies en utilisant la date en UTC
        List<Anomalie> anomalies = anomalieRepository.findByDateValidationBetween(startUtc, endUtc);
        for(Anomalie a: anomalies){
            a.setEmploye(employeFeignAnomalie.getEmployeById(a.getEmploye_id()));
        }

        if (anomalies.isEmpty()) {
            System.out.println("⚠️ Aucune anomalie trouvée après conversion UTC !");
        }

        return anomalies;
    }



  /*  public List<Anomalie> getRetardsByEmployeAndPeriode(Long employeId, int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        // ✅ Convertir LocalDateTime en Instant (UTC) pour MongoDB
        Instant startInstant = startDate.toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.toInstant(ZoneOffset.UTC);

        System.out.println("🔍 Recherche des retards pour employé " + employeId + " entre " + startInstant + " et " + endInstant);

        List<Anomalie> retards = anomalieRepository.findRetardsByEmployeAndDate(
                employeId, startInstant, endInstant
        );

        System.out.println("📌 Nombre de retards trouvés: " + retards.size());
        return retards;
    }
*/

    public List<Anomalie> getAnomaliesByEmployeAndPeriode(Long employeId, int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        // ✅ Convertir LocalDateTime en Instant pour MongoDB
        Instant startInstant = startDate.toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.toInstant(ZoneOffset.UTC);

        System.out.println("🔍 Recherche des anomalies (Retard & Départ Anticipé) pour employé " + employeId);

        List<Anomalie> anomalies = anomalieRepository.findRetardsAndDepartAnticipeByEmployeAndDate(
                employeId, startInstant, endInstant
        );

        System.out.println("📌 Nombre d'anomalies trouvées (Retard & Départ Anticipé): " + anomalies.size());
        return anomalies;
    }


    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
