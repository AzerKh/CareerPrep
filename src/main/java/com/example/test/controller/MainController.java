package com.example.test.controller;

import com.example.test.model.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label     userNameLabel;   // ← Label not Text
    @FXML private Label     avatarLabel;     // ← Label not Text
    @FXML private Label     userRoleLabel;

    @FXML private Button btnProfil;
    @FXML private Button btnCV;
    @FXML private Button btnOffres;
    @FXML private Button btnIA;

    private static final String ACTIVE =
            "-fx-background-color: #534AB7; -fx-text-fill: white; " +
                    "-fx-background-radius: 8; -fx-padding: 10 14; -fx-font-size: 13px; " +
                    "-fx-alignment: CENTER-LEFT; -fx-cursor: hand;";

    private static final String INACTIVE =
            "-fx-background-color: transparent; -fx-text-fill: #c5c2e8; " +
                    "-fx-background-radius: 8; -fx-padding: 10 14; -fx-font-size: 13px; " +
                    "-fx-alignment: CENTER-LEFT; -fx-cursor: hand;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utilisateur u = SessionManager.getUtilisateurConnecte();
        if (u != null) {
            userNameLabel.setText(u.getPrenom() + " " + u.getNom());

            String initials = String.valueOf(u.getPrenom().charAt(0)).toUpperCase()
                    + String.valueOf(u.getNom().charAt(0)).toUpperCase();
            avatarLabel.setText(initials);

            if (u.isEtudiant()) {
                userRoleLabel.setText("Étudiant");
                userRoleLabel.setStyle("-fx-background-color: rgba(29,158,117,0.25);" +
                        "-fx-text-fill: #9fd3c7; -fx-font-size: 10px;" +
                        "-fx-padding: 2 8; -fx-background-radius: 20;");
                btnCV.setVisible(true);  btnCV.setManaged(true);
                btnIA.setVisible(true);  btnIA.setManaged(true);
                btnOffres.setText("  Offres d'emploi");
            } else {
                userRoleLabel.setText("Recruteur");
                userRoleLabel.setStyle("-fx-background-color: rgba(239,159,39,0.25);" +
                        "-fx-text-fill: #f0c080; -fx-font-size: 10px;" +
                        "-fx-padding: 2 8; -fx-background-radius: 20;");
                btnCV.setVisible(false); btnCV.setManaged(false);
                btnIA.setVisible(false); btnIA.setManaged(false);
                btnOffres.setText("  Mes offres");
            }
        }
        setAllInactive();
        showProfil();
    }

    @FXML public void showProfil() {
        setAllInactive(); btnProfil.setStyle(ACTIVE);
        loadContent("/com/example/test/ProfilContent.fxml");
    }

    @FXML public void showCV() {
        setAllInactive(); btnCV.setStyle(ACTIVE);
        loadContent("/com/example/test/CV.fxml");
    }

    @FXML public void showOffres() {
        setAllInactive(); btnOffres.setStyle(ACTIVE);
        Utilisateur u = SessionManager.getUtilisateurConnecte();
        if (u != null && u.isRecruteur())
            loadContent("/com/example/test/OffresRecruteur.fxml");
        else
            loadContent("/com/example/test/OffresEtudiant.fxml");
    }

    @FXML public void showIA() {
        setAllInactive(); btnIA.setStyle(ACTIVE);
        loadContent("/com/example/test/IA.fxml");
    }

    @FXML private void handleDeconnexion() {
        SessionManager.deconnecter();
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/test/Connexion.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(new Scene(root, 860, 520));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String path) {
        try {
            Node content = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAllInactive() {
        btnProfil.setStyle(INACTIVE);
        btnCV.setStyle(INACTIVE);
        btnOffres.setStyle(INACTIVE);
        btnIA.setStyle(INACTIVE);
    }
}