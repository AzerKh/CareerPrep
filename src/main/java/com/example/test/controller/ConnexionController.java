package com.example.test.controller;

import com.example.test.model.Utilisateur;
import com.example.test.service.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ConnexionController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        connexionBtn;

    private final UtilisateurService service = new UtilisateurService();

    @FXML
    private void handleConnexion() {
        errorLabel.setText("");
        String email = emailField.getText().trim();
        String mdp   = passwordField.getText();

        try {
            Utilisateur u = service.connecter(email, mdp);
            SessionManager.setUtilisateurConnecte(u);
            Stage stage = (Stage) connexionBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/test/Main.fxml"));
            stage.setResizable(true);
            stage.setScene(new Scene(root, 1000, 680));
            stage.centerOnScreen();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void goToInscription() {
        try {
            Stage stage = (Stage) connexionBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/test/Inscription.fxml"));
            stage.setResizable(false);
            stage.setScene(new Scene(root, 860, 580));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}