package com.example.test.dao;

import com.example.test.model.Candidature;
import java.util.List;

public interface CandidatureDAO {
    /**
     * Returns all candidatures for a given offer,
     * with full student and CV details (JOIN query).
     */
    List<Candidature> findByOffreId(int offreId);

    /**
     * Deletes a specific candidature by its id.
     */
    void delete(int candidatureId);
}