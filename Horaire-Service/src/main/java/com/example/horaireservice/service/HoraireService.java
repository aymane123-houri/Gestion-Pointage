package com.example.horaireservice.service;

import com.example.horaireservice.entity.Horaire;
import com.example.horaireservice.feignHoraire.EmployeFeignHoraire;
import com.example.horaireservice.model.Employe;
import com.example.horaireservice.repository.HoraireRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HoraireService {

    private final HoraireRepository horaireRepository;
    private EmployeFeignHoraire employeFeignPointage;

    public HoraireService(HoraireRepository horaireRepository, EmployeFeignHoraire employeFeignPointage) {
        this.horaireRepository = horaireRepository;
        this.employeFeignPointage = employeFeignPointage;
    }

    public Horaire createHoraire(Horaire horaire){
        return horaireRepository.save(horaire);
    }

    public Horaire getHoraireById(Long id){
        Horaire horaire=horaireRepository.findById(id).orElse(null);
        horaire.setEmploye(employeFeignPointage.getEmployeById(horaire.getEmployeId()));
        return horaireRepository.findById(id).orElse(null);

    }

    public List<Horaire> getAllHoraire(){
        List<Horaire> horaireList = horaireRepository.findAll();
        for (Horaire h: horaireList){
            h.setEmploye(employeFeignPointage.getEmployeById(h.getEmployeId()));
        }
        return horaireList;
    }

    public Horaire updateHoraire(Horaire horaire,Long id){
        return horaireRepository.findById(id).map(horaire1 -> {
            horaire1.setHeure_depart(horaire.getHeure_depart());
            horaire1.setHeure_arrivee(horaire.getHeure_arrivee());
            horaire1.setType(horaire.getType());
            horaire1.setEmployeId(horaire.getEmployeId());
                    return horaireRepository.save(horaire1);
                }

        ).orElseThrow((() -> new RuntimeException("horaire not found")));
    }

    public void deleteHoraire(Long id){
        horaireRepository.deleteById(id);
    }

    public List<Employe> rechercherEmployeParNom(String nom) {
        // Appel à l'API du microservice Employe via FeignClient
        return employeFeignPointage.rechercherEmployeParNom(nom);
    }


    // Méthode pour récupérer les horaires d'un employé pour une date spécifique
   /* public List<Horaire> getHorairesParEmployeEtDate(Long employeId, LocalDate date) {
        // Appeler le repository pour obtenir les horaires
        return horaireRepository.findByEmployeIdAndDateHeureEntree(employeId, date);
    }*/

    // Récupère tous les horaires pour un employé donné
    public List<Horaire> getHorairesParEmploye(Long employeId) {
        return horaireRepository.findByEmployeId(employeId);
    }

}
