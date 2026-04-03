package com.example.test.controller;

import com.example.test.model.Utilisateur;
import com.example.test.service.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class InscriptionController {

    @FXML private TextField     nomField, prenomField, emailField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private Label         errorLabel;
    @FXML private Button        inscriptionBtn;
    @FXML private RadioButton   roleEtudiant, roleRecruteur;

    private final UtilisateurService service = new UtilisateurService();

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        roleEtudiant.setToggleGroup(group);
        roleRecruteur.setToggleGroup(group);
        roleEtudiant.setSelected(true);
    }

    @FXML
    private void handleInscription() {
        errorLabel.setText("");

        String nom     = nomField.getText().trim();
        String prenom  = prenomField.getText().trim();
        String email   = emailField.getText().trim();
        String mdp     = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!mdp.equals(confirm)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        Utilisateur.Role role = roleRecruteur.isSelected()
                ? Utilisateur.Role.RECRUTEUR
                : Utilisateur.Role.ETUDIANT;

        try {
            Utilisateur u = service.inscrire(nom, prenom, email, mdp, role);
            SessionManager.setUtilisateurConnecte(u);
            Stage stage = (Stage) inscriptionBtn.getScene().getWindow();
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
    private void goToConnexion() {
        try {
            Stage stage = (Stage) inscriptionBtn.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/test/Connexion.fxml"));
            stage.setResizable(false);
            stage.setScene(new Scene(root, 860, 520));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}