package com.example.notificationservice.controller;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import com.example.notificationservice.feignClient.AnomalieFeignClient;
import com.example.notificationservice.feignClient.HoraireFeignClient;
import com.example.notificationservice.model.Anomalie;
import com.example.notificationservice.model.Horaire;
import com.example.notificationservice.model.StatutAnomalie;
import com.example.notificationservice.model.type_anomalie;
import com.example.notificationservice.service.KafkaProducerService;
import com.example.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/Notification")
public class NotificationController {

    private final NotificationService service;
    private final KafkaProducerService kafkaProducerService;
    private final AnomalieFeignClient anomalieFeignClient;
    private final HoraireFeignClient horaireFeignClient;

    public NotificationController(NotificationService service, KafkaProducerService kafkaProducerService, AnomalieFeignClient anomalieFeignClient, HoraireFeignClient horaireFeignClient) {
        this.service = service;
        this.kafkaProducerService = kafkaProducerService;
        this.anomalieFeignClient = anomalieFeignClient;
        this.horaireFeignClient = horaireFeignClient;
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification){
        return service.createNotification(notification);
    }

    @PutMapping("/{id}")
    public Notification updateNotification(@PathVariable String id , @RequestBody Notification notification){
        notification.set_id(id);
        return service.updateNotification(notification);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable String id){
        service.deleteNotification(id);
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable String id){
        return service.getById(id);
    }

    @GetMapping
    public List<Notification> getAll(){
        return service.getAll();
    }

   /* @PostMapping("/send-email/{anomalieId}")
    public ResponseEntity<String> sendEmailNotification(@PathVariable String anomalieId,@PathVariable Long employeId) {
        Anomalie anomalie = anomalieFeignClient.getAnomalieById(anomalieId);
        Horaire horaire= horaireFeignClient.getHoraire(anomalie.getEmpoloyer_id());
        try {
            Notification notification = new Notification(new Date(), anomalie,horaire);

            kafkaProducerService.sendConfirmationEmail(notification);
            service.createNotification(notification);

            return ResponseEntity.ok("Notification envoyée avec succès !");
        }
        catch (Exception e){
            return ResponseEntity.ok("Error caused by: "+e.getMessage());
        }
    }*/

// Méthode pour détecter les absences tous les jours à 6h00
    @Scheduled(cron = "0 23 22 * * ?")
    public void detectAbsencesAndSendEmails() {
        // Tu peux récupérer toutes les anomalies du jour via ton client Feign
        List<Anomalie> anomalies = anomalieFeignClient.getAnomaliesDuJour();
        if (anomalies == null || anomalies.isEmpty()) {
            System.out.println("❌ Aucune anomalie trouvée aujourd'hui !");
            return;
        }
        for (Anomalie anomalie : anomalies) {
            // Récupère les horaires de l'employé (liste)
            List<Horaire> horaires = horaireFeignClient.getHoraire(anomalie.getEmploye_id());

            if (horaires == null || horaires.isEmpty()) {
                System.out.println("❌ Aucun horaire trouvé pour l'employé ID: " + anomalie.getEmploye_id());
                continue;
            }
            // Prendre le premier horaire (ou appliquer une logique spécifique)
            Horaire horaire = horaires.get(0);

            // Crée une notification pour l'employé absent
            Notification notification = new Notification(new Date(), anomalie, horaire);

            // Envoie la notification via Kafka
            kafkaProducerService.sendAbsenceNotification(notification);
            System.out.println("✅ Notification envoyée à Kafka pour employé ID: " + anomalie.getEmploye_id());
            }
        }


    @Scheduled(cron = "0 23 22 * * ?")
    public void detectAbsencesAndSendAdminEmails() {
        // Récupérer toutes les anomalies du jour via le client Feign
        List<Anomalie> anomalies = anomalieFeignClient.getAnomaliesDuJour();
        if (anomalies == null || anomalies.isEmpty()) {
            System.out.println("❌ Aucune anomalie trouvée aujourd'hui !");
            return;
        }

        // Liste des absences (uniquement les absents)
        List<Anomalie> absents = anomalies.stream()
                .filter(anomalie -> anomalie.getType() == type_anomalie.ABSENCE)
                .collect(Collectors.toList());

        if (absents.isEmpty()) {
            System.out.println("❌ Aucun employé absent aujourd'hui !");
            return;
        }

        // Crée une notification pour le rapport d'absences de l'admin
        NotificationAdmin notificationAdmin = new NotificationAdmin(new Date(), absents);

        // Envoie la notification via Kafka
        kafkaProducerService.sendAdminAbsenceNotification(notificationAdmin);
        System.out.println("✅ Notification envoyée à Kafka pour le rapport d'absences.");
    }


    // Point d'API pour tester manuellement l'envoi des emails d'absences
    @PostMapping("/send-daily-absences")
    public ResponseEntity<String> sendDailyAbsencesNotification() {
        detectAbsencesAndSendEmails();
        return ResponseEntity.ok("Emails envoyés pour les absences du jour !");
    }
}
