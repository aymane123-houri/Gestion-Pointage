package com.example.congeservice.Controller;

import com.example.congeservice.Entity.Conge;
import com.example.congeservice.Entity.StatusConge;
import com.example.congeservice.Service.CongeService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Utilisateurs",
                description = " Gerer les Utilisateurs",
                version = "1.0.0"
        ),

        servers = @Server(
                //url = "http://localhost:8081/"
                url = "http://conge-service:8089/"
        )
)
@RestController
@RequestMapping("/Conges")
public class CongeController {
    private CongeService congeService;

    public CongeController(CongeService congeService) {
        this.congeService = congeService;
    }

    @Operation(
            summary = "Ajouter Un Conge",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "Application/json",
                            schema = @Schema(implementation = Conge.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "ajouter par succéses",
                            content = @Content(
                                    mediaType = "Application/json",
                                    schema = @Schema(implementation = Conge.class))
                    ),

                    @ApiResponse(responseCode = "400",description = "erreur données"),
                    @ApiResponse(responseCode ="500", description = "erreur server")
            }
    )
    @PostMapping
    public ResponseEntity<Conge> CreateConge(@RequestBody Conge conge){
        conge.setDateCreation(LocalDateTime.now()); // Ajout automatique de la date de création
        conge.setStatut(StatusConge.EN_ATTENTE); // Statut initial
        Conge savedConge = congeService.createConge(conge);
        return ResponseEntity.ok(savedConge);
    }

    @Operation(
            summary = "recuperer un Conge par son Id",
            parameters = @Parameter(
                    name = "id",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "bien recuperer",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conge.class))
                    ),
                    @ApiResponse(responseCode = "404",description = "conge pas trouvé ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Conge> GetCongeById(@PathVariable Long id){
        Conge conge = congeService.GetCongeById(id);
        return ResponseEntity.ok(conge);

    }


    @Operation(
            summary="Recuprer Liste des Congés",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conge.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
            }  )
    @GetMapping
    public ResponseEntity<List<Conge>> GetAllConge(){
        List<Conge> conges = congeService.GetAllConge();
        return ResponseEntity.ok(conges);
    }

    @Operation(
            summary = "Mettre à jour un Conge par ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Conge.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID de le Conge à mettre à jour",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conge mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conge.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Conge introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Conge> UpdateConge(@RequestBody Conge conge,@PathVariable Long id){
        Conge conge1 = congeService.updateConge(conge,id);
        return ResponseEntity.ok(conge1);
    }
    @Operation(
            summary = "Supprimer un conge par ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID de conge à supprimer",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "conge supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "conge introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @DeleteMapping("/{id}")
    public void DeleteCongeById(@PathVariable Long id){
        congeService.DeleteConge(id);
    }


    @Operation(
            summary = "Récupérer les demandes de congé en attente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des demandes en attente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conge.class)
                            )
                    )
            }
    )
    // Endpoint pour récupérer les demandes en attente
    @GetMapping("/en-attente")
    public ResponseEntity<List<Conge>> getDemandesEnAttente() {
        List<Conge> demandes = congeService.findDemandesByStatut(StatusConge.EN_ATTENTE);
        return ResponseEntity.ok(demandes);
    }


    @Operation(
            summary = "Valider ou refuser une demande de congé",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Conge.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID de la demande de congé à valider/refuser",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Demande de congé mise à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conge.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Demande de congé introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    // Endpoint pour valider/refuser une demande de congé
    @PutMapping("/{id}/validation")
    public ResponseEntity<Conge> validerDemandeConge(@PathVariable Long id, @RequestBody Conge conge) {
        // Utilisez directement l'objet `Conge` reçu dans le corps de la requête
        Conge updatedConge = congeService.validerDemande(id, conge.getStatut(), conge.getCommentaire());
        return ResponseEntity.ok(updatedConge);
    }

    @Operation(
            summary = "Vérifier si un employé est en congé à une date donnée",
            parameters = {
                    @Parameter(name = "employeId", description = "ID de l'employé", required = true),
                    @Parameter(name = "date", description = "Date à vérifier (format YYYY-MM-DD)", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Renvoie 'true' si l'employé est en congé, sinon 'false'")
            }
    )
    // Vérifier si un employé est en congé à une date donnée
    @GetMapping("/employe/{employeId}/en-conge")
    public boolean isEmployeEnConge(@PathVariable Long employeId, @RequestParam String date) {
        LocalDate dateConcernee = LocalDate.parse(date);
        return congeService.isEmployeEnCongeApprouve(employeId, dateConcernee);
    }


    @Operation(
            summary = "Récupérer les congés d'un employé pour un mois donné",
            parameters = {
                    @Parameter(name = "employeId", description = "ID de l'employé", required = true),
                    @Parameter(name = "year", description = "Année", required = true),
                    @Parameter(name = "month", description = "Mois", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des congés de l'employé pour la période spécifiée",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Conge.class)
                            )
                    )
            }
    )
    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<Conge>> getCongesByEmploye(
            @PathVariable Long employeId,
            @RequestParam int year,
            @RequestParam int month) {

        List<Conge> conges = congeService.getCongesByEmployeAndMonth(employeId, year, month);
        return ResponseEntity.ok(conges);
    }
}
