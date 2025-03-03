package dcc.tp2.securityservice.service;

import dcc.tp2.securityservice.feignClient.AdministratorFeign;
import dcc.tp2.securityservice.model.Administrateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailService implements UserDetailsService {

    private final AdministratorFeign administratorFeign;

    public UserDetailService( AdministratorFeign administratorFeign) {
        this.administratorFeign = administratorFeign;
    }

 /*   @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        dcc.tp2.securityservice.model.User user = userFeignClient.getUserCredentials(email);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("Guest"));
        return new User(user.getNom() + ' ' + user.getPrenom(), user.getPassword(), authorities);
    }*/

    @Override
    public UserDetails loadUserByUsername(String combinedUsername) throws UsernameNotFoundException {
        String[] parts = combinedUsername.split(":");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("Invalid username format. Expected format: email:type");
        }

        String email = parts[0];
        String userType = parts[1];
        System.out.println("DANS USER DETAIL ");
        Administrateur administrateur = new Administrateur();

        if (userType.equals("ADMINISTRATEUR")) {
            System.out.println("YES ADMINISTRATEUR");
            administrateur = administratorFeign.getAdministrator(email);

        }

        if (userType.equals("RH")) {
            System.out.println("YES RH");
            administrateur = administratorFeign.getAdministrator(email);
            System.out.println(administrateur);
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(administrateur.getRole()));

        return new User(administrateur.getNom()+" "+administrateur.getPrenom() , "{noop}" + administrateur.getMotDePasse(), authorities);
    }

}


