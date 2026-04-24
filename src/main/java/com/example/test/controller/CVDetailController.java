package com.example.test.controller;

import com.example.test.model.Candidature;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CVDetailController implements Initializable {

    @FXML private ImageView photoView;
    @FXML private Label     nomCompletLabel;
    @FXML private Label     emailLabel;
    @FXML private Label     cvTitreLabel;
    @FXML private Label     formationLabel;
    @FXML private Label     experiencesLabel;
    @FXML private Label     languesLabel;
    @FXML private FlowPane  competencesBadges;
    @FXML private Label     dateCandidatureLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setCandidature(Candidature c) {
        nomCompletLabel.setText(c.getNomComplet());
        emailLabel.setText(c.getEtudiantEmail());
        cvTitreLabel.setText(c.getCvTitre() != null ? c.getCvTitre() : "CV sans titre");
        dateCandidatureLabel.setText("Candidature reçue le " +
                c.getDateCandidature().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        formationLabel.setText(orEmpty(c.getCvFormation()));
        experiencesLabel.setText(orEmpty(c.getCvExperiences()));
        languesLabel.setText(orEmpty(c.getCvLangues()));

        // Competences badges
        competencesBadges.getChildren().clear();
        if (c.getCvCompetences() != null && !c.getCvCompetences().isBlank()) {
            for (String skill : c.getCvCompetences().split(",")) {
                String s = skill.trim();
                if (!s.isEmpty()) {
                    Label badge = new Label(s);
                    badge.setStyle(
                            "-fx-background-color: #EEEDFE; -fx-text-fill: #3C3489;" +
                                    "-fx-background-radius: 20; -fx-padding: 4 12;" +
                                    "-fx-font-size: 12px; -fx-font-weight: bold;");
                    competencesBadges.getChildren().add(badge);
                }
            }
        } else {
            Label none = new Label("Non renseignées");
            none.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");
            competencesBadges.getChildren().add(none);
        }

        // Photo
        if (c.getCvPhotoPath() != null && !c.getCvPhotoPath().isBlank()
                && Files.exists(Paths.get(c.getCvPhotoPath()))) {
            try {
                photoView.setImage(new Image("file:" + c.getCvPhotoPath()));
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleFermer() {
        ((Stage) nomCompletLabel.getScene().getWindow()).close();
    }

    private String orEmpty(String s) {
        return (s != null && !s.isBlank()) ? s : "Non renseigné";
    }
}