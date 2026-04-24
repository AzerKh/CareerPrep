package com.example.test.service;

import com.example.test.dao.CandidatureDAO;
import com.example.test.dao.CandidatureDAOImpl;
import com.example.test.model.Candidature;

import java.util.List;

public class CandidatureService {

    private final CandidatureDAO dao = new CandidatureDAOImpl();

    /**
     * Returns all candidatures for the given offer with full student + CV details.
     */
    public List<Candidature> getCandidaturesParOffre(int offreId) {
        return dao.findByOffreId(offreId);
    }

    /**
     * Removes a candidature (recruteur rejects a candidate).
     */
    public void supprimerCandidature(int candidatureId) {
        dao.delete(candidatureId);
    }
}