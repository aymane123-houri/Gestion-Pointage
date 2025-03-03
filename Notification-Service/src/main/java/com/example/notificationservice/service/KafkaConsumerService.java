package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import com.example.notificationservice.model.Anomalie;
import jakarta.mail.MessagingException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumerService {
    private final EmailService emailService;

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    // Consommer le message du topic 'notification-email_topic' et envoyer l'email
    @KafkaListener(topics = "notification-email_topic_absent", groupId = "pointage-groupe")
    public void consumeConfirmationEmailNotification(Notification notification) throws MessagingException {
        if (notification.getAnomalie() != null && notification.getAnomalie().getEmploye() != null) {
            String email = notification.getAnomalie().getEmploye().getEmail();
            if (email != null) {
                System.out.println("📧 Envoi d'email à: " + email);
                emailService.sendAbsenceNotification(notification);
            } else {
                System.out.println("❌ L'employé n'a pas d'email.");
            }
        } else {
            System.out.println("❌ L'anomalie ou l'employé est manquant.");
        }
    }

    // Autre méthode pour consommer d'autres notifications si nécessaire
    @KafkaListener(topics = "notification-email_topic_admin_absences", groupId = "pointage-groupe")
    public void consumeAdminAbsenceReport(NotificationAdmin notificationAdmin) throws MessagingException {
        if (notificationAdmin != null && notificationAdmin.getAbsences() != null && !notificationAdmin.getAbsences().isEmpty()) {
            System.out.println("📧 Envoi du rapport d'absences aux administrateurs...");
            emailService.sendAdminAbsenceReport(notificationAdmin);
        } else {
            System.out.println("✅ Aucun employé absent aujourd'hui. Pas de notification envoyée.");
        }
    }





}