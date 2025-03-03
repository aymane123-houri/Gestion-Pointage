package com.example.employeservice.controller;

import com.example.employeservice.entity.Employe;
import com.example.employeservice.service.EmployeService;
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

import java.util.Arrays;
import java.util.Base64;
import java.util.List;


@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Employes",
                description = " Gerer les Employes",
                version = "1.0.0"
        ),

        servers = @Server(
                url = "http://localhost:8081/"
        )
)
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/Employes")
public class EmployerController {

    private final EmployeService employeService;

    public EmployerController(EmployeService employeService) {
        this.employeService = employeService;
    }

    @Operation(
            summary = "Ajouter Un Employer",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "Application/json",
                            schema = @Schema(implementation = Employe.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "ajouter par succéses",
                            content = @Content(
                                    mediaType = "Application/json",
                                    schema = @Schema(implementation = Employe.class))
                    ),

                    @ApiResponse(responseCode = "400",description = "erreur données"),
                    @ApiResponse(responseCode ="500", description = "erreur server")
            }
    )
    @PostMapping
    public ResponseEntity<Employe> CreateEmploye( @RequestBody Employe employe){
        System.out.println("Données reçues: " + employe);

        Employe employe1= employeService.createEmploye(employe);
        return ResponseEntity.ok(employe1);
    }


    @Operation(
            summary="Recuprer Liste des Employes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Employe.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Paramètre d'entrée non valide")
            }  )
    @GetMapping
    public ResponseEntity<List<Employe>> GetAllEmploye(){
        List<Employe> employes = employeService.GetAllEmploye();
        return ResponseEntity.ok(employes);
    }


    @Operation(
            summary = "recuperer un Employe par son Id",
            parameters = @Parameter(
                    name = "id",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "bien recuperer",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Employe.class))
                    ),
                    @ApiResponse(responseCode = "404",description = "employe pas trouvé ")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Employe> GetEmployeById(@PathVariable Long id){
        Employe employe = employeService.getEmployeById(id);
        return ResponseEntity.ok(employe);
    }



    @Operation(
            summary = "Mettre à jour un Employe par ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Employe.class)
                    )
            ),
            parameters = @Parameter(
                    name = "id",
                    description = "ID de l'Employer à mettre à jour",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employe mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Employe.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Employe introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Employe> UpdateEmploye(@RequestBody Employe employe,@PathVariable Long id){
        Employe employe1= employeService.updateEmploye(employe,id);
        return ResponseEntity.ok(employe1);
    }


    @Operation(
            summary = "Supprimer un Employe par ID",
            parameters = @Parameter(
                    name = "id",
                    description = "ID de l'Employe à supprimer",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Employe supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Employe introuvable"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @DeleteMapping("/{id}")
    public void DeleteEmploye(@PathVariable Long id){
        employeService.deleteEmploye(id);
    }





    @GetMapping("/employes/last-matricule")
    public ResponseEntity<String> getLastMatricule() {
        String lastMatricule = employeService.getLastMatricule();
        return ResponseEntity.ok(lastMatricule);
    }






    @GetMapping("/rechercher/{nom}")
    public List<Employe> rechercherParNom(@PathVariable String nom) {
        return employeService.rechercherParNom(nom);
    }

    @GetMapping("/departement/{departement}")
    public List<Employe> rechercherParDepartement(@PathVariable String departement) {
        return employeService.rechercherParDepartement(departement);
    }


    /*@PutMapping("/{employeId}/statut/{statut}")
    public void changerStatutEmploye(@PathVariable String employeId, @PathVariable String statut) {
        employeService.changerStatutEmploye(employeId, statut);
    }*/
    @GetMapping("/api/employes/count")
    public long getNombreEmployes() {
        return employeService.getNombreEmployes();
    }

}


