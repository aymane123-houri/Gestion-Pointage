package com.example.employeservice.service;

import com.example.employeservice.entity.Employe;
import com.example.employeservice.repository.EmployeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeService {
    private final EmployeRepository employeRepository;

    public EmployeService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    public Employe createEmploye(Employe employe){
        return employeRepository.save(employe);
    }

    public Employe getEmployeById(Long id){
        return employeRepository.findById(id).orElse(null);
    }

    public List<Employe> GetAllEmploye(){
        return employeRepository.findAll();
    }

    public void deleteEmploye(Long id){
        employeRepository.deleteById(id);
    }

    public Employe updateEmploye(Employe employe, Long id) {
        return employeRepository.findById(id).map(employe1 -> {
                    employe1.setNom(employe.getNom());
                    employe1.setPrenom(employe.getPrenom());
                    employe1.setEmail(employe.getEmail());
                    employe1.setAdresse(employe.getAdresse());
                    employe1.setTelephone(employe.getTelephone());
                    employe1.setCin(employe.getCin());
                    employe1.setDateEmbauche(employe.getDateEmbauche());
                    employe1.setDateNaissance(employe.getDateNaissance());
                    employe1.setGenre(employe.getGenre());
                    employe1.setMatricule(employe.getMatricule());
                    employe1.setMatricule(employe.getMatricule());
                    employe1.setDepartement(employe.getDepartement());
                    employe1.setPhotoProfil(employe.getPhotoProfil());
                    employe1.setPoste(employe.getPoste());
                    employe1.setSalaire(employe.getSalaire());

                    //employeRepository1.setPassword(passwordEncoder.encode(user.getPassword()));
                    return employeRepository.save(employe1);
                }

        ).orElseThrow((() -> new RuntimeException("employe not found")));
    }


    public String getLastMatricule() {
        String lastMatricule = employeRepository.findLastMatricule();
        return (lastMatricule != null) ? lastMatricule : "EMP0";
    }





    public List<Employe> rechercherParNom(String nom) {
        return employeRepository.findByNom(nom);
    }

    public List<Employe> rechercherParDepartement(String departement) {
        return employeRepository.findByDepartement(departement);
    }



    /*public void changerStatutEmploye(Long employeId, String statut) {
        Optional<Employe> employeOpt = employeRepository.findById(employeId);
        if (employeOpt.isPresent()) {
            Employe employe = employeOpt.get();
            employe.setStatut(statut);
            employeRepository.save(employe);
        }
    }

   */

    public long getNombreEmployes() {
        return employeRepository.count();
    }
}
