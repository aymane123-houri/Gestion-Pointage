package com.example.rapportservice.service;

import com.example.rapportservice.entity.Rapport;
import com.example.rapportservice.entity.RapportDetail;
import com.example.rapportservice.feignRapport.CongeFeignClient;
import com.example.rapportservice.feignRapport.EmployeFeignRapport;
import com.example.rapportservice.feignRapport.HoraireFeignClient;
import com.example.rapportservice.feignRapport.PointageFeignClient;
import com.example.rapportservice.model.Employe;
import com.example.rapportservice.model.Horaire;
import com.example.rapportservice.model.Pointage;
import com.example.rapportservice.model.Statistiques;
import com.example.rapportservice.repository.RapportDetailsRepository;
import com.example.rapportservice.repository.RapportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RapportService {

    private final RapportRepository rapportRepository;
    private EmployeFeignRapport employeFeignRapport;
    private final PointageFeignClient pointageFeignClient;
    private final HoraireFeignClient horaireFeignClient;
    private final CongeFeignClient congeFeignClient;

    private final RapportDetailsRepository rapportDetailRepository;

    public RapportService(RapportRepository rapportRepository, EmployeFeignRapport employeFeignRapport, PointageFeignClient pointageFeignClient, HoraireFeignClient horaireFeignClient, CongeFeignClient congeFeignClient, RapportDetailsRepository rapportDetailRepository) {
        this.rapportRepository = rapportRepository;
        this.employeFeignRapport = employeFeignRapport;
        this.pointageFeignClient = pointageFeignClient;
        this.horaireFeignClient = horaireFeignClient;
        this.congeFeignClient = congeFeignClient;
        this.rapportDetailRepository = rapportDetailRepository;
    }

    public Rapport createRapport(Rapport rapport){
        Employe employe = employeFeignRapport.getEmployeById(rapport.getEmployeId());
        if (employe == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'employé avec l'ID " + rapport.getEmployeId() + " n'existe pas.");
        }
        return rapportRepository.save(rapport);
    }

    public Rapport getRapportById(String id){
       Rapport rapport= rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé avec l'ID : " + id));
        rapport.setEmploye(employeFeignRapport.getEmployeById(rapport.getEmployeId()));
        return rapport;
    }

    public List<Rapport> getAllRapport(){
        List<Rapport> rapportList = rapportRepository.findAll();
        for(Rapport e: rapportList){
            e.setEmploye(employeFeignRapport.getEmployeById(e.getEmployeId()));

        }
        return rapportList;
    }

    public Rapport updateRapport(Rapport rapport,String id){
        return rapportRepository.findById(id).map(rapport1 -> {
            rapport1.setEmployeId(rapport.getEmployeId());
            rapport1.setAbsences(rapport.getAbsences());
            rapport1.setPeriode(rapport.getPeriode());
            rapport1.setRetards(rapport.getRetards());
            rapport1.setHeures_travaillees(rapport.getHeures_travaillees());
            rapport1.setHeures_supplementaires(rapport.getHeures_supplementaires());
                    return rapportRepository.save(rapport1);
                }

        ).orElseThrow((() -> new RuntimeException("rapport not found")));
    }

    public void deleteRapport(String id){
        rapportRepository.deleteById(id);
    }

    public Map<String, Integer> calculerStatistiques(int mois, int annee) {
        List<Rapport> rapports = rapportRepository.findAll();

        int totalAbsences = 0;
        int totalRetards = 0;

        for (Rapport rapport : rapports) {
            LocalDate dateRapport = LocalDate.parse(rapport.getPeriode()); // Convertir la période en date
            if (dateRapport.getMonthValue() == mois && dateRapport.getYear() == annee) {
                totalAbsences += rapport.getAbsences();
                totalRetards += rapport.getRetards();
            }
        }

        Map<String, Integer> statistiques = new HashMap<>();
        statistiques.put("totalAbsences", totalAbsences);
        statistiques.put("totalRetards", totalRetards);

        return statistiques;
    }

    public Statistiques getStatistiquesJourPrecedent() {
        LocalDate datePrecedente = LocalDate.now().minusDays(1); // Calculer la date du jour précédent
        List<RapportDetail> rapportsDetails = rapportDetailRepository.findByJour(datePrecedente); // Récupérer les détails du rapport pour cette date
        for(RapportDetail rapportDetail : rapportsDetails){
            System.out.println(rapportDetail);
        }
        if (rapportsDetails.isEmpty()) {
            System.out.println("Aucun rapport trouvé pour la date : " + datePrecedente);
        }

        // Calculer les statistiques sur les absences et les retards
        long totalAbsences = rapportsDetails.stream().filter(RapportDetail::isAbsent).count();
        long totalRetards = rapportsDetails.stream().filter(RapportDetail::isEnRetard).count();

        return new Statistiques(totalAbsences, totalRetards); // Retourner les statistiques calculées
    }



    /*public void genererRapportsQuotidiens() {
        List<Horaire> horaires = horaireFeignClient.getAllHoraires();
        LocalDate today = LocalDate.now();
        String periodeMois = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

        for (Horaire horaire : horaires) {
            Long employeId = horaire.getEmployeId();

            // 🔹 Vérification si l'employé est en congé 🔹
            boolean enConge = congeFeignClient.isEmployeEnConge(employeId, today.toString());
            if (enConge) {
                System.out.println("✅ Employé " + employeId + " est en congé APPROUVÉ aujourd'hui. Aucune absence enregistrée.");
                continue; // Ignorer l'absence de cet employé
            } else {
                System.out.println("🚨 Employé " + employeId + " n'est PAS en congé approuvé. Vérification des absences...");
            }


            // Récupérer les pointages du jour
            List<Pointage> pointages = pointageFeignClient.getPointagesParEmployeEtDate(employeId, today.toString());

            // Initialisation des variables
            int heuresTravailles = 0;
            int heuresSupplementaires = 0;
            int retards = 0;
            int absences = 0;

            if (pointages == null || pointages.isEmpty()) {
                if (!isWeekend(today)) { // 🔥 Vérifie si ce n'est pas un week-end
                    absences = 1; // Marquer l'absence seulement en semaine
                } else {
                    System.out.println("🛑 Absence ignorée pour " + employeId + " (week-end)");
                }
            }else {
                for (Pointage pointage : pointages) {
                    if (pointage.getDateHeureEntree() != null && pointage.getDateHeureSortie() != null) {
                        LocalTime heureEntree = pointage.getDateHeureEntree().toLocalTime();
                        LocalTime heureSortie = pointage.getDateHeureSortie().toLocalTime();

                        // Vérifier le retard
                        if (heureEntree.isAfter(horaire.getHeure_arrivee())) {
                            retards++;
                        }

                        // Calcul des heures travaillées
                        int heuresTravailleesJour = (int) Duration.between(heureEntree, heureSortie).toHours();
                        heuresTravailles += heuresTravailleesJour;

                        // Calcul des heures supplémentaires
                        if (heureSortie.isAfter(horaire.getHeure_depart())) {
                            int heuresSupp = (int) Duration.between(horaire.getHeure_depart(), heureSortie).toHours();
                            heuresSupplementaires += heuresSupp;
                        }
                    }
                }
            }

            // Créer le détail du jour
            RapportDetail rapportDetail = new RapportDetail(today, heuresTravailles, heuresSupplementaires, retards > 0, absences > 0);

            // Mettre à jour le rapport mensuel
            enregistrerRapportMensuel(employeId, periodeMois, rapportDetail);
        }
    }*/

    public void genererRapportsQuotidiens() {
        List<Horaire> horaires = horaireFeignClient.getAllHoraires();
        LocalDate today = LocalDate.now();
        String periodeMois = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

        for (Horaire horaire : horaires) {
            Long employeId = horaire.getEmployeId();
            String statutJour = "Travail"; // Par défaut, jour ouvré normal

            // 🔹 Vérification si l'employé est en congé 🔹
            boolean enConge = congeFeignClient.isEmployeEnConge(employeId, today.toString());
            if (enConge) {
                System.out.println("✅ Employé " + employeId + " est en congé APPROUVÉ aujourd'hui. Aucune absence enregistrée.");
                statutJour = "Congé";
            } else if (isWeekend(today)) {
                System.out.println("🛑 Employé " + employeId + " est en week-end. Absence non comptabilisée.");
                statutJour = "Week-end";
            } else {
                System.out.println("🚨 Employé " + employeId + " n'est PAS en congé approuvé. Vérification des absences...");
            }

            // Récupérer les pointages du jour
            List<Pointage> pointages = pointageFeignClient.getPointagesParEmployeEtDate(employeId, today.toString());

            // Initialisation des variables
            int heuresTravailles = 0;
            int heuresSupplementaires = 0;
            int retards = 0;
            int absences = 0;

            if (pointages == null || pointages.isEmpty()) {
                if (!enConge && !isWeekend(today)) { // 🔥 Vérifie si ce n'est pas un week-end ou congé
                    absences = 1; // Marquer l'absence seulement en semaine
                } else {
                    System.out.println("🛑 Absence ignorée pour " + employeId + " (jour non ouvré)");
                }
            } else {
                for (Pointage pointage : pointages) {
                    if (pointage.getDateHeureEntree() != null && pointage.getDateHeureSortie() != null) {
                        LocalTime heureEntree = pointage.getDateHeureEntree().toLocalTime();
                        LocalTime heureSortie = pointage.getDateHeureSortie().toLocalTime();

                        // Vérifier le retard
                        if (heureEntree.isAfter(horaire.getHeure_arrivee())) {
                            retards++;
                        }

                        // Calcul des heures travaillées
                        int heuresTravailleesJour = (int) Duration.between(heureEntree, heureSortie).toHours();
                        heuresTravailles += heuresTravailleesJour;

                        // Calcul des heures supplémentaires
                        if (heureSortie.isAfter(horaire.getHeure_depart())) {
                            int heuresSupp = (int) Duration.between(horaire.getHeure_depart(), heureSortie).toHours();
                            heuresSupplementaires += heuresSupp;
                        }
                    }
                }
            }

            // Créer le détail du jour avec statut du jour
            RapportDetail rapportDetail = new RapportDetail(today, heuresTravailles, heuresSupplementaires, retards > 0, absences > 0, statutJour);

            // Mettre à jour le rapport mensuel
            enregistrerRapportMensuel(employeId, periodeMois, rapportDetail);
        }
    }


    private void enregistrerRapportMensuel(Long employeId, String periodeMois, RapportDetail rapportDetail) {
        // 🔍 Ajout d'un log pour voir si le rapport est bien trouvé
        Rapport rapport = rapportRepository.findByEmployeIdAndPeriode(employeId, periodeMois);

        if (rapport == null) {
            System.out.println("⚠️ Aucun rapport trouvé pour employe_id=" + employeId + " et période=" + periodeMois + ". Création d'un nouveau rapport.");
            rapport = new Rapport();
            rapport.setEmployeId(employeId);
            rapport.setPeriode(periodeMois);
            rapport.setDetails(new ArrayList<>());
        } else {
            System.out.println("✅ Rapport trouvé pour employe_id=" + employeId + " et période=" + periodeMois + ". Mise à jour en cours.");
        }

        // Mise à jour des heures et des détails
        rapport.getDetails().add(rapportDetail);
        rapport.setHeures_travaillees(rapport.getHeures_travaillees() + rapportDetail.getHeuresTravaillees());
        rapport.setHeures_supplementaires(rapport.getHeures_supplementaires() + rapportDetail.getHeuresSupplementaires());
        rapport.setRetards(rapport.getRetards() + (rapportDetail.isEnRetard() ? 1 : 0));
        rapport.setAbsences(rapport.getAbsences() + (rapportDetail.isAbsent() ? 1 : 0));

        // 🔍 Log avant la sauvegarde
        System.out.println("📌 Sauvegarde du rapport: " + rapport);

        // Sauvegarde du rapport
        rapportRepository.save(rapport);
    }


    public Rapport getRapportByEmployeAndPeriode(Long idEmploye, int year, int month) {
        String periode = year + "-" + String.format("%02d", month); // Format YYYY-MM
        Rapport rapport = rapportRepository.findByEmployeIdAndPeriode(idEmploye, periode);

        if (rapport != null) {
            // 🔥 Récupérer les informations de l'employé via FeignClient
            Employe employe = employeFeignRapport.getEmployeById(idEmploye);
            rapport.setEmploye(employe);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun rapport trouvé pour cet employé et cette période.");
        }

        return rapport;
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }



    public Map<String, Map<String, Integer>> getStatistiquesMensuelles(int mois, int annee) {
        // Récupérer tous les rapports pour le mois et l'année donnés
        String periode = annee + "-" + String.format("%02d", mois); // Format YYYY-MM
        List<Rapport> rapports = rapportRepository.findByPeriode(periode);

        // Initialiser les totaux pour chaque mois
        Map<String, Map<String, Integer>> statistiquesParMois = new HashMap<>();

        // Calculer les totaux pour le mois demandé
        Map<String, Integer> statistiquesMois = new HashMap<>();
        int totalAbsences = 0;
        int totalRetards = 0;
        int totalHeuresSupplementaires = 0;

        for (Rapport rapport : rapports) {
            totalAbsences += rapport.getAbsences();
            totalRetards += rapport.getRetards();
            totalHeuresSupplementaires += rapport.getHeures_supplementaires();
        }

        // Ajouter les totaux pour le mois demandé
        statistiquesMois.put("totalAbsences", totalAbsences);
        statistiquesMois.put("totalRetards", totalRetards);
        statistiquesMois.put("totalHeuresSupplementaires", totalHeuresSupplementaires);

        // Ajouter les statistiques pour le mois demandé dans la map globale
        String nomMois = getNomMois(mois); // Convertir le numéro du mois en nom (ex: 1 -> "janvier")
        statistiquesParMois.put(nomMois, statistiquesMois);

        return statistiquesParMois;
    }

    // Méthode pour convertir un numéro de mois en nom de mois
    private String getNomMois(int mois) {
        String[] nomsMois = {
                "janvier", "février", "mars", "avril", "mai", "juin",
                "juillet", "août", "septembre", "octobre", "novembre", "décembre"
        };
        return nomsMois[mois - 1]; // Les mois sont indexés à partir de 1
    }


    public Map<String, Map<String, Integer>> getStatistiquesAnnuelles(int annee) {
        // Récupérer tous les rapports pour l'année donnée
        List<Rapport> rapports = rapportRepository.findByPeriodeStartingWith(String.valueOf(annee));

        // Initialiser les totaux pour chaque mois
        Map<String, Map<String, Integer>> statistiquesParMois = new HashMap<>();

        // Boucler sur chaque mois de l'année
        for (int mois = 1; mois <= 12; mois++) {
            String periode = annee + "-" + String.format("%02d", mois); // Format YYYY-MM

            // Calculer les totaux pour le mois en cours
            int totalAbsences = 0;
            int totalRetards = 0;
            int totalHeuresSupplementaires = 0;

            for (Rapport rapport : rapports) {
                if (rapport.getPeriode().equals(periode)) {
                    totalAbsences += rapport.getAbsences();
                    totalRetards += rapport.getRetards();
                    totalHeuresSupplementaires += rapport.getHeures_supplementaires();
                }
            }

            // Ajouter les totaux pour le mois en cours
            Map<String, Integer> statistiquesMois = new HashMap<>();
            statistiquesMois.put("totalAbsences", totalAbsences);
            statistiquesMois.put("totalRetards", totalRetards);
            statistiquesMois.put("totalHeuresSupplementaires", totalHeuresSupplementaires);

            // Ajouter les statistiques pour le mois en cours dans la map globale
            String nomMois = getNomMois(mois); // Convertir le numéro du mois en nom (ex: 1 -> "janvier")
            statistiquesParMois.put(nomMois, statistiquesMois);
        }

        return statistiquesParMois;
    }


    public Map<String, Integer> getStatistiquesParEmploye(Long employeId, int mois, int annee) {
        // Récupérer le rapport pour l'employé et la période donnés
        String periode = annee + "-" + String.format("%02d", mois); // Format YYYY-MM
        Rapport rapport = rapportRepository.findByEmployeIdAndPeriode(employeId, periode);

        // Vérifier si le rapport existe
        if (rapport == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun rapport trouvé pour cet employé et cette période.");
        }

        // Retourner les statistiques sous forme de Map
        Map<String, Integer> statistiques = new HashMap<>();
        statistiques.put("totalAbsences", rapport.getAbsences());
        statistiques.put("totalRetards", rapport.getRetards());
        statistiques.put("totalHeuresSupplementaires", (int) rapport.getHeures_supplementaires());

        return statistiques;
    }

    public double getTauxAbsenteisme(int mois, int annee) {
        String periode = annee + "-" + String.format("%02d", mois);
        List<Rapport> rapports = rapportRepository.findByPeriode(periode);

        int totalAbsences = 0;
        int totalJoursOuvrables = LocalDate.of(annee, mois, 1).lengthOfMonth() * rapports.size(); // Approximation

        for (Rapport rapport : rapports) {
            totalAbsences += rapport.getAbsences();
        }
        if (totalJoursOuvrables == 0) {
            return 0.0; // ou -1 pour signaler une absence de données
        }
        return (double) totalAbsences / totalJoursOuvrables * 100; // Taux en pourcentage
    }

    public double getTauxRetards(int mois, int annee) {
        String periode = annee + "-" + String.format("%02d", mois);
        List<Rapport> rapports = rapportRepository.findByPeriode(periode);

        int totalRetards = 0;
        int totalJoursTravailles = LocalDate.of(annee, mois, 1).lengthOfMonth() * rapports.size(); // Approximation

        for (Rapport rapport : rapports) {
            totalRetards += rapport.getRetards();
        }
        if (totalJoursTravailles == 0) {
            return 0.0; // ou -1 pour signaler une absence de données
        }
        return (double) totalRetards / totalJoursTravailles * 100; // Taux en pourcentage
    }

    public Map<String, Object> getStatistiquesDetailleesParEmploye(Long employeId, int mois, int annee) {
        String periode = annee + "-" + String.format("%02d", mois);
        Rapport rapport = rapportRepository.findByEmployeIdAndPeriode(employeId, periode);

        if (rapport == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun rapport trouvé pour cet employé et cette période.");
        }

        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("absences", rapport.getAbsences());
        statistiques.put("retards", rapport.getRetards());
        statistiques.put("heures_travaillees", rapport.getHeures_travaillees());
        statistiques.put("heures_supplementaires", rapport.getHeures_supplementaires());

        return statistiques;
    }

    public Map<String, Map<String, Integer>> getStatistiquesParJourSemaine(int mois, int annee) {
        String periode = annee + "-" + String.format("%02d", mois);
        List<Rapport> rapports = rapportRepository.findByPeriode(periode);

        // Map pour stocker les statistiques par jour de la semaine
        Map<String, Map<String, Integer>> statistiquesParJour = new HashMap<>();

        for (Rapport rapport : rapports) {
            for (RapportDetail detail : rapport.getDetails()) {
                String jourSemaine = detail.getJour().getDayOfWeek().toString(); // Ex: "MONDAY"
                statistiquesParJour.putIfAbsent(jourSemaine, new HashMap<>());
                Map<String, Integer> stats = statistiquesParJour.get(jourSemaine);

                stats.put("absences", stats.getOrDefault("absences", 0) + (detail.isAbsent() ? 1 : 0));
                stats.put("retards", stats.getOrDefault("retards", 0) + (detail.isEnRetard() ? 1 : 0));
            }
        }

        return statistiquesParJour;
    }

    public double getHeuresTravailleesMoyennes(int mois, int annee) {
        String periode = annee + "-" + String.format("%02d", mois);
        List<Rapport> rapports = rapportRepository.findByPeriode(periode);

        int totalHeuresTravaillees = 0;
        int nombreEmployes = rapports.size();

        for (Rapport rapport : rapports) {
            totalHeuresTravaillees += rapport.getHeures_travaillees();
        }
        if (nombreEmployes == 0) {
            return 0.0; // ou -1.0 si tu veux signaler qu’il n’y a pas de données
        }
        return (double) totalHeuresTravaillees / nombreEmployes;
    }

    public Map<String, Map<String, Map<String, Integer>>> getStatistiquesParDepartement(int mois, int annee) {
        String periode = annee + "-" + String.format("%02d", mois);
        List<Rapport> rapports = rapportRepository.findByPeriode(periode);

        // Map pour stocker les statistiques par département et par mois
        Map<String, Map<String, Map<String, Integer>>> statistiquesParDepartement = new HashMap<>();

        for (Rapport rapport : rapports) {
            Employe employe = employeFeignRapport.getEmployeById(rapport.getEmployeId());
            String departement = employe.getDepartement();

            // Initialiser les statistiques pour le département s'il n'existe pas
            statistiquesParDepartement.putIfAbsent(departement, new HashMap<>());
            Map<String, Map<String, Integer>> statsParMois = statistiquesParDepartement.get(departement);

            // Initialiser les statistiques pour le mois s'il n'existe pas
            String nomMois = getNomMois(mois);
            statsParMois.putIfAbsent(nomMois, new HashMap<>());
            Map<String, Integer> stats = statsParMois.get(nomMois);

            // Mettre à jour les totaux
            stats.put("totalAbsences", stats.getOrDefault("totalAbsences", 0) + rapport.getAbsences());
            stats.put("totalRetards", stats.getOrDefault("totalRetards", 0) + rapport.getRetards());
            stats.put("totalHeuresSupplementaires", (int) (stats.getOrDefault("totalHeuresSupplementaires", 0) + rapport.getHeures_supplementaires()));
        }

        return statistiquesParDepartement;
    }
}
