package com.example.gatewayservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    /*@Bean
   public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll())  // ✅ Autorise les requêtes preflight CORS
                .authorizeExchange(auth -> auth.pathMatchers("/actuator/health").permitAll())
                .authorizeExchange(auth -> auth.pathMatchers("/administrateur-service/Administrateurs/email/{email}").permitAll())
                .authorizeExchange(auth -> auth.pathMatchers("/horaire-service/**").permitAll())  // ✅ Autorise horaire-service
                .authorizeExchange(auth -> auth.anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }*/


  /*  @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints publics
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/administrateur-service/Administrateurs/email/**").permitAll()

                        // Microservices et autorisations par rôle
                        .pathMatchers(HttpMethod.POST, "/administrateur-service/Administrateurs").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/administrateur-service/Administrateurs").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/administrateur-service/Administrateurs/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN")

                        .pathMatchers("/employe-service/**").hasAnyRole("RH", "SUPER_ADMIN")

                        .pathMatchers(HttpMethod.GET, "/pointage-service/**").hasAnyRole("RH", "ADMINISTRATEUR", "SUPER_ADMIN")
                        .pathMatchers("/pointage-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN")

                        .pathMatchers("/horaire-service/**").hasAnyRole("RH", "SUPER_ADMIN")

                        .pathMatchers(HttpMethod.GET, "/rapport-service/**").hasAnyRole("RH", "ADMINISTRATEUR", "SUPER_ADMIN")
                        .pathMatchers("/rapport-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN")

                        .pathMatchers("/anomalie-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN")

                        .pathMatchers("/conge-service/**").hasAnyRole("RH", "SUPER_ADMIN")

                        // Toutes les autres requêtes nécessitent une authentification
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter customJwtAuthenticationConverter() {
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1. Essayez d'abord le champ 'role' (pour un seul rôle)
            String role = jwt.getClaimAsString("role");

            // 2. Fallback sur 'scope' si 'role' n'existe pas
            if (role == null) {
                role = jwt.getClaimAsString("scope");
            }

            // 3. Fallback sur 'roles' (tableau) si les autres champs n'existent pas
            List<String> roles = jwt.getClaimAsStringList("roles");

            Collection<GrantedAuthority> authorities;

            if (roles != null && !roles.isEmpty()) {
                authorities = roles.stream()
                        .map(r -> "ROLE_" + r.toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            } else if (role != null) {
                authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                );
            } else {
                authorities = Collections.emptyList();
            }

            return authorities;
        });
        return converter;
    }
*/

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints publics
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/administrateur-service/Administrateurs/email/**").permitAll()

                        // Microservices et autorisations par rôle
                        .pathMatchers(HttpMethod.POST, "/administrateur-service/Administrateurs").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/administrateur-service/Administrateurs").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/administrateur-service/Administrateurs/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/administrateur-service/Administrateurs/**").hasRole("SUPER_ADMIN")

                        // **Employe-Service**
                        .pathMatchers(HttpMethod.POST, "/employe-service/Employes").hasAnyRole("RH","SUPER_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/employe-service/Employes/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN","RH")
                        .pathMatchers(HttpMethod.PUT, "/employe-service/Employes/**").hasAnyRole("RH", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/employe-service/Employes/**").hasAnyRole("RH","SUPER_ADMIN")


                        // **Pointage-Service**
                        .pathMatchers( "/pointage-service/**").hasAnyRole("RH", "ADMINISTRATEUR", "SUPER_ADMIN")

                        // **Horaire-Service**
                        .pathMatchers(HttpMethod.POST,"/horaire-service/**").hasAnyRole("RH", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT,"/horaire-service/**").hasAnyRole("RH", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE,"/horaire-service/**").hasAnyRole("RH", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/horaire-service/**").hasAnyRole("RH", "ADMINISTRATEUR", "SUPER_ADMIN")

                        // **Rapport-Service**
                        .pathMatchers(HttpMethod.GET, "/rapport-service/**").hasAnyRole("RH", "ADMINISTRATEUR", "SUPER_ADMIN")
                        .pathMatchers("/rapport-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN","RH")

                        // **Anomalie-Service**
                        .pathMatchers("/anomalie-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN","RH")

                        // **Conge-Service**
                        .pathMatchers("/conge-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN","RH")
                        .pathMatchers(HttpMethod.POST,"/conge-service/**").hasAnyRole("ADMINISTRATEUR", "SUPER_ADMIN","RH")

                        // Toutes les autres requêtes nécessitent une authentification
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter customJwtAuthenticationConverter() {
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1. Essayez d'abord le champ 'role' (pour un seul rôle)
            String role = jwt.getClaimAsString("role");

            // 2. Fallback sur 'scope' si 'role' n'existe pas
            if (role == null) {
                role = jwt.getClaimAsString("scope");
            }

            // 3. Fallback sur 'roles' (tableau) si les autres champs n'existent pas
            List<String> roles = jwt.getClaimAsStringList("roles");

            Collection<GrantedAuthority> authorities;

            if (roles != null && !roles.isEmpty()) {
                authorities = roles.stream()
                        .map(r -> "ROLE_" + r.toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            } else if (role != null) {
                authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
                );
            } else {
                authorities = Collections.emptyList();
            }

            return authorities;
        });
        return converter;
    }


}
