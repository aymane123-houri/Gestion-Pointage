package com.example.notificationservice.model;

import com.example.notificationservice.feignClient.HoraireFeignClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
public class Messages {
    private HoraireFeignClient horaireFeignClient;

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

        // Construction du message de notification
        return "✨ Bonjour " + employe.getNom() + " " + employe.getPrenom() + ",\n\n"
                + "⚠️ Nous avons constaté une absence non justifiée à votre poste le **" + employe.getDepartement() + "**.\n\n"
                + "📅 **Détails de l'absence** :\n"
                + "- 🏢 **Poste** : " + employe.getDepartement() + "\n"
                + "- ⏰ **Heure prévue d'arrivée** : " + heureArriveeStr + " - " + heureDepartStr + "\n"
                + "- ❌ **Statut** : Absence non signalée\n\n"
                + "🔍 Nous vous rappelons qu'il est important d'informer votre supérieur en cas d'empêchement pour éviter toute sanction disciplinaire.\n\n"
                + "📩 Merci de régulariser votre situation en fournissant une justification au plus tard dans les 24 heures.\n\n"
                + "Si vous avez des questions, veuillez contacter le service des ressources humaines.\n\n"
                + "Cordialement,\n"
                + "L'équipe RH 🏢";
    }

    // Méthode pour générer le rapport d'absence pour l'administrateur
    public String getAdminAbsenceReport(List<Anomalie> absences) {
        StringBuilder message = new StringBuilder();

        message.append("📢 **Rapport des absences du jour** 📢\n\n");
        message.append("Bonjour,\n\n");
        message.append("Veuillez trouver ci-dessous la liste des employés absents aujourd'hui :\n\n");

        if (absences.isEmpty()) {
            message.append("✅ Aucun employé absent aujourd'hui.\n\n");
        } else {
            message.append("| Matricule | Nom et Prénom       | Département      |\n");
            message.append("|-----------|--------------------|----------------|\n");

            for (Anomalie absence : absences) {
                Employe employe = absence.getEmploye();
                if (employe != null) {
                    message.append("| ").append(employe.getMatricule()).append(" | ")
                            .append(employe.getNom()).append(" ").append(employe.getPrenom()).append(" | ")
                            .append(employe.getDepartement()).append(" |\n");
                }
            }
            message.append("\n⚠️ Veuillez vérifier et prendre les mesures nécessaires.\n\n");
        }

        message.append("📅 **Date** : ").append(LocalDate.now()).append("\n\n");
        message.append("📩 **Merci de votre vigilance.**\n\n");
        message.append("Cordialement,\n");
        message.append("L'équipe RH 🏢");

        return message.toString();
    }



}
