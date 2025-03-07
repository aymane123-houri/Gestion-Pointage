package com.example.notificationservice.model;

import com.example.notificationservice.feignClient.HoraireFeignClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
public class Messages {
    private HoraireFeignClient horaireFeignClient;

    public Messages() {
    }

    public Messages(HoraireFeignClient horaireFeignClient) {
        this.horaireFeignClient = horaireFeignClient;
    }

    public String getAbsenceNotification(Employe employe, Pointage pointage, Horaire horaire) {
        // Formatteur d'heure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Récupération des heures de début et de fin de l'horaire de l'employé
        String heureArriveeStr = (horaire != null && horaire.getHeure_arrivee() != null)
                ? horaire.getHeure_arrivee().format(formatter)
                : "Non défini";
        String heureDepartStr = (horaire != null && horaire.getHeure_depart() != null)
                ? horaire.getHeure_depart().format(formatter)
                : "Non défini";

        return "<html><body>"
                + "<h2 style='color: #D32F2F;'>⚠️ Notification d'Absence Non Justifiée ⚠️</h2>"
                + "<p>Bonjour <strong>" + employe.getNom() + " " + employe.getPrenom() + "</strong>,</p>"
                + "<p>Nous avons constaté une absence non justifiée à votre poste dans le département <strong>" + employe.getDepartement() + "</strong>.</p>"

                + "<h3>📅 Détails de l'absence :</h3>"
                + "<ul style='background-color: #f8f9fa; padding: 10px; border-radius: 8px;'>"
                + "    <li><strong>🏢 Poste :</strong> " + employe.getPoste() + "</li>"
                + "    <li><strong>⏰ Horaire prévu :</strong> " + heureArriveeStr + " - " + heureDepartStr + "</li>"
                + "    <li><strong>❌ Statut :</strong> Absence non signalée</li>"
                + "</ul>"

                + "<p style='color: red;'><strong>🔍 Rappel :</strong> Il est important d'informer votre supérieur en cas d'empêchement afin d'éviter toute sanction disciplinaire.</p>"
                + "<p><strong>📩 Merci de régulariser votre situation en fournissant une justification au plus tard dans les 24 heures.</strong></p>"
                + "<p>Si vous avez des questions, veuillez contacter le service des ressources humaines.</p>"

                + "<br><p>Cordialement,<br><strong>L'équipe RH 🏢</strong></p>"
                + "</body></html>";
    }


    // Méthode pour générer le rapport d'absence pour l'administrateur
    public String getAdminAbsenceReport(List<AbsenceInfo> absences) {
        StringBuilder message = new StringBuilder();

        message.append("<html><body>");
        message.append("<h2 style='color: #2E86C1;'>📢 Rapport des absences du jour 📢</h2>");
        message.append("<p>Bonjour,</p>");
        message.append("<p>Veuillez trouver ci-dessous la liste des employés absents aujourd'hui :</p>");

        if (absences.isEmpty()) {
            message.append("<p style='color: green;'>✅ Aucun employé absent aujourd'hui.</p>");
        } else {
            message.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>");
            message.append("<tr style='background-color: #f2f2f2;'>");
            message.append("<th style='padding: 8px;'>Matricule</th>");
            message.append("<th style='padding: 8px;'>Nom et Prénom</th>");
            message.append("<th style='padding: 8px;'>Département</th>");
            message.append("</tr>");

            for (AbsenceInfo absence : absences) {
                message.append("<tr>");
                message.append("<td style='padding: 8px;'>" + absence.getMatricule() + "</td>");
                message.append("<td style='padding: 8px;'>" + absence.getNom() + " " + absence.getPrenom() + "</td>");
                message.append("<td style='padding: 8px;'>" + absence.getDepartement() + "</td>");
                message.append("</tr>");
            }

            message.append("</table>");
            message.append("<p style='color: red;'>⚠️ Veuillez vérifier et prendre les mesures nécessaires.</p>");
        }

        message.append("<p><strong>📅 Date :</strong> " + LocalDate.now() + "</p>");
        message.append("<p><strong>📩 Merci de votre vigilance.</strong></p>");
        message.append("<p>Cordialement,<br>L'équipe RH 🏢</p>");
        message.append("</body></html>");

        return message.toString();
    }


    public String getAbsenceSmsNotification(Employe employe, Pointage pointage, Horaire horaire) {
        // Récupération des heures de début et de fin de l'horaire de l'employé
        String heureArriveeStr = (horaire != null && horaire.getHeure_arrivee() != null)
                ? horaire.getHeure_arrivee().toString()
                : "Non défini";
        String heureDepartStr = (horaire != null && horaire.getHeure_depart() != null)
                ? horaire.getHeure_depart().toString()
                : "Non défini";

        // Création du message SMS
        return "⚠️ Absence non justifiée détectée !\n\n"
                + "Bonjour " + employe.getNom() + " " + employe.getPrenom() + ",\n\n"
                + "Poste : " + employe.getPoste() + "\n"
                + "Département : " + employe.getDepartement() + "\n"
                + "Horaire prévu : " + heureArriveeStr + " - " + heureDepartStr + "\n"
                + "Statut : Absence non signalée\n\n"
                + "📩 Merci de régulariser votre situation et contacter votre responsable.";
    }



}
