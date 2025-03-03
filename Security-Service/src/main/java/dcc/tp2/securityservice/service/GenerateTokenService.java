package dcc.tp2.securityservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GenerateTokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserDetailService userDetailService;

    public GenerateTokenService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserDetailService userDetailService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userDetailService = userDetailService;
    }

    /*public Map<String, String> generateToken(String email, String password){

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        if (authenticate.isAuthenticated()){
            Instant instant = Instant.now();

            String scopes =  authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

            JwtClaimsSet jwtClaimsSet_Access_token =  JwtClaimsSet.builder()
                    .issuer(authenticate.getName())
                    .subject(authenticate.getName())
                    .issuedAt(instant)
                    .expiresAt(instant.plus(2, ChronoUnit.MINUTES))
                    .claim("name",authenticate.getName())
                    .claim("scope",scopes)
                    .build();

            String Access_Token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_Access_token)).getTokenValue();

            JwtClaimsSet jwtClaimsSet_refresh_token =  JwtClaimsSet.builder()
                    .issuer(authenticate.getName())
                    .subject(authenticate.getName())
                    .issuedAt(instant)
                    .expiresAt(instant.plus(15, ChronoUnit.MINUTES))
                    .claim("name",authenticate.getName())
                    .build();
            String RefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_refresh_token)).getTokenValue();

            Map<String, String> token = new HashMap<>();
            token.put("Access_Token",Access_Token);
            token.put("Refresh_Token",RefreshToken);

            return token;
        }
        else
            return null;
    }*/


    public Map<String, String> generateToken(String username,  String password, String role){
        String combined = username+":"+role;

        System.out.println(username+" and "+password+" and "+role+" end "+combined);

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(combined, password)
        );
        System.out.println("Authentication successful: " + authenticate);

        if (authenticate.isAuthenticated()){
            Instant instant = Instant.now();

            String scopes =  authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
            System.out.println(scopes);
            JwtClaimsSet jwtClaimsSet_Access_token =  JwtClaimsSet.builder()
                    .issuer(authenticate.getName())
                    .subject(authenticate.getName())
                    .issuedAt(instant)
                    //.expiresAt(instant.plus(2, ChronoUnit.MINUTES))
                    .expiresAt(instant.plus(30, ChronoUnit.DAYS))
                    .claim("name",authenticate.getName())
                    .claim("scope",scopes)
                    .build();

            String Access_Token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_Access_token)).getTokenValue();

            JwtClaimsSet jwtClaimsSet_refresh_token =  JwtClaimsSet.builder()
                    .issuer(authenticate.getName())
                    .subject(authenticate.getName())
                    .issuedAt(instant)
                    .expiresAt(instant.plus(15, ChronoUnit.MINUTES))
                    .claim("name",authenticate.getName())
                    .build();
            String RefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_refresh_token)).getTokenValue();

            Map<String, String> token = new HashMap<>();
            token.put("Access_Token",Access_Token);
            token.put("Refresh_Token",RefreshToken);

            return token;
        }
        else
            return null;
    }
    public  Map<String,String> generateRefreshToken(String RefreshToken){

        if(RefreshToken==null){
            return Map.of("Message error","Refresh_Token est necessaite");
        }

        Jwt decoded = jwtDecoder.decode(RefreshToken);

        String  username = decoded.getSubject();

        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        Instant instant = Instant.now();

        String scopes =  userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet_Access_token =  JwtClaimsSet.builder()
                .issuer(userDetails.getUsername())
                .subject(userDetails.getUsername())
                .issuedAt(instant)
                .expiresAt(instant.plus(2, ChronoUnit.MINUTES))
                .claim("name",userDetails.getUsername())
                .claim("SCOPE",scopes)
                .build();
        String Access_Token = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet_Access_token)).getTokenValue();

        Map<String, String> token = new HashMap<>();
        token.put("Access_Token",Access_Token);
        token.put("Refresh_Token",RefreshToken);

        return token;

    }
}
