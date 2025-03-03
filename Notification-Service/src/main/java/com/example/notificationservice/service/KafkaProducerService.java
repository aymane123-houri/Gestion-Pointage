package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import com.example.notificationservice.model.Anomalie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Notification> kafkaTemplate1;
    private final KafkaTemplate<String, NotificationAdmin> kafkaTemplate2;

    public KafkaProducerService(KafkaTemplate<String, Notification> kafkaTemplate1, KafkaTemplate<String, NotificationAdmin> kafkaTemplate2) {
        this.kafkaTemplate1 = kafkaTemplate1;
        this.kafkaTemplate2 = kafkaTemplate2;
    }

    // Méthode pour envoyer la notification d'absence
    public void sendAbsenceNotification(Notification notification) {
        // Vérifie si l'employé est bien renseigné
        if (notification.getAnomalie() != null && notification.getAnomalie().getEmploye() != null) {
            System.out.println("✅ Notification envoyée à Kafka pour employé ID: " + notification.getAnomalie().getEmploye().getId());
            kafkaTemplate1.send("notification-email_topic_absent", notification);
            System.out.println("✅ Notification envoyée à Kafka pour employé ID: ");
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




}
