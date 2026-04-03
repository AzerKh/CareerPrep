package com.example.test.controller;

import com.example.test.model.Utilisateur;
import com.example.test.service.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfilContentController implements Initializable {

    @FXML private TextField nomField, prenomField, emailField;
    @FXML private Label     infoMessage;

    private final UtilisateurService service = new UtilisateurService();
    private Utilisateur utilisateur;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();
        if (utilisateur != null) {
            nomField.setText(utilisateur.getNom());
            prenomField.setText(utilisateur.getPrenom());
            emailField.setText(utilisateur.getEmail());
        }
    }

    @FXML
    private void handleModifierInfos() {
        try {
            service.modifierInfos(utilisateur,
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    emailField.getText().trim());
            succes("Informations mises à jour.");
        } catch (IllegalArgumentException e) {
            erreur(e.getMessage());
        }
    }

    private void succes(String msg) {
        infoMessage.setStyle("-fx-text-fill: #1D9E75; -fx-font-size: 12px;");
        infoMessage.setText(msg);
    }

    private void erreur(String msg) {
        infoMessage.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
        infoMessage.setText(msg);
    }
}