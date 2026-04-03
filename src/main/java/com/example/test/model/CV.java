package com.example.test.model;

import java.time.LocalDate;

public class CV {

    private int id;
    private int utilisateurId;
    private String titre;
    private String formation;
    private String competences;
    private String experiences;
    private String langues;
    private String photoPath;   // chemin local vers la photo
    private LocalDate dateCreation;
    private LocalDate dateModification;

    public CV() {}

    public CV(int utilisateurId, String titre) {
        this.utilisateurId = utilisateurId;
        this.titre = titre;
        this.dateCreation = LocalDate.now();
        this.dateModification = LocalDate.now();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }

    public String getCompetences() { return competences; }
    public void setCompetences(String competences) { this.competences = competences; }

    public String getExperiences() { return experiences; }
    public void setExperiences(String experiences) { this.experiences = experiences; }

    public String getLangues() { return langues; }
    public void setLangues(String langues) { this.langues = langues; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public LocalDate getDateModification() { return dateModification; }
    public void setDateModification(LocalDate dateModification) { this.dateModification = dateModification; }
}