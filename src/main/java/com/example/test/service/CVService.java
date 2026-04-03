package com.example.test.service;

import com.example.test.dao.CVDAO;
import com.example.test.dao.CVDAOImpl;
import com.example.test.model.CV;
import com.example.test.model.Utilisateur;

import java.util.Optional;

public class CVService {

    private final CVDAO dao = new CVDAOImpl();

    public CV creer(Utilisateur utilisateur, String titre, String formation,
                    String competences, String experiences, String langues, String photoPath) {
        if (dao.findByUtilisateurId(utilisateur.getId()).isPresent())
            throw new IllegalStateException("Vous avez déjà un CV. Vous pouvez le modifier.");

        validerTitre(titre);
        CV cv = new CV(utilisateur.getId(), titre);
        cv.setFormation(formation);
        cv.setCompetences(competences);
        cv.setExperiences(experiences);
        cv.setLangues(langues);
        cv.setPhotoPath(photoPath);
        dao.create(cv);
        return cv;
    }

    public void modifier(CV cv, String titre, String formation,
                         String competences, String experiences, String langues, String photoPath) {
        validerTitre(titre);
        cv.setTitre(titre);
        cv.setFormation(formation);
        cv.setCompetences(competences);
        cv.setExperiences(experiences);
        cv.setLangues(langues);
        if (photoPath != null && !photoPath.isBlank()) cv.setPhotoPath(photoPath);
        dao.update(cv);
    }

    /**
     * Saves the CV object directly as-is — used by the IA module
     * to apply AI-generated improvements without going through the form.
     */
    public void sauvegarderDirectement(CV cv) {
        dao.update(cv);
    }

    public Optional<CV> getCVUtilisateur(int utilisateurId) {
        return dao.findByUtilisateurId(utilisateurId);
    }

    public void supprimer(int cvId) {
        dao.delete(cvId);
    }

    private void validerTitre(String titre) {
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre du CV est requis.");
    }
}