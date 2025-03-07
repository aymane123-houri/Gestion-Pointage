package com.example.administrateurservice.controller;


import com.example.administrateurservice.entity.Administrateur;
import com.example.administrateurservice.service.AdministrateurService;
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

import java.util.List;
import java.util.Map;

@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Administrateurs",
                description = " Gerer les opération de banque",
                version = "1.0.0"
        ),

        servers = @Server(
                url = "http://administrateur-service:8087/"
                //url = "http://localhost:8087/"
        )
)

@RestController
@RequestMapping("/Administrateurs")
public class AdministrateurController {
        private final AdministrateurService administrateurService;

        public AdministrateurController(AdministrateurService administrateurService) {

                this.administrateurService = administrateurService;
        }

        @Operation(
                summary = "Ajouter Un Administrateur",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required = true,
                        content = @Content(
                                mediaType = "Application/json",
                                schema = @Schema(implementation = Administrateur.class)
                        )
                ),
                responses = {
                        @ApiResponse(responseCode = "200",
                                description = "ajouter par succéses",
                                content = @Content(
                                        mediaType = "Application/json",
                                        schema = @Schema(implementation = Administrateur.class))
                        ),

                        @ApiResponse(responseCode = "400",description = "erreur données"),
                        @ApiResponse(responseCode ="500", description = "erreur server")
                }
        )
        @PostMapping
        public ResponseEntity<Administrateur> CreateAdministrateur(@RequestBody Administrateur administrateur){
                Administrateur administrateur1= administrateurService.createAdministrateur(administrateur);
                return ResponseEntity.ok(administrateur1);
        }


        @Operation(
                summary="Recuprer Liste des Adminnistrateurs",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Succès",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Administrateur.class)
                                )
                        ),
                        @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
                }  )
        @GetMapping
        public ResponseEntity<List<Administrateur>> GetAllAdministrateurs(){
                List<Administrateur> administrateurs = administrateurService.getAllAdministrateurs();
                return ResponseEntity.ok(administrateurs);
        }


        @Operation(
                summary = "recuperer un Administrateur par son Id",
                parameters = @Parameter(
                        name = "id",
                        required = true
                ),
                responses = {
                        @ApiResponse(responseCode = "200",
                                description = "bien recuperer",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Administrateur.class))
                        ),
                        @ApiResponse(responseCode = "404",description = "administrateur pas trouvé ")
                }
        )
        @GetMapping("/{id}")
        public ResponseEntity<Administrateur> GetAdministrateurById(@PathVariable Long id){
                Administrateur administrateur = administrateurService.getAdministrateurById(id);
                return ResponseEntity.ok(administrateur);
        }

        @Operation(
                summary = "Mettre à jour un Administrateur par ID",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required = true,
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = Administrateur.class)
                        )
                ),
                parameters = @Parameter(
                        name = "id",
                        description = "ID de l'Administrateur à mettre à jour",
                        required = true
                ),
                responses = {
                        @ApiResponse(responseCode = "200", description = "administrateur mis à jour avec succès",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Administrateur.class)
                                )
                        ),
                        @ApiResponse(responseCode = "404", description = "Administrateur introuvable"),
                        @ApiResponse(responseCode = "500", description = "Erreur serveur")
                }
        )
        @PutMapping("/{id}")
        public ResponseEntity<Administrateur> UpdateAdministrateur(@RequestBody Administrateur administrateur,@PathVariable Long id){
                Administrateur administrateur1= administrateurService.updateAdministrateur(administrateur,id);
                return ResponseEntity.ok(administrateur1);
        }

        @Operation(
                summary = "Supprimer un Administrateur par ID",
                parameters = @Parameter(
                        name = "id",
                        description = "ID de l'Administrateur à supprimer",
                        required = true
                ),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Administrateur supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Administrateur introuvable"),
                        @ApiResponse(responseCode = "500", description = "Erreur serveur")
                }
        )
        @DeleteMapping("/{id}")
        public void DeleteAdministrateur(@PathVariable Long id){
                administrateurService.deleteAdministrateur(id);
        }


        @Operation(
                summary = "Récupérer un Administrateur par email",
                parameters = @Parameter(
                        name = "email",
                        description = "Email de l'Administrateur",
                        required = true
                ),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Administrateur récupéré avec succès",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = Map.class) // Car on retourne un Map<String, String>
                                )
                        ),
                        @ApiResponse(responseCode = "404", description = "Administrateur introuvable")
                }
        )
        @GetMapping("/email/{email}")
        public ResponseEntity<Administrateur> GetAdministrateurByEmail(@PathVariable String email){
                Administrateur administrateur=administrateurService.getAdministrateurByEmail(email);
                return ResponseEntity.ok(administrateur);
        }
}
