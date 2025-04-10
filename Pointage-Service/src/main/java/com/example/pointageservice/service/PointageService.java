package com.example.pointageservice.service;

import com.example.pointageservice.entity.Pointage;
import com.example.pointageservice.feignPointage.EmployeFeignPointage;
import com.example.pointageservice.model.Employe;
import com.example.pointageservice.repository.PointageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class PointageService {
    private final PointageRepository pointageRepository;

    private EmployeFeignPointage employeFeignPointage;

    public PointageService(PointageRepository pointageRepository, EmployeFeignPointage employeFeignPointage) {
        this.pointageRepository = pointageRepository;
        this.employeFeignPointage = employeFeignPointage;
    }

    /*public Pointage createPointage(Pointage pointage){
        Employe employe = employeFeignPointage.getEmployeById(pointage.getEmployeId());
        if (employe == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'employé avec l'ID " + pointage.getEmployeId() + " n'existe pas.");
        }
        return pointageRepository.save(pointage);
    }*/

    /*public Pointage createPointage(Long employeId) {
        // Vérifier si l'employé a déjà pointé aujourd'hui
        LocalDateTime todayStart = LocalDate.now().atStartOfDay(); // Début de la journée
        LocalDateTime now = LocalDateTime.now(); // Heure actuelle

        // Chercher un pointage d'entrée existant pour aujourd'hui
        List<Pointage> pointagesExistants = pointageRepository.findByEmployeIdAndDateHeureEntreeAfter(employeId, todayStart);

        if (!pointagesExistants.isEmpty()) {
            Pointage pointageExist = pointagesExistants.get(0);
            // Si l'employé a déjà pointé à l'entrée et n'a pas pointé de sortie, mettre à jour l'heure de sortie
            if (pointageExist.getDateHeureSortie() == null) {
                pointageExist.setDateHeureSortie(now);
                pointageExist.setStatut("Présent");
                return pointageRepository.save(pointageExist);
            }
        }

        // Sinon, c'est un nouveau pointage d'entrée
        Pointage nouveauPointage = new Pointage();
        nouveauPointage.setEmployeId(employeId);
        nouveauPointage.setDateHeureEntree(now);
        nouveauPointage.setStatut("Présent");
        return pointageRepository.save(nouveauPointage);
    }*/

    public Pointage createPointage(Long employeId) {
        LocalDate today = LocalDate.now();
        List<Pointage> todayPointages = getPointagesAujourdhui(employeId, today);

        // Vérifier s'il y a un pointage sans sortie
        Optional<Pointage> incompletePointage = todayPointages.stream()
                .filter(p -> p.getDateHeureSortie() == null)
                .findFirst();

        if (incompletePointage.isPresent()) {
            // Enregistrer la sortie
            Pointage pointage = incompletePointage.get();
            pointage.setDateHeureSortie(LocalDateTime.now());
            pointage.setStatut("Présent");
            return pointageRepository.save(pointage);
        }

        // Sinon créer un nouveau pointage d'entrée
        Pointage nouveauPointage = new Pointage();
        nouveauPointage.setEmployeId(employeId);
        nouveauPointage.setDateHeureEntree(LocalDateTime.now());
        nouveauPointage.setStatut("Présent");
        return pointageRepository.save(nouveauPointage);
    }



    public Pointage getPointageById(String id){
        Pointage pointage=pointageRepository.findById(id).orElse(null);
        pointage.setEmploye(employeFeignPointage.getEmployeById(pointage.getEmployeId()));
        return pointage;
    }

    public List<Pointage> GetAllPointage(){
        List<Pointage> pointageList = pointageRepository.findAll();
        for(Pointage e: pointageList){
            e.setEmploye(employeFeignPointage.getEmployeById(e.getEmployeId()));

        }
        return pointageList;
    }

    public Pointage updatePointage(Pointage pointage,String id){

            return pointageRepository.findById(id).map(pointage1 -> {
                if (pointage.getEmployeId() != null && !pointage.getEmployeId().equals(pointage1.getEmployeId())) {
                    // Vérifier si le nouvel employeId existe
                    Employe employe = employeFeignPointage.getEmployeById(pointage.getEmployeId());
                    if (employe == null) {
                        throw new RuntimeException("L'employé avec l'ID " + pointage.getEmployeId() + " n'existe pas.");
                    }
                }
                pointage1.setEmployeId(pointage.getEmployeId());
                pointage1.setDateHeureSortie(pointage.getDateHeureSortie());
                pointage1.setDateHeureEntree(pointage.getDateHeureEntree());
                pointage1.setStatut(pointage.getStatut());
                        return pointageRepository.save(pointage1);
                    }

            ).orElseThrow((() -> new RuntimeException("pointage not found")));
    }

    public void deletePointage(String id){
        pointageRepository.deleteById(id);
    }




    public Pointage enregistrerPointageEntree(Long employeId) {
        Pointage dernierPointage = getDernierPointage(employeId);

        // Vérifier si l'employé n'a pas déjà pointé son entrée aujourd'hui
        if (dernierPointage != null && dernierPointage.getDateHeureEntree().toLocalDate().isEqual(LocalDateTime.now().toLocalDate())
                && dernierPointage.getDateHeureSortie() == null) {
            throw new IllegalStateException("L'employé a déjà pointé son entrée aujourd'hui.");
        }

        Pointage pointage = new Pointage();
        pointage.setEmployeId(employeId);
        pointage.setDateHeureEntree(LocalDateTime.now());
        pointage.setStatut("Normal");

        return pointageRepository.save(pointage);
    }

    public Pointage enregistrerPointageSortie(Long employeId) {
        Pointage dernierPointage = getDernierPointage(employeId);

        if (dernierPointage == null || dernierPointage.getDateHeureSortie() != null) {
            throw new IllegalStateException("Aucune entrée en cours trouvée pour cet employé.");
        }

        dernierPointage.setDateHeureSortie(LocalDateTime.now());
        return pointageRepository.save(dernierPointage);
    }

    public Pointage getDernierPointage(Long employeId) {
        Optional<Pointage> pointage = pointageRepository.findTopByEmployeIdOrderByDateHeureEntreeDesc(employeId);
        return pointage.orElse(null);
    }


    public List<Pointage> getHistoriquePointages(Long employeId) {
        return pointageRepository.findByEmployeId(employeId);
    }



    public List<Pointage> getPointagesParEmployeEtDate(Long employeId, LocalDate date) {
        // Convertir la LocalDate en LocalDateTime, en commençant et finissant à minuit pour la journée entière
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);

        return pointageRepository.findByEmployeIdAndDateHeureEntreeBetween(employeId, startDate, endDate);
    }


    public List<Pointage> getAllPointages(String debut,String fin) {
        LocalDateTime dateDebut = LocalDate.parse(debut).atStartOfDay();
        LocalDateTime dateFin = LocalDate.parse(fin).atTime(23, 59, 59);

        return pointageRepository.findByDateHeureEntreeBetween(dateDebut, dateFin);
    }


    public List<Pointage> getPointagesAujourdhui(Long employeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Pointage> pointages = pointageRepository.findByEmployeIdAndDateHeureEntreeBetween(
                employeId, startOfDay, endOfDay);

        // Optionnel: Hydrater les données employé
        pointages.forEach(p -> p.setEmploye(employeFeignPointage.getEmployeById(p.getEmployeId())));

        return pointages;
    }
}
