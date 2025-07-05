package com.example.anomalieservice.controller;


import com.example.anomalieservice.entity.Anomalie;
import com.example.anomalieservice.model.Statistique;
import com.example.anomalieservice.service.AnomalieService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Anomalie",
                description = " Gerer les Anomalie",
                version = "1.0.0"
        ),

        servers = @Server(
                //url = "http://localhost:8086/"
                url = "http://anomalie-service:8086/"
        )
)
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/Anomalie")
public class AnomalieController {

    private final AnomalieService anomalieService;

    public AnomalieController(AnomalieService anomalieService) {
        this.anomalieService = anomalieService;
    }

    @Operation(
            summary = "Ajouter Un Anomalie",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "Application/json",
                            schema = @Schema(implementation = Anomalie.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "ajouter par succéses",
                            content = @Content(
                                    mediaType = "Application/json",
                                    schema = @Schema(implementation = Anomalie.class))
                    ),

                    @ApiResponse(responseCode = "400",description = "erreur données"),
                    @ApiResponse(responseCode ="500", description = "erreur server")
            }
    )
    @PostMapping
    public ResponseEntity<Anomalie> CreateAnomalie(@RequestBody Anomalie anomalie){
        Anomalie anomalie1= anomalieService.createAnomalie(anomalie);
        return ResponseEntity.ok(anomalie1);
    }


    @Operation(
            summary="Recuprer Liste des Anomalies",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Anomalie.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
            }  )
    @GetMapping
    public ResponseEntity<List<Anomalie>> GetAllAnomalie(){
        List<Anomalie> anomalies = anomalieService.getAllAnomalie();
        return ResponseEntity.ok(anomalies);
    }


    @Operation(
            summary = "recuperer un Anomalie par son Id",
            parameters = @Parameter(
                    name = "id",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "bien recuperer",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Anomalie.class))
                    ),
                    @ApiResponse(responseCode = "404",description = "anomalie pas trouvé ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Anomalie> GetAnomalieById(@PathVariable String id){
        Anomalie anomalie = anomalieService.getAnomalieById(id);
        return ResponseEntity.ok(anomalie);
    }


    @Operation(
            summary = "Mettre à jour un Anomalie par ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Anomalie.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID de l'Anomalie à mettre à jour",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Anomalie mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Anomalie.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Anomalie introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Anomalie> UpdateAnomalie(@RequestBody Anomalie anomalie,@PathVariable String id){
        Anomalie anomalie1= anomalieService.updateAnomalie(anomalie,id);
        return ResponseEntity.ok(anomalie1);
    }



    @Operation(
            summary = "Supprimer un Anomalie par ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID de l'Anomalie à supprimer",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Anomalie supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Anomalie introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @DeleteMapping("/{id}")
    public void DeleteAnomalie(@PathVariable String id){
        anomalieService.deleteAnomalie(id);
    }



    // Endpoint pour valider une anomalie
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerAnomalie(@PathVariable String id) {
        try {
            Anomalie anomalieValidee = anomalieService.validerAnomalie(id);
            return ResponseEntity.ok(anomalieValidee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Détecter automatiquement les anomalies journalières",
            description = "Cette tâche est exécutée chaque jour à 23h30 pour détecter les anomalies"
    )
    // Exécute la tâche tous les jours à 23h59
    @Scheduled(cron = "00 27 23 * * ?")
    public void detecterAnomaliesJournalieres() {
        anomalieService.detecterAnomalies();
    }

    @Operation(
            summary = "Récupérer les anomalies du jour",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des anomalies du jour",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Anomalie.class)
                            )
                    )
            }
    )
    // Méthode pour récupérer les anomalies d'aujourd'hui
    @GetMapping("/anomalies/duJour")
    public List<Anomalie> getAnomaliesDuJour() {
        // Obtenir la date actuelle
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();  // Début de la journée à 00:00
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);  // Fin de la journée à 23:59:59.999999999

        // Log pour vérifier les dates
        System.out.println("🔍 Start of Day: " + startOfDay);
        System.out.println("🔍 End of Day: " + endOfDay);

        // Appel du service pour récupérer les anomalies entre ces deux dates
        List<Anomalie> anomalies = anomalieService.findAnomaliesByDate(startOfDay, endOfDay);

        // Log des résultats
        if (anomalies.isEmpty()) {
            System.out.println("⚠️ Aucune anomalie trouvée pour aujourd'hui !");
        } else {
            System.out.println("✅ Nombre d'anomalies trouvées : " + anomalies.size());
            anomalies.forEach(a -> System.out.println("📌 Anomalie ID: " + a.getId() + " - Employé ID: " + a.getEmploye_id() + " - Type: " + a.getType()));
        }

        return anomalies;
    }


    @Operation(
            summary = "Récupérer les retards d'un employé sur une période",
            parameters = {
                    @Parameter(name = "idEmploye", description = "ID de l'employé", required = true),
                    @Parameter(name = "year", description = "Année", required = true),
                    @Parameter(name = "month", description = "Mois", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des retards de l'employé",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Anomalie.class)
                            )
                    )
            }
    )
    // ✅ Endpoint pour récupérer les retards d'un employé sur une période
    @GetMapping("/{idEmploye}/{year}/{month}")
    public List<Anomalie> getRetardsByEmploye(@PathVariable Long idEmploye, @PathVariable int year, @PathVariable int month) {
        return anomalieService.getAnomaliesByEmployeAndPeriode(idEmploye, year, month);
    }


}
