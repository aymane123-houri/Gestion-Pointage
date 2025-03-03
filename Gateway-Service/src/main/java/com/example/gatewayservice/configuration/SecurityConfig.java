package com.example.gatewayservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
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
    }

}
