package com.example.test.service;

import com.example.test.dao.CVDAO;
import com.example.test.dao.CVDAOImpl;
import com.example.test.dao.OffreEmploiDAO;
import com.example.test.dao.OffreEmploiDAOImpl;
import com.example.test.model.CV;
import com.example.test.model.OffreEmploi;
import com.example.test.model.Utilisateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class OffreEmploiService {

    private final OffreEmploiDAO offreDAO = new OffreEmploiDAOImpl();
    private final CVDAO          cvDAO    = new CVDAOImpl();

    // ── RECRUTEUR ─────────────────────────────────────────────────────────

    public OffreEmploi ajouter(Utilisateur u, String titre, String entreprise,
                               String description, OffreEmploi.TypeContrat type,
                               LocalDate dateLimite) {
        valider(titre, entreprise);
        OffreEmploi o = new OffreEmploi(u.getId(), titre, entreprise, description, type, dateLimite);
        offreDAO.create(o);
        return o;
    }

    public void modifier(OffreEmploi o, String titre, String entreprise,
                         String description, OffreEmploi.TypeContrat type, LocalDate dateLimite) {
        valider(titre, entreprise);
        o.setTitre(titre);
        o.setEntreprise(entreprise);
        o.setDescription(description);
        o.setTypeContrat(type);
        o.setDateLimite(dateLimite);
        offreDAO.update(o);
    }

    public void supprimer(int offreId) {
        offreDAO.delete(offreId);
    }

    /** Returns offers created by this recruteur */
    public List<OffreEmploi> getOffresUtilisateur(int utilisateurId) {
        return offreDAO.findByUtilisateurId(utilisateurId);
    }

    public int compterCandidatures(int offreId) {
        return offreDAO.compterCandidatures(offreId);
    }

    // ── ETUDIANT ──────────────────────────────────────────────────────────

    /** Returns ALL offers from ALL recruiters (for etudiant view) */
    public List<OffreEmploi> getAllOffres() {
        return offreDAO.findAll();
    }

    /** Associates the student's CV with the given offer (= postuler) */
    public void associerCV(int offreId, int utilisateurId) {
        Optional<CV> cv = cvDAO.findByUtilisateurId(utilisateurId);
        if (cv.isEmpty())
            throw new IllegalStateException("Vous devez d'abord créer un CV avant de postuler.");
        offreDAO.associerCV(offreId, cv.get().getId(), utilisateurId);
    }

    /** Removes the student's application */
    public void dissocierCV(int offreId, int utilisateurId) {
        offreDAO.dissocierCV(offreId, utilisateurId);
    }

    /** Checks if the student already applied to this offer */
    public boolean aDejaPostule(int offreId, int utilisateurId) {
        return offreDAO.aDejaPostule(offreId, utilisateurId);
    }

    public Optional<OffreEmploi> findById(int id) {
        return offreDAO.findById(id);
    }

    private void valider(String titre, String entreprise) {
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre de l'offre est requis.");
        if (entreprise == null || entreprise.isBlank())
            throw new IllegalArgumentException("Le nom de l'entreprise est requis.");
    }
}