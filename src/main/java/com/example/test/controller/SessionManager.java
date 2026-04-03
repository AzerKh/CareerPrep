package com.example.test.controller;

import com.example.test.model.Utilisateur;

public class SessionManager {

    private static Utilisateur utilisateurConnecte;

    private SessionManager() {}

    public static void setUtilisateurConnecte(Utilisateur u) { utilisateurConnecte = u; }
    public static Utilisateur getUtilisateurConnecte() { return utilisateurConnecte; }
    public static boolean estConnecte() { return utilisateurConnecte != null; }
    public static void deconnecter() { utilisateurConnecte = null; }
}