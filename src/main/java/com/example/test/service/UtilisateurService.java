package com.example.test.service;

import com.example.test.dao.UtilisateurDAO;
import com.example.test.dao.UtilisateurDAOImpl;
import com.example.test.model.Utilisateur;
import com.example.test.util.PasswordUtil;

import java.util.Optional;

public class UtilisateurService {

    private final UtilisateurDAO dao = new UtilisateurDAOImpl();

    public Utilisateur inscrire(String nom, String prenom, String email,
                                String motDePasse, Utilisateur.Role role) {
        validerNomPrenom(nom, prenom);
        validerEmail(email);
        validerMotDePasse(motDePasse);

        if (dao.emailExists(email))
            throw new IllegalArgumentException("Un compte existe déjà avec cet email.");

        Utilisateur u = new Utilisateur(nom, prenom, email, PasswordUtil.hash(motDePasse), role);
        dao.create(u);
        return u;
    }

    public Utilisateur connecter(String email, String motDePasse) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email requis.");
        if (motDePasse == null || motDePasse.isBlank()) throw new IllegalArgumentException("Mot de passe requis.");

        Optional<Utilisateur> opt = dao.findByEmail(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Email ou mot de passe incorrect.");

        Utilisateur u = opt.get();
        if (!PasswordUtil.verify(motDePasse, u.getMotDePasseHash()))
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");

        return u;
    }

    public void modifierProfil(Utilisateur u, String formation, String competences, String experiences) {
        u.setFormation(formation);
        u.setCompetences(competences);
        u.setExperiences(experiences);
        dao.update(u);
    }

    public void modifierInfos(Utilisateur u, String nom, String prenom, String email) {
        validerNomPrenom(nom, prenom);
        validerEmail(email);
        if (!email.equals(u.getEmail()) && dao.emailExists(email))
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        dao.update(u);
    }

    private void validerNomPrenom(String nom, String prenom) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom requis.");
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Prénom requis.");
    }

    private void validerEmail(String email) {
        if (email == null || !email.matches("^[\\w.+\\-]+@[a-zA-Z\\d\\-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Email invalide.");
    }

    private void validerMotDePasse(String mdp) {
        if (mdp == null || mdp.length() < 8)
            throw new IllegalArgumentException("Mot de passe : minimum 8 caractères.");
    }
}