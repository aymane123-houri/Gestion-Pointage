package dcc.tp2.securityservice.controller;

import dcc.tp2.securityservice.model.Credentials;
import dcc.tp2.securityservice.service.GenerateTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("http://localhost:4200/")
public class AuthenticationController {
    private final GenerateTokenService generateJWTService;

    public AuthenticationController(GenerateTokenService generateJWTService) {
        this.generateJWTService = generateJWTService;
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Credentials credentials){
        return generateJWTService.generateToken(credentials.getEmail(), credentials.getPassword(),credentials.getRole());
    }
    @PostMapping("/refreshToken")
    public Map<String, String> refresh(@RequestParam String email, @RequestParam String password ,@RequestParam String role){
        return generateJWTService.generateToken(email, password,role);
    }

}
