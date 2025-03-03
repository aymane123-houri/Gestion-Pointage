package com.example.administrateurservice.service;

import com.example.administrateurservice.entity.Administrateur;
import com.example.administrateurservice.repository.AdministrateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrateurService {

    private final AdministrateurRepository administrateurRepository;

    public AdministrateurService(AdministrateurRepository administrateurRepository) {
        this.administrateurRepository = administrateurRepository;
    }

    public Administrateur createAdministrateur(Administrateur administrateur){
        return administrateurRepository.save(administrateur);
    }

    public Administrateur getAdministrateurById(Long id){
        return administrateurRepository.findById(id).orElse(null);
    }

    public List<Administrateur> getAllAdministrateurs(){
        return administrateurRepository.findAll();
    }

    public Administrateur updateAdministrateur(Administrateur administrateur,Long id){
        return administrateurRepository.findById(id).map(administrateur1 -> {
            administrateur1.setNom(administrateur.getNom());
            administrateur1.setPrenom(administrateur.getPrenom());
            administrateur1.setEmail(administrateur.getEmail());
            administrateur1.setRole(administrateur.getRole());
            administrateur1.setMotDePasse(administrateur.getMotDePasse());
            administrateur1.setTelephone(administrateur.getTelephone());
                    return administrateurRepository.save(administrateur1);
                }

        ).orElseThrow((() -> new RuntimeException("administrateur not found")));
    }

    public void deleteAdministrateur(Long id){
        administrateurRepository.deleteById(id);
    }


    public Administrateur getAdministrateurByEmail(String email){
        return administrateurRepository.getAdministrateurByEmail(email);
    }

}
