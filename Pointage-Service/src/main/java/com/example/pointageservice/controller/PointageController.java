package com.example.pointageservice.controller;


import com.example.pointageservice.entity.Pointage;
import com.example.pointageservice.repository.PointageRepository;
import com.example.pointageservice.service.PointageService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Pointages",
                description = " Gerer les Pointages",
                version = "1.0.0"
        ),

        servers = @Server(
                url = "http://localhost:8083/"
        )
)
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/Pointages")
public class PointageController {

    public final PointageService pointageService;

    public PointageController(PointageService pointageService) {
        this.pointageService = pointageService;
    }


    @Operation(
            summary = "Ajouter Un Pointage",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "Application/json",
                            schema = @Schema(implementation = Pointage.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "ajouter par succéses",
                            content = @Content(
                                    mediaType = "Application/json",
                                    schema = @Schema(implementation = Pointage.class))
                    ),

                    @ApiResponse(responseCode = "400",description = "erreur données"),
                    @ApiResponse(responseCode ="500", description = "erreur server")
            }
    )

    /*public ResponseEntity<Pointage> CreatePointage(@RequestBody Pointage pointage){
        Pointage pointage1= pointageService.createPointage(pointage);
        return ResponseEntity.ok(pointage1);
    }*/
    // Endpoint pour effectuer un pointage (entrée ou sortie)
    @PostMapping("/enregistrer/{employeId}")
    public ResponseEntity<?> enregistrerPointage(@PathVariable Long employeId) {
        try {
            Pointage pointage = pointageService.createPointage(employeId);

            // Si l'heure de sortie a été enregistrée, cela signifie que c'était une sortie
            if (pointage.getDateHeureSortie() != null) {
                return ResponseEntity.ok().body("Pointage de sortie enregistré pour l'employé " + employeId);
            } else {
                return ResponseEntity.ok().body("Pointage d'entrée enregistré pour l'employé " + employeId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement du pointage: " + e.getMessage());
        }
    }

    // Endpoint pour obtenir l'historique des pointages d'un employé
    @GetMapping("/historique/{employeId}")
    public ResponseEntity<?> getHistoriquePointages(@PathVariable Long employeId) {
        try {
            // Appel à un service pour récupérer l'historique des pointages
            List<Pointage> pointage = pointageService.getHistoriquePointages(employeId);
            return ResponseEntity.ok(pointage);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération de l'historique : " + e.getMessage());
        }
    }



    @Operation(
            summary = "recuperer un Pointage par son Id",
            parameters = @Parameter(
                    name = "id",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "bien recuperer",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Pointage.class))
                    ),
                    @ApiResponse(responseCode = "404",description = "pointage pas trouvé ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Pointage> GetPointageById(@PathVariable String id){
        Pointage pointage=pointageService.getPointageById(id);
        return ResponseEntity.ok(pointage);
    }


    @Operation(
            summary="Recuprer Liste des Pointages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Pointage.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
            }  )
    @GetMapping
    public ResponseEntity<List<Pointage>> GetAllPointages(){
        List<Pointage> pointages = pointageService.GetAllPointage();
        return ResponseEntity.ok(pointages);
    }



    @Operation(
            summary = "Mettre à jour un pointage par ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Pointage.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID de pointage à mettre à jour",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pointage mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Pointage.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Pointage introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Pointage> UpdatePointage(Pointage pointage,String id){
        Pointage pointage1=pointageService.updatePointage(pointage,id);
        return ResponseEntity.ok(pointage1);
    }


    @Operation(
            summary = "Supprimer un pointage par ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID de pointage à supprimer",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pointage supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Pointage introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @DeleteMapping("/{id}")
    public void DeletePointage(@PathVariable String id){
        pointageService.deletePointage(id);
    }




    @PostMapping("/entree/{employeId}")
    public Pointage enregistrerPointageEntree(@PathVariable Long employeId) {
        return pointageService.enregistrerPointageEntree(employeId);
    }

    @PostMapping("/sortie/{employeId}")
    public Pointage enregistrerPointageSortie(@PathVariable Long employeId) {
        return pointageService.enregistrerPointageSortie(employeId);
    }

    @GetMapping("/dernier/{employeId}")
    public Pointage getDernierPointage(@PathVariable Long employeId) {
        return pointageService.getDernierPointage(employeId);
    }

    @GetMapping("/employe/{id}/{date}")
    public ResponseEntity<List<Pointage>> getPointages(@PathVariable Long id, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Pointage> pointages = pointageService.getPointagesParEmployeEtDate(id, localDate);
        if (pointages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(pointages);
    }


    @GetMapping("/api/pointages")
    public List<Pointage> getAllPointages(@RequestParam String debut, @RequestParam String fin) {
        return pointageService.getAllPointages(debut, fin);
    }
}
