package dcc.tp2.securityservice.controller;

import dcc.tp2.securityservice.model.Credentials;
import dcc.tp2.securityservice.service.GenerateTokenService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@OpenAPIDefinition(
        info = @Info(
                title = "Gestion des Utilisateurs",
                description = " Gerer les Utilisateurs",
                version = "1.0.0"
        ),

        servers = @Server(
                url = "http://security-service:9996/"
        )
)
@RestController
@CrossOrigin("http://localhost:4200/")
public class AuthenticationController {
    private final GenerateTokenService generateJWTService;

    public AuthenticationController(GenerateTokenService generateJWTService) {
        this.generateJWTService = generateJWTService;
    }

    @Operation(
            summary = "Connexion et génération de JWT",
            description = "Authentifie un utilisateur avec ses identifiants et retourne un token JWT.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants de l'utilisateur",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Credentials.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Connexion réussie, JWT retourné"),
                    @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Credentials credentials){
        return generateJWTService.generateToken(credentials.getEmail(), credentials.getPassword(),credentials.getRole());
    }
    @Operation(
            summary = "Rafraîchir le token JWT",
            description = "Génère un nouveau token JWT à partir des identifiants de l'utilisateur.",
            parameters = {
                    @Parameter(name = "email", description = "Email de l'utilisateur", required = true),
                    @Parameter(name = "password", description = "Mot de passe de l'utilisateur", required = true),
                    @Parameter(name = "role", description = "Rôle de l'utilisateur", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nouveau token JWT généré"),
                    @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
                    @ApiResponse(responseCode = "500", description = "Erreur serveur")
            }
    )
    @PostMapping("/refreshToken")
    public Map<String, String> refresh(@RequestParam String email, @RequestParam String password ,@RequestParam String role){
        return generateJWTService.generateToken(email, password,role);
    }

}
