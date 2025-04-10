package com.example.rapportservice.controller;


import com.example.rapportservice.entity.Rapport;
import com.example.rapportservice.model.Employe;
import com.example.rapportservice.model.Statistiques;
import com.example.rapportservice.service.RapportService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Rapports",
                description = " Gerer les Rapports",
                version = "1.0.0"
        ),

        servers = @Server(
                //url = "http://localhost:8085/"
                url = "http://rapport-service:8085/"
        )
)
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/Rapports")
public class RapportController {

private final RapportService rapportService;

    public RapportController(RapportService rapportService) {
        this.rapportService = rapportService;
    }

    @Operation(
            summary = "Ajouter Un Rapport",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "Application/json",
                            schema = @Schema(implementation = Rapport.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "ajouter par succéses",
                            content = @Content(
                                    mediaType = "Application/json",
                                    schema = @Schema(implementation = Rapport.class))
                    ),

                    @ApiResponse(responseCode = "400",description = "erreur données"),
                    @ApiResponse(responseCode ="500", description = "erreur server")
            }
    )
    @PostMapping
    public ResponseEntity<Rapport> CreateRapport(@RequestBody Rapport rapport) {
        return ResponseEntity.ok(rapportService.createRapport(rapport));
    }



    @Operation(
            summary = "recuperer un Rapport par son Id",
            parameters = @Parameter(
                    name = "id",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "bien recuperer",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Rapport.class))
                    ),
                    @ApiResponse(responseCode = "404",description = "rapport pas trouvé ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Rapport> getRapportById(@PathVariable String id) {
        return ResponseEntity.ok(rapportService.getRapportById(id));
    }


    @Operation(
            summary="Recuprer Liste des Rapports",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Rapport.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
            }  )
    @GetMapping
    public ResponseEntity<List<Rapport>> getAllRapport() {

        return ResponseEntity.ok(rapportService.getAllRapport());
    }


    @Operation(
            summary = "Mettre à jour un rapport par ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Rapport.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID de Rapport à mettre à jour",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rapport mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Rapport.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Rapport introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Rapport> UpdateRapport(@RequestBody Rapport rapport,@PathVariable String id) {
        return ResponseEntity.ok(rapportService.updateRapport(rapport,id));
    }

    @Operation(
            summary = "Supprimer un rapport par ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID de Rapport à supprimer",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rapport supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Rapport introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @DeleteMapping("/{id}")
    public void DeleteRapport(@PathVariable String id) {
        rapportService.deleteRapport(id);
    }






/*

    @PostMapping("/mensuel/{employeId}/{mois}/{annee}")
    public Rapport genererRapportMensuel(@PathVariable Long employeId, @PathVariable int mois, @PathVariable int annee) {
        return rapportService.genererRapportMensuel(employeId, mois, annee);
    }

    @PostMapping("/hebdomadaire/{employeId}/{semaine}")
    public Rapport genererRapportHebdomadaire(@PathVariable Long employeId, @PathVariable String semaine) {
        return rapportService.genererRapportHebdomadaire(employeId, LocalDate.parse(semaine));
    }

    @GetMapping("/employe/{employeId}")
    public List<Rapport> getRapportsParEmploye(@PathVariable Long employeId) {
        return rapportService.getRapportsParEmploye(employeId);
    }

    @GetMapping("/periode")
    public List<Rapport> getRapportsParPeriode(@RequestParam String debut, @RequestParam String fin) {
        return rapportService.getRapportsParPeriode(debut, fin);
    }

    @GetMapping("/heuresSupp/{employeId}/{mois}/{annee}")
    public int calculerHeuresSupplementaires(@PathVariable Long employeId, @PathVariable int mois, @PathVariable int annee) {
        return rapportService.calculerHeuresSupplementaires(employeId, mois, annee);
    }

    @GetMapping("/retards/{employeId}/{mois}/{annee}")
    public int calculerRetards(@PathVariable Long employeId, @PathVariable int mois, @PathVariable int annee) {
        return rapportService.calculerRetards(employeId, mois, annee);
    }

    @GetMapping("/absences/{employeId}/{mois}/{annee}")
    public int calculerAbsences(@PathVariable Long employeId, @PathVariable int mois, @PathVariable int annee) {
        return rapportService.calculerAbsences(employeId, mois, annee);
    }*/
@Operation(
        summary = "Générer automatiquement les rapports quotidiens",
        description = "Cette méthode est exécutée automatiquement chaque jour à 23h30 pour générer les rapports journaliers."
)
    @Scheduled(cron = "00 17 23 * * ?")
    public void genererRapportJournalieres() {
        rapportService.genererRapportsQuotidiens();
    }

    @Operation(
            summary = "Obtenir les statistiques d'un mois donné",
            parameters = {
                    @Parameter(name = "mois", description = "Mois concerné (1-12)", required = true),
                    @Parameter(name = "annee", description = "Année concernée", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Integer>> getStatistiques(
            @RequestParam int mois,
            @RequestParam int annee) {
        return ResponseEntity.ok(rapportService.calculerStatistiques(mois, annee));
    }

    @Operation(
            summary = "Obtenir les statistiques du jour précédent",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statistiques du jour précédent récupérées",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Statistiques.class))
                    )
            }
    )
    @GetMapping("/statistiques-jour-precedent")
    public Statistiques getStatistiquesJourPrecedent() {
        return rapportService.getStatistiquesJourPrecedent();
    }

    @Operation(
            summary = "Obtenir un rapport mensuel pour un employé donné",
            parameters = {
                    @Parameter(name = "idEmploye", description = "ID de l'employé", required = true),
                    @Parameter(name = "year", description = "Année du rapport", required = true),
                    @Parameter(name = "month", description = "Mois du rapport (1-12)", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rapport trouvé",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rapport.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Aucun rapport trouvé pour cet employé et cette période")
            }
    )
    @GetMapping("/{idEmploye}/{year}/{month}")
    public ResponseEntity<Rapport> getRapportByEmploye(@PathVariable Long idEmploye, @PathVariable int year, @PathVariable int month) {
        Rapport rapport = rapportService.getRapportByEmployeAndPeriode(idEmploye, year, month);
        return ResponseEntity.ok(rapport);
    }







    // Endpoint pour les statistiques mensuelles
    // Endpoint pour les statistiques mensuelles
    @GetMapping("/statistiques/mensuelles")
    public Map<String, Map<String, Integer>> getStatistiquesMensuelles(
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getStatistiquesMensuelles(mois, annee);
    }

    // Endpoint pour les statistiques annuelles
    @GetMapping("/statistiques/annuelles")
    public Map<String, Map<String, Integer>> getStatistiquesAnnuelles(
            @RequestParam int annee) {
        return rapportService.getStatistiquesAnnuelles(annee);
    }

    // Endpoint pour les statistiques par département
    @GetMapping("/statistiques/departement")
    public Map<String, Map<String, Map<String, Integer>>> getStatistiquesParDepartement(
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getStatistiquesParDepartement(mois, annee);
    }


    // Endpoint pour les statistiques par employé
    @GetMapping("/statistiques/employe")
    public Map<String, Integer> getStatistiquesParEmploye(
            @RequestParam Long employeId,
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getStatistiquesParEmploye(employeId, mois, annee);
    }
    @GetMapping("/statistiques/taux-absenteisme")
    public double getTauxAbsenteisme(
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getTauxAbsenteisme(mois, annee);
    }

    @GetMapping("/statistiques/taux-retards")
    public double getTauxRetards(
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getTauxRetards(mois, annee);
    }


    @GetMapping("/statistiques/employe-details")
    public Map<String, Object> getStatistiquesDetailleesParEmploye(
            @RequestParam Long employeId,
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getStatistiquesDetailleesParEmploye(employeId, mois, annee);
    }
    @GetMapping("/statistiques/jour-semaine")
    public Map<String, Map<String, Integer>> getStatistiquesParJourSemaine(
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getStatistiquesParJourSemaine(mois, annee);
    }
    @GetMapping("/statistiques/heures-travaillees-moyennes")
    public double getHeuresTravailleesMoyennes(
            @RequestParam int mois,
            @RequestParam int annee) {
        return rapportService.getHeuresTravailleesMoyennes(mois, annee);
    }

}
