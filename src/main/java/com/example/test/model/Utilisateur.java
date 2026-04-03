package com.example.test.model;

import java.time.LocalDate;

public class Utilisateur {

    public enum Role { ETUDIANT, RECRUTEUR }

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasseHash;
    private String formation;
    private String competences;
    private String experiences;
    private LocalDate dateInscription;
    private Role role;

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String motDePasseHash, Role role) {
        this.nom            = nom;
        this.prenom         = prenom;
        this.email          = email;
        this.motDePasseHash = motDePasseHash;
        this.role           = role;
        this.dateInscription = LocalDate.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasseHash() { return motDePasseHash; }
    public void setMotDePasseHash(String h) { this.motDePasseHash = h; }

    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }

    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }

    public String getExperiences() { return experiences; }
    public void setExperiences(String experiences) { this.experiences = experiences; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate d) { this.dateInscription = d; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isEtudiant()   { return role == Role.ETUDIANT; }
    public boolean isRecruteur()  { return role == Role.RECRUTEUR; }

    @Override
    public String toString() { return prenom + " " + nom + " (" + email + ")"; }
}