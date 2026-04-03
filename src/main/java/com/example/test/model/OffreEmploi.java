package com.example.test.model;

import java.time.LocalDate;

public class OffreEmploi {

    public enum TypeContrat { CDI, CDD, STAGE }

    private int id;
    private int utilisateurId;
    private String titre;
    private String entreprise;
    private String description;
    private TypeContrat typeContrat;
    private LocalDate dateLimite;
    private LocalDate dateAjout;
    private boolean cvAssocie;   // true if user linked their CV to this offer

    public OffreEmploi() {}

    public OffreEmploi(int utilisateurId, String titre, String entreprise,
                       String description, TypeContrat typeContrat, LocalDate dateLimite) {
        this.utilisateurId = utilisateurId;
        this.titre         = titre;
        this.entreprise    = entreprise;
        this.description   = description;
        this.typeContrat   = typeContrat;
        this.dateLimite    = dateLimite;
        this.dateAjout     = LocalDate.now();
        this.cvAssocie     = false;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getEntreprise() { return entreprise; }
    public void setEntreprise(String entreprise) { this.entreprise = entreprise; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }

    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }

    public LocalDate getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDate dateAjout) { this.dateAjout = dateAjout; }

    public boolean isCvAssocie() { return cvAssocie; }
    public void setCvAssocie(boolean cvAssocie) { this.cvAssocie = cvAssocie; }

    @Override
    public String toString() { return titre + " — " + entreprise; }
}