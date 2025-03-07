package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import com.example.notificationservice.feignClient.AdministratorFeignClient;
import com.example.notificationservice.model.Administrateur;
import com.example.notificationservice.model.Anomalie;
import com.example.notificationservice.model.Employe;
import com.example.notificationservice.model.Messages;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Messages messages;
    private final AdministratorFeignClient administratorFeignClient;


    public EmailService(JavaMailSender mailSender, Messages messages, AdministratorFeignClient administratorFeignClient) {
        this.mailSender = mailSender;
        this.messages = messages;
        this.administratorFeignClient = administratorFeignClient;
    }

    public void sendAbsenceNotification(Notification notification) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        if (notification == null || notification.getAnomalie() == null ||
                notification.getAnomalie().getEmploye() == null ||
                notification.getAnomalie().getEmploye().getEmail() == null) {
            System.out.println("❌ Impossible d'envoyer l'email : informations manquantes.");
            return;
        }
        // Set recipient email
        helper.setTo(notification.getAnomalie().getEmploye().getEmail());

        // Set subject
        helper.setSubject("⚠️ Absence non justifiée – Action requise");

        // Set message body
        helper.setText(messages.getAbsenceNotification(notification.getAnomalie().getEmploye(),
                notification.getAnomalie().getPointage(),
                notification.getHoraire()), true);

        // Send email
        mailSender.send(message);
    }

    /*public void sendAdminAbsenceReport(List<Anomalie> absences) throws MessagingException {
        if (absences.isEmpty()) {
            System.out.println("✅ Aucun employé absent aujourd'hui. Pas d'email envoyé.");
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Liste des administrateurs à notifier
        List<String> adminEmails = List.of("admin1@entreprise.com", "admin2@entreprise.com");
        helper.setTo(adminEmails.toArray(new String[0]));

        // Sujet de l'email
        helper.setSubject("📢 Rapport des absences du jour");

        // Génération du contenu de l'email
        String emailContent = messages.getAdminAbsenceReport(absences);
        helper.setText(emailContent, true); // true pour activer le HTML si nécessaire

        // Envoi de l'email
        mailSender.send(message);
        System.out.println("📧 Email de notification envoyé aux administrateurs.");
    }
*/
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    public void sendAdminAbsenceReport(NotificationAdmin notificationAdmin) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            // Récupération des administrateurs via FeignClient
            List<Administrateur> admins = administratorFeignClient.getAllAdministrateurs();

            // Vérifier si la liste d'administrateurs est vide ou nulle
            if (admins == null || admins.isEmpty()) {
                logger.warn("❌ Aucun administrateur trouvé, email non envoyé.");
                return;
            }

            // Extraction des emails des administrateurs
            List<String> adminEmails = admins.stream()
                    .map(Administrateur::getEmail) // Supposons que getEmail() existe
                    .filter(Objects::nonNull) // Filtre les emails nuls
                    .collect(Collectors.toList()); // Liste des emails des administrateurs

            // Vérification qu'il y a des emails valides
            if (adminEmails.isEmpty()) {
                logger.warn("❌ Aucun email valide trouvé parmi les administrateurs.");
                return;
            }

            // Définition du destinataire, du sujet et du corps du message
            helper.setTo(adminEmails.toArray(new String[0])); // Envoi à tous les administrateurs
            helper.setSubject(notificationAdmin.getSujet());
            helper.setText(messages.getAdminAbsenceReport(notificationAdmin.getAbsences()), true); // Corps du message généré dans getAdminAbsenceReport

            // Envoi de l'email
            mailSender.send(message);
            logger.info("✅ Rapport d'absences envoyé avec succès aux administrateurs.");

        } catch (MessagingException e) {
            // Gestion des exceptions spécifiques à l'envoi d'email
            logger.error("❌ Erreur lors de l'envoi du rapport d'absences : " + e.getMessage(), e);
            throw e; // Rethrow pour que l'appelant puisse gérer l'erreur
        } catch (Exception e) {
            // Gestion des autres exceptions
            logger.error("❌ Erreur imprévue lors de l'envoi du rapport d'absences : " + e.getMessage(), e);
        }
    }





}
