package com.example.test.controller;

import com.example.test.model.CV;
import com.example.test.model.Utilisateur;
import com.example.test.service.CVService;
import com.example.test.service.GeminiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class IAController implements Initializable {

    @FXML private TextArea  resultArea;
    @FXML private Label     statusLabel;
    @FXML private TextField posteField;
    @FXML private Button    btnAmeliorer, btnSuggerer, btnEntretien;
    @FXML private Button    btnAppliquer;  // apply CV improvement
    @FXML private HBox      appliquerBox;  // container for apply button
    @FXML private VBox      loadingBox;

    private final GeminiService geminiService = new GeminiService();
    private final CVService     cvService     = new CVService();

    private Utilisateur utilisateur;
    private CV          cv;

    // Tracks which feature produced the current result
    private String currentMode = ""; // "ameliorer" | "suggerer" | "entretien"

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();

        Optional<CV> optCV = cvService.getCVUtilisateur(utilisateur.getId());
        if (optCV.isPresent()) {
            cv = optCV.get();
            setStatus("✓  CV chargé : " + cv.getTitre(), "#1D9E75");
            enableButtons(true);
        } else {
            setStatus("⚠  Aucun CV trouvé. Créez votre CV d'abord.", "#c0392b");
            enableButtons(false);
        }

        loadingBox.setVisible(false);
        loadingBox.setManaged(false);
        appliquerBox.setVisible(false);
        appliquerBox.setManaged(false);
    }

    @FXML
    private void handleAmeliorer() {
        if (cv == null) return;
        currentMode = "ameliorer";
        resultArea.clear();
        hideApplyButton();
        showLoading("Amélioration du CV en cours...");

        new Thread(() -> {
            String result = geminiService.ameliorerCV(cv, utilisateur);
            Platform.runLater(() -> {
                hideLoading();
                resultArea.setText(result);
                // Show apply button only for CV improvement
                showApplyButton();
            });
        }).start();
    }

    @FXML
    private void handleSuggerer() {
        if (cv == null) return;
        currentMode = "suggerer";
        resultArea.clear();
        hideApplyButton();
        showLoading("Analyse des compétences manquantes...");

        new Thread(() -> {
            String result = geminiService.suggererCompetences(cv);
            Platform.runLater(() -> {
                hideLoading();
                resultArea.setText(result);
            });
        }).start();
    }

    @FXML
    private void handleEntretien() {
        if (cv == null) return;
        currentMode = "entretien";
        resultArea.clear();
        hideApplyButton();
        showLoading("Génération des questions d'entretien...");

        new Thread(() -> {
            String result = geminiService.simulerEntretien(cv, posteField.getText().trim());
            Platform.runLater(() -> {
                hideLoading();
                resultArea.setText(result);
            });
        }).start();
    }

    /**
     * Applies the AI-improved CV content back to the CV and saves it.
     * Parses the result and updates the relevant CV fields.
     */
    @FXML
    private void handleAppliquer() {
        if (cv == null || resultArea.getText().isBlank()) return;

        String improvedText = resultArea.getText();

        // Save the full improved text as the experiences field
        // (since it contains the full restructured CV content)
        cv.setExperiences(improvedText);
        cvService.sauvegarderDirectement(cv);

        setStatus("✅  Modifications appliquées et sauvegardées !", "#1D9E75");
        hideApplyButton();
    }

    @FXML
    private void handleCopier() {
        String text = resultArea.getText();
        if (!text.isBlank()) {
            javafx.scene.input.Clipboard cb = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(text);
            cb.setContent(content);
            setStatus("📋  Copié dans le presse-papiers !", "#534AB7");
        }
    }

    @FXML
    private void handleEffacer() {
        resultArea.clear();
        hideApplyButton();
        setStatus(cv != null ? "✓  CV chargé : " + cv.getTitre() : "", "#1D9E75");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void showLoading(String message) {
        loadingBox.setVisible(true);
        loadingBox.setManaged(true);
        setStatus(message, "#534AB7");
        enableButtons(false);
    }

    private void hideLoading() {
        loadingBox.setVisible(false);
        loadingBox.setManaged(false);
        setStatus("✓  Terminé", "#1D9E75");
        enableButtons(true);
    }

    private void showApplyButton() {
        appliquerBox.setVisible(true);
        appliquerBox.setManaged(true);
    }

    private void hideApplyButton() {
        appliquerBox.setVisible(false);
        appliquerBox.setManaged(false);
    }

    private void enableButtons(boolean enabled) {
        btnAmeliorer.setDisable(!enabled);
        btnSuggerer.setDisable(!enabled);
        btnEntretien.setDisable(!enabled);
    }

    private void setStatus(String msg, String color) {
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
        statusLabel.setText(msg);
    }
}