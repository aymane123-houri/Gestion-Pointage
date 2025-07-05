package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    /*private final KafkaTemplate<String, Notification> kafkaTemplate1;
    private final KafkaTemplate<String, NotificationAdmin> kafkaTemplate2;
    private final TwilioSmsService twilioService;

    public KafkaProducerService(KafkaTemplate<String, Notification> kafkaTemplate1, KafkaTemplate<String, NotificationAdmin> kafkaTemplate2, TwilioSmsService twilioService) {
        this.kafkaTemplate1 = kafkaTemplate1;
        this.kafkaTemplate2 = kafkaTemplate2;
        this.twilioService = twilioService;
    }

    // Méthode pour envoyer la notification d'absence
    public void sendAbsenceNotification(Notification notification) {

        if (notification.getAnomalie() != null && notification.getAnomalie().getEmploye() != null) {
            Long employeId = notification.getAnomalie().getEmploye().getId();
            String employePhone = notification.getAnomalie().getEmploye().getTelephone();

            System.out.println("✅ Notification envoyée à Kafka pour employé ID: " + employeId);
            kafkaTemplate1.send("notification-email_topic_absent", notification);

            // Envoi de SMS si le numéro est disponible
            if (employePhone != null && !employePhone.isEmpty()) {
                String smsMessage = "⚠️ Absence non justifiée détectée ! Veuillez contacter votre responsable.";
                twilioService.sendSms(employePhone, smsMessage);
            } else {
                System.out.println("❌ Aucun numéro de téléphone enregistré pour l'employé ID: " + employeId);
            }
        } else {
            System.out.println("❌ L'employé est manquant dans l'anomalie, notification non envoyée.");
        }
    }


    // Autre méthode de production, si nécessaire, comme pour un autre type de notification
    public void sendAdminAbsenceNotification(NotificationAdmin notificationAdmin) {
        // Vérifie si le rapport d'absences est bien renseigné
        if (notificationAdmin != null && notificationAdmin.getAbsences() != null && !notificationAdmin.getAbsences().isEmpty()) {
            System.out.println("✅ Notification envoyée à Kafka pour les absences des employés.");

            // Envoie la notification à Kafka pour l'admin
            kafkaTemplate2.send("notification-email_topic_admin_absences", notificationAdmin);

            System.out.println("✅ Notification envoyée à Kafka pour les absences.");
        } else {
            System.out.println("❌ Aucun employé absent aujourd'hui, notification non envoyée.");
        }
    }
*/
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, Notification> kafkaTemplate1;
    private final KafkaTemplate<String, NotificationAdmin> kafkaTemplate2;
    private final TwilioSmsService twilioService;

    public KafkaProducerService(KafkaTemplate<String, Notification> kafkaTemplate1,
                                KafkaTemplate<String, NotificationAdmin> kafkaTemplate2,
                                TwilioSmsService twilioService) {
        this.kafkaTemplate1 = kafkaTemplate1;
        this.kafkaTemplate2 = kafkaTemplate2;
        this.twilioService = twilioService;
    }

    public void sendAbsenceNotification(Notification notification) {
        if (notification == null || notification.getAnomalie() == null || notification.getAnomalie().getEmploye() == null) {
            logger.warn("❌ L'employé ou l'anomalie est manquant, notification non envoyée.");
            return;
        }

        Long employeId = notification.getAnomalie().getEmploye().getId();
        String employePhone = notification.getAnomalie().getEmploye().getTelephone();

        try {
            // Envoi de la notification à Kafka
            kafkaTemplate1.send("notification-email_topic_absent", notification);
            logger.info("✅ Notification envoyée à Kafka pour employé ID: {}", employeId);

            // Envoi de SMS si le numéro de téléphone est disponible
            if (employePhone != null && !employePhone.isEmpty()) {
                String smsMessage = "⚠️ Absence non justifiée détectée ! Veuillez contacter votre responsable.";
                //twilioService.sendSms(employePhone, smsMessage);
                logger.info("✅ SMS envoyé à {}", employePhone);
            } else {
                logger.warn("❌ Aucun numéro de téléphone enregistré pour l'employé ID: {}", employeId);
            }
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi à Kafka pour employé ID: {}", employeId, e);
        }
    }

    public void sendAdminAbsenceNotification(NotificationAdmin notificationAdmin) {
        System.out.println(notificationAdmin);
        if (notificationAdmin == null || notificationAdmin.getAbsences() == null || notificationAdmin.getAbsences().isEmpty()) {
            logger.warn("❌ Aucun employé absent aujourd'hui, notification non envoyée.");
            return;
        }

        try {
            kafkaTemplate2.send("notification-email_topic_admin_absences", notificationAdmin);
            logger.info("✅ Notification envoyée à Kafka pour les absences des employés.");
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'envoi à Kafka pour les absences des employés", e);
        }
    }



}
