package com.example.congeservice.Controller;

import com.example.congeservice.Entity.Conge;
import com.example.congeservice.Entity.StatusConge;
import com.example.congeservice.Service.CongeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/Conges")
public class CongeController {
    private CongeService congeService;

    public CongeController(CongeService congeService) {
        this.congeService = congeService;
    }

    @PostMapping
    public ResponseEntity<Conge> CreateConge(@RequestBody Conge conge){
        conge.setDateCreation(LocalDateTime.now()); // Ajout automatique de la date de création
        conge.setStatut(StatusConge.EN_ATTENTE); // Statut initial
        Conge savedConge = congeService.createConge(conge);
        return ResponseEntity.ok(savedConge);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conge> GetCongeById(@PathVariable Long id){
        Conge conge = congeService.GetCongeById(id);
        return ResponseEntity.ok(conge);

    }

    @GetMapping
    public ResponseEntity<List<Conge>> GetAllConge(){
        List<Conge> conges = congeService.GetAllConge();
        return ResponseEntity.ok(conges);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conge> UpdateConge(@RequestBody Conge conge,@PathVariable Long id){
        Conge conge1 = congeService.updateConge(conge,id);
        return ResponseEntity.ok(conge1);
    }

    @DeleteMapping("/{id}")
    public void DeleteCongeById(@PathVariable Long id){
        congeService.DeleteConge(id);
    }

    // Endpoint pour récupérer les demandes en attente
    @GetMapping("/en-attente")
    public ResponseEntity<List<Conge>> getDemandesEnAttente() {
        List<Conge> demandes = congeService.findDemandesByStatut(StatusConge.EN_ATTENTE);
        return ResponseEntity.ok(demandes);
    }

    // Endpoint pour valider/refuser une demande de congé
    @PutMapping("/{id}/validation")
    public ResponseEntity<Conge> validerDemandeConge(@PathVariable Long id, @RequestBody Conge conge) {
        // Utilisez directement l'objet `Conge` reçu dans le corps de la requête
        Conge updatedConge = congeService.validerDemande(id, conge.getStatut(), conge.getCommentaire());
        return ResponseEntity.ok(updatedConge);
    }

    // Vérifier si un employé est en congé à une date donnée
    @GetMapping("/employe/{employeId}/en-conge")
    public boolean isEmployeEnConge(@PathVariable Long employeId, @RequestParam String date) {
        LocalDate dateConcernee = LocalDate.parse(date);
        return congeService.isEmployeEnCongeApprouve(employeId, dateConcernee);
    }


    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<Conge>> getCongesByEmploye(
            @PathVariable Long employeId,
            @RequestParam int year,
            @RequestParam int month) {

        List<Conge> conges = congeService.getCongesByEmployeAndMonth(employeId, year, month);
        return ResponseEntity.ok(conges);
    }
}
