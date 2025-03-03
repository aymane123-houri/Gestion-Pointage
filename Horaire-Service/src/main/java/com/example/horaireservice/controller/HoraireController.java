package com.example.horaireservice.controller;


import com.example.horaireservice.entity.Horaire;
import com.example.horaireservice.model.Employe;
import com.example.horaireservice.service.HoraireService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Horaires",
                description = " Gerer les Horaires",
                version = "1.0.0"
        ),

        servers = @Server(
                url = "http://localhost:8084/"
        )
)
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/Horaires")
public class HoraireController {

    private final HoraireService horaireService;

    public HoraireController(HoraireService horaireService) {
        this.horaireService = horaireService;
    }

    @Operation(
            summary = "Ajouter Un Horaire",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "Application/json",
                            schema = @Schema(implementation = Horaire.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "ajouter par succéses",
                            content = @Content(
                                    mediaType = "Application/json",
                                    schema = @Schema(implementation = Horaire.class))
                    ),

                    @ApiResponse(responseCode = "400",description = "erreur données"),
                    @ApiResponse(responseCode ="500", description = "erreur server")
            }
    )
    @PostMapping
    public ResponseEntity<Horaire> createHoraire(@RequestBody Horaire horaire) {
        return ResponseEntity.ok(horaireService.createHoraire(horaire));
    }



    @Operation(
            summary = "recuperer un Horaire par son Id",
            parameters = @Parameter(
                    name = "id",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "bien recuperer",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Horaire.class))
                    ),
                    @ApiResponse(responseCode = "404",description = "horaire pas trouvé ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Horaire> getHoraireById(@PathVariable Long id) {
        return ResponseEntity.ok(horaireService.getHoraireById(id));
    }



    @Operation(
            summary="Recuprer Liste des Horaires",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Horaire.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
            }  )
    @GetMapping
    public ResponseEntity<List<Horaire>> getAllHoraire() {
        return ResponseEntity.ok(horaireService.getAllHoraire());
    }



    @Operation(
            summary = "Mettre à jour un horaire par ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Horaire.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID d'Horaire à mettre à jour",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Horaire mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Horaire.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Horaire introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Horaire> updateHoraire(@RequestBody Horaire horaire,@PathVariable Long id) {
        return ResponseEntity.ok(horaireService.updateHoraire(horaire,id));
    }

    @Operation(
            summary = "Supprimer un horaire par ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID de l'Horaire à supprimer",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Horaire supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Horaire introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @DeleteMapping("/{id}")
    public void deleteHoraire(@PathVariable Long id) {
        horaireService.deleteHoraire(id);
    }



    // Route pour rechercher un employé par nom
    @GetMapping("/rechercherEmploye/{nom}")
    public ResponseEntity<List<Employe>> rechercherEmployeParNom(@PathVariable String nom) {
        List<Employe> employes = horaireService.rechercherEmployeParNom(nom);
        if (employes.isEmpty()) {
            return ResponseEntity.status(404).body(null); // Aucun employé trouvé
        }
        return ResponseEntity.ok(employes);
    }


    // Méthode pour récupérer les horaires d'un employé pour une date donnée
   /* @GetMapping("/employe/{employeId}/{date}")
    public List<Horaire> getHorairesParEmployeEtDate(@PathVariable Long employeId, @PathVariable String date) {
        // Convertir la chaîne de caractères 'date' en LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Appeler le service pour récupérer les horaires
        return horaireService.getHorairesParEmployeEtDate(employeId, localDate);
    }*/

    @GetMapping("/employe/{employeId}")
    public List<Horaire> getHorairesParEmploye(@PathVariable Long employeId) {
        return horaireService.getHorairesParEmploye(employeId);
    }

}
