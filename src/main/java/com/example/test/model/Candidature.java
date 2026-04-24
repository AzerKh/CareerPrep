package com.example.test.model;

import java.time.LocalDate;

public class Candidature {

    private int id;
    private int offreId;
    private int cvId;
    private int etudiantId;
    private LocalDate dateCandidature;

    // Loaded via JOIN
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;
    private String cvTitre;
    private String cvCompetences;
    private String cvFormation;
    private String cvExperiences;
    private String cvLangues;
    private String cvPhotoPath;

    public Candidature() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOffreId() { return offreId; }
    public void setOffreId(int offreId) { this.offreId = offreId; }

    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }

    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }

    public LocalDate getDateCandidature() { return dateCandidature; }
    public void setDateCandidature(LocalDate d) { this.dateCandidature = d; }

    public String getEtudiantNom() { return etudiantNom; }
    public void setEtudiantNom(String s) { this.etudiantNom = s; }

    public String getEtudiantPrenom() { return etudiantPrenom; }
    public void setEtudiantPrenom(String s) { this.etudiantPrenom = s; }

    public String getEtudiantEmail() { return etudiantEmail; }
    public void setEtudiantEmail(String s) { this.etudiantEmail = s; }

    public String getCvTitre() { return cvTitre; }
    public void setCvTitre(String s) { this.cvTitre = s; }

    public String getCvCompetences() { return cvCompetences; }
    public void setCvCompetences(String s) { this.cvCompetences = s; }

    public String getCvFormation() { return cvFormation; }
    public void setCvFormation(String s) { this.cvFormation = s; }

    public String getCvExperiences() { return cvExperiences; }
    public void setCvExperiences(String s) { this.cvExperiences = s; }

    public String getCvLangues() { return cvLangues; }
    public void setCvLangues(String s) { this.cvLangues = s; }

    public String getCvPhotoPath() { return cvPhotoPath; }
    public void setCvPhotoPath(String s) { this.cvPhotoPath = s; }

    public String getNomComplet() { return etudiantPrenom + " " + etudiantNom; }
}