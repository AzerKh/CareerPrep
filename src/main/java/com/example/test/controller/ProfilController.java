package com.example.test.controller;

import com.example.test.model.Utilisateur;
import com.example.test.service.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfilController implements Initializable {

    @FXML private TextField nomField, prenomField, emailField;
    @FXML private TextArea  formationArea, competencesArea, experiencesArea;
    @FXML private Label     messageLabel;

    private final UtilisateurService service = new UtilisateurService();
    private Utilisateur utilisateur;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();
        if (utilisateur != null) {
            nomField.setText(utilisateur.getNom());
            prenomField.setText(utilisateur.getPrenom());
            emailField.setText(utilisateur.getEmail());
            formationArea.setText(utilisateur.getFormation() != null ? utilisateur.getFormation() : "");
            competencesArea.setText(utilisateur.getCompetences() != null ? utilisateur.getCompetences() : "");
            experiencesArea.setText(utilisateur.getExperiences() != null ? utilisateur.getExperiences() : "");
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

    @FXML
    private void handleModifierProfil() {
        try {
            service.modifierProfil(utilisateur,
                    formationArea.getText().trim(),
                    competencesArea.getText().trim(),
                    experiencesArea.getText().trim());
            succes("Profil enregistré.");
        } catch (Exception e) {
            erreur("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleDeconnexion() {
        SessionManager.deconnecter();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/test/Connexion.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 400));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void succes(String msg) {
        messageLabel.setStyle("-fx-text-fill: #1D9E75; -fx-font-size: 12px;");
        messageLabel.setText(msg);
    }

    private void erreur(String msg) {
        messageLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
        messageLabel.setText(msg);
    }
    @FXML
    private void handleGererCV() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/test/CV.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root, 650, 750));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}