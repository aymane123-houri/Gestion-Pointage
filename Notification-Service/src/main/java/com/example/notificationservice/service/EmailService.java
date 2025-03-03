package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.document.NotificationAdmin;
import com.example.notificationservice.model.Anomalie;
import com.example.notificationservice.model.Employe;
import com.example.notificationservice.model.Messages;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Messages messages;

    public EmailService(JavaMailSender mailSender, Messages messages) {
        this.mailSender = mailSender;
        this.messages = messages;
    }


    public void sendAbsenceNotification(Notification notification) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set recipient email
        helper.setTo(notification.getAnomalie().getEmploye().getEmail());

        // Set subject
        helper.setSubject("⚠️ Absence non justifiée – Action requise");

        // Set message body
        helper.setText(messages.getAbsenceNotification(notification.getAnomalie().getEmploye(),
                notification.getAnomalie().getPointage(),
                notification.getHoraire()));

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

    public void sendAdminAbsenceReport(NotificationAdmin notificationAdmin) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        List<String> adminEmails = List.of("admin1@example.com", "admin2@example.com");  // Liste des emails des administrateurs

        helper.setTo(adminEmails.toArray(new String[0]));  // Envoi à tous les administrateurs
        helper.setSubject(notificationAdmin.getSujet());
        helper.setText(notificationAdmin.getMessage());  // Corps du message généré dans getAdminAbsenceReport

        mailSender.send(message);
    }




}
