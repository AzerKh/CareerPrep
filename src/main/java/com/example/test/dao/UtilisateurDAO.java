package com.example.test.dao;

import com.example.test.model.Utilisateur;
import java.util.Optional;

public interface UtilisateurDAO {
    void create(Utilisateur utilisateur);
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findById(int id);
    void update(Utilisateur utilisateur);
    boolean emailExists(String email);
}
