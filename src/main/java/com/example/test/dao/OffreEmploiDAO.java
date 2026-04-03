package com.example.test.dao;

import com.example.test.model.OffreEmploi;
import java.util.List;
import java.util.Optional;

public interface OffreEmploiDAO {
    void create(OffreEmploi offre);
    List<OffreEmploi> findByUtilisateurId(int utilisateurId); // recruteur's offers
    List<OffreEmploi> findAll();                               // all offers for etudiant
    Optional<OffreEmploi> findById(int id);
    void update(OffreEmploi offre);
    void delete(int id);

    // Applications (candidatures)
    void associerCV(int offreId, int cvId, int etudiantId);
    void dissocierCV(int offreId, int etudiantId);
    boolean aDejaPostule(int offreId, int etudiantId);
    int compterCandidatures(int offreId);
}