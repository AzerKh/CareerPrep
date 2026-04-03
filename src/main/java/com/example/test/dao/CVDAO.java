package com.example.test.dao;

import com.example.test.model.CV;
import java.util.Optional;

public interface CVDAO {
    void create(CV cv);
    Optional<CV> findByUtilisateurId(int utilisateurId);
    void update(CV cv);
    void delete(int id);
}