package com.example.congeservice.Service;

import com.example.congeservice.Entity.Conge;
import com.example.congeservice.Entity.StatusConge;
import com.example.congeservice.FeignClient.AdministrateurFeignClient;
import com.example.congeservice.FeignClient.EmployeFeignClient;
import com.example.congeservice.Repository.CongeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CongeService {
    private final CongeRepository congeRepository;
    private EmployeFeignClient employeFeignClient;
    private AdministrateurFeignClient administrateurFeignClient;

    public CongeService(CongeRepository congeRepository, EmployeFeignClient employeFeignClient, AdministrateurFeignClient administrateurFeignClient) {
        this.congeRepository = congeRepository;
        this.employeFeignClient = employeFeignClient;
        this.administrateurFeignClient = administrateurFeignClient;
    }

    public Conge createConge(Conge conge){
        System.out.println(conge);
        return congeRepository.save(conge);
    }

    public List<Conge> GetAllConge(){
        List<Conge> conges= congeRepository.findAll();
        for (Conge c : conges){
            c.setEmploye(employeFeignClient.getEmployeById(c.getEmployeId()));
        }
        for (Conge c : conges){
            c.setValidateur(administrateurFeignClient.getAdministrateurById(c.getAdministrateurId()));
        }
        return conges;
    }

    public Conge GetCongeById(Long id){
        Conge conge = congeRepository.findById(id).orElse(null);
        conge.setEmploye(employeFeignClient.getEmployeById(conge.getEmployeId()));
        conge.setValidateur(administrateurFeignClient.getAdministrateurById(conge.getAdministrateurId()));
        return conge;
    }
    public Conge updateConge(Conge conge, Long id) {
        return congeRepository.findById(id).map(conge1 -> {
            conge1.setType(conge.getType());
            conge1.setDateDebut(conge.getDateDebut());
            conge1.setDateFin(conge.getDateFin());
            conge1.setCommentaire(conge.getCommentaire());
            conge1.setDateCreation(conge.getDateCreation());
            conge1.setDateValidation(conge.getDateValidation());
            conge1.setAffecteSurRapport(conge.isAffecteSurRapport());
            conge1.setNombreJours(conge.getNombreJours());
            conge1.setEmployeId(conge.getEmployeId());
            conge1.setAdministrateurId(conge.getAdministrateurId());
                    //employeRepository1.setPassword(passwordEncoder.encode(user.getPassword()));
                    return congeRepository.save(conge1);
                }

        ).orElseThrow((() -> new RuntimeException("conge not found")));
    }



    public void DeleteConge(Long id){
        congeRepository.deleteById(id);
    }

    // Récupérer les demandes de congé selon le statut
    public List<Conge> findDemandesByStatut(StatusConge statut) {
        List<Conge> conges = congeRepository.findByStatut(statut);  // Utilisation du repository pour récupérer les demandes
        for (Conge c : conges){
            c.setEmploye(employeFeignClient.getEmployeById(c.getEmployeId()));
        }
        for (Conge c : conges){
            c.setValidateur(administrateurFeignClient.getAdministrateurById(c.getAdministrateurId()));
        }
        return conges;
    }

    // Méthode pour valider/refuser la demande de congé
    public Conge validerDemande(Long id, StatusConge statut, String commentaire) {
        Conge conge = congeRepository.findById(id).orElse(null);
        conge.setStatut(statut);   // Mettre à jour le statut
        conge.setCommentaire(commentaire); // Ajouter un commentaire si refusé
        return congeRepository.save(conge);  // Sauvegarder l'entité mise à jour
    }

    public boolean isEmployeEnCongeApprouve(Long employeId, LocalDate date) {
        List<Conge> conges = congeRepository.findByEmployeIdAndStatut(employeId, StatusConge.APPROUVE);

        return conges.stream()
                .anyMatch(conge ->
                        (date.isEqual(conge.getDateDebut()) || date.isEqual(conge.getDateFin()) ||
                                (date.isAfter(conge.getDateDebut()) && date.isBefore(conge.getDateFin())))
                );
    }


    public List<Conge> getCongesByEmployeAndMonth(Long employeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return congeRepository.findByEmployeIdAndDateDebutBetween(employeId, startDate, endDate);
    }
}
