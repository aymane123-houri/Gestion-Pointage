package dcc.tp2.securityservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur {

    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String motDePasse; // Stocké en hashé (BCrypt)

    private String role;

    private String telephone;


    private LocalDateTime dateCreation = LocalDateTime.now();


}