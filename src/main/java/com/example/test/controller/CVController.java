package com.example.test.controller;

import com.example.test.model.CV;
import com.example.test.model.Utilisateur;
import com.example.test.service.CVService;
import com.example.test.service.PDFExportService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CVController implements Initializable {

    @FXML private TextField   titreField;
    @FXML private TextArea    formationArea, competencesArea, experiencesArea, languesArea;
    @FXML private ImageView   photoView;
    @FXML private Button      photoBtn, sauvegarderBtn, exporterBtn;
    @FXML private Label       messageLabel;

    private final CVService       cvService  = new CVService();
    private final PDFExportService pdfService = new PDFExportService();

    private Utilisateur utilisateur;
    private CV          cv;
    private String      photoPath;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();

        // Load existing CV if any
        Optional<CV> existing = cvService.getCVUtilisateur(utilisateur.getId());
        if (existing.isPresent()) {
            cv = existing.get();
            chargerCV();
            exporterBtn.setDisable(false);
        } else {
            exporterBtn.setDisable(true);
        }
    }

    private void chargerCV() {
        titreField.setText(cv.getTitre() != null ? cv.getTitre() : "");
        formationArea.setText(cv.getFormation() != null ? cv.getFormation() : "");
        competencesArea.setText(cv.getCompetences() != null ? cv.getCompetences() : "");
        experiencesArea.setText(cv.getExperiences() != null ? cv.getExperiences() : "");
        languesArea.setText(cv.getLangues() != null ? cv.getLangues() : "");

        if (cv.getPhotoPath() != null) {
            try {
                photoView.setImage(new Image("file:" + cv.getPhotoPath()));
                photoPath = cv.getPhotoPath();
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleChoisirPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une photo");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(photoBtn.getScene().getWindow());
        if (file != null) {
            photoPath = file.getAbsolutePath();
            photoView.setImage(new Image("file:" + photoPath));
        }
    }

    @FXML
    private void handleSauvegarder() {
        messageLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
        try {
            String titre       = titreField.getText().trim();
            String formation   = formationArea.getText().trim();
            String competences = competencesArea.getText().trim();
            String experiences = experiencesArea.getText().trim();
            String langues     = languesArea.getText().trim();

            if (cv == null) {
                cv = cvService.creer(utilisateur, titre, formation, competences, experiences, langues, photoPath);
            } else {
                cvService.modifier(cv, titre, formation, competences, experiences, langues, photoPath);
            }

            exporterBtn.setDisable(false);
            succes("CV sauvegardé avec succès !");

        } catch (IllegalArgumentException | IllegalStateException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleExporterPDF() {
        if (cv == null) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le PDF");
        chooser.setInitialFileName(utilisateur.getNom() + "_CV.pdf");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = chooser.showSaveDialog(exporterBtn.getScene().getWindow());

        if (file != null) {
            try {
                pdfService.exporter(cv, utilisateur, file.getAbsolutePath());
                succes("PDF exporté : " + file.getName());
            } catch (Exception e) {
                messageLabel.setText("Erreur export PDF : " + e.getMessage());
            }
        }
    }

    private void succes(String msg) {
        messageLabel.setStyle("-fx-text-fill: #1D9E75; -fx-font-size: 12px;");
        messageLabel.setText(msg);
    }
    @FXML
    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/test/Profil.fxml"));
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 700));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}