package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import com.example.notificationservice.model.Anomalie;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KafkaConsumerService {
    /*private final EmailService emailService;
    private final TwilioSmsService twilioSmsService;

    public KafkaConsumerService(EmailService emailService, TwilioSmsService twilioSmsService) {
        this.emailService = emailService;
        this.twilioSmsService = twilioSmsService;
    }

    // Consommer le message du topic 'notification-email_topic' et envoyer l'email
    @KafkaListener(topics = "notification-email_topic_absent", groupId = "pointage-groupe")
    public void consumeConfirmationEmailNotification(Notification notification) throws MessagingException {
        if (notification.getAnomalie() != null && notification.getAnomalie().getEmploye() != null) {
            String email = notification.getAnomalie().getEmploye().getEmail();
            String phoneNumber = notification.getAnomalie().getEmploye().getTelephone();
            if (email != null) {
                System.out.println("📧 Envoi d'email à: " + email);
                emailService.sendAbsenceNotification(notification);
            } else {
                System.out.println("❌ L'employé n'a pas d'email.");
            }
            // Vérifier si un numéro de téléphone est disponible et envoyer un SMS
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                String smsMessage = "⚠️ Absence non justifiée détectée ! Veuillez contacter votre responsable.";
                twilioSmsService.sendSms(phoneNumber, smsMessage);
                System.out.println("📲 SMS envoyé à " + phoneNumber);
            } else {
                System.out.println("❌ Aucun numéro de téléphone enregistré pour l'employé.");
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

*/
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final EmailService emailService;
    private final TwilioSmsService twilioSmsService;

    public KafkaConsumerService(EmailService emailService, TwilioSmsService twilioSmsService) {
        this.emailService = emailService;
        this.twilioSmsService = twilioSmsService;
    }

    @KafkaListener(topics = "notification-email_topic_absent", groupId = "pointage-groupe")
    public void consumeConfirmationEmailNotification(Notification notification) {
        Optional.ofNullable(notification)
                .map(Notification::getAnomalie)
                .map(Anomalie::getEmploye)
                .ifPresentOrElse(employe -> {
                    if (employe.getEmail() != null) {
                        try {
                            logger.info("📧 Envoi d'email à: {}", employe.getEmail());
                            emailService.sendAbsenceNotification(notification);
                        } catch (MessagingException e) {
                            logger.error("❌ Échec de l'envoi de l'email à {}", employe.getEmail(), e);
                        }
                    } else {
                        logger.warn("❌ L'employé n'a pas d'email.");
                    }

                    if (employe.getTelephone() != null && !employe.getTelephone().isEmpty()) {
                        String smsMessage = "⚠️ Absence non justifiée détectée ! Veuillez contacter votre responsable.";
                        //twilioSmsService.sendSms(employe.getTelephone(), smsMessage);
                        logger.info("📲 SMS envoyé à {}", employe.getTelephone());
                    } else {
                        logger.warn("❌ Aucun numéro de téléphone enregistré pour l'employé.");
                    }
                }, () -> logger.error("❌ L'anomalie ou l'employé est manquant."));
    }

    @KafkaListener(topics = "notification-email_topic_admin_absences", groupId = "pointage-groupe")
    public void consumeAdminAbsenceReport(NotificationAdmin notificationAdmin) {
        if (notificationAdmin != null && notificationAdmin.getAbsences() != null && !notificationAdmin.getAbsences().isEmpty()) {
            try {
                logger.info("📧 Envoi du rapport d'absences aux administrateurs...");
                emailService.sendAdminAbsenceReport(notificationAdmin);
            } catch (MessagingException e) {
                logger.error("❌ Échec de l'envoi du rapport d'absences", e);
            }
        } else {
            logger.info("✅ Aucun employé absent aujourd'hui. Pas de notification envoyée.");
        }
    }



}