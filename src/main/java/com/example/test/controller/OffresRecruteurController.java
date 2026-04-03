package com.example.test.controller;

import com.example.test.model.OffreEmploi;
import com.example.test.model.Utilisateur;
import com.example.test.service.OffreEmploiService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OffresRecruteurController implements Initializable {

    @FXML private VBox      offresListContainer;
    @FXML private Label     messageLabel, formTitle;
    @FXML private TextField titreField, entrepriseField;
    @FXML private TextArea  descriptionArea;
    @FXML private ComboBox<OffreEmploi.TypeContrat> typeCombo;
    @FXML private DatePicker dateLimitePicker;
    @FXML private Button    ajouterBtn;

    private final OffreEmploiService service = new OffreEmploiService();
    private Utilisateur utilisateur;
    private OffreEmploi offreEnEdition = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();
        typeCombo.setItems(FXCollections.observableArrayList(OffreEmploi.TypeContrat.values()));
        typeCombo.setValue(OffreEmploi.TypeContrat.CDI);
        chargerOffres();
    }

    @FXML
    private void handleSauvegarder() {
        messageLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
        try {
            String titre       = titreField.getText().trim();
            String entreprise  = entrepriseField.getText().trim();
            String description = descriptionArea.getText().trim();
            OffreEmploi.TypeContrat type = typeCombo.getValue();
            LocalDate dateLimite = dateLimitePicker.getValue();

            if (offreEnEdition == null) {
                service.ajouter(utilisateur, titre, entreprise, description, type, dateLimite);
                succes("Offre publiée !");
            } else {
                service.modifier(offreEnEdition, titre, entreprise, description, type, dateLimite);
                offreEnEdition = null;
                formTitle.setText("Publier une offre");
                ajouterBtn.setText("Publier l'offre");
                succes("Offre modifiée !");
            }
            viderFormulaire();
            chargerOffres();
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        offreEnEdition = null;
        viderFormulaire();
        formTitle.setText("Publier une offre");
        ajouterBtn.setText("Publier l'offre");
        messageLabel.setText("");
    }

    private void chargerOffres() {
        offresListContainer.getChildren().clear();
        List<OffreEmploi> offres = service.getOffresUtilisateur(utilisateur.getId());

        if (offres.isEmpty()) {
            Label empty = new Label("Vous n'avez pas encore publié d'offres.");
            empty.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");
            empty.setPadding(new Insets(20, 0, 0, 0));
            offresListContainer.getChildren().add(empty);
            return;
        }
        for (OffreEmploi o : offres)
            offresListContainer.getChildren().add(buildCard(o));
    }

    private VBox buildCard(OffreEmploi o) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;" +
                "-fx-border-color: #e8e6f8; -fx-border-radius: 10; -fx-border-width: 1;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label titre = new Label(o.getTitre());
        titre.setFont(Font.font("System", FontWeight.BOLD, 15));
        titre.setTextFill(Color.web("#2c2c2c"));
        HBox.setHgrow(titre, Priority.ALWAYS);
        top.getChildren().addAll(titre, buildBadge(o.getTypeContrat()));

        Label entreprise = new Label("🏢  " + o.getEntreprise());
        entreprise.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        String dateTxt = o.getDateLimite() != null
                ? "📅  Limite : " + o.getDateLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "📅  Pas de date limite";
        Label date = new Label(dateTxt);
        date.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Candidatures count
        Label candidatures = new Label("👥  " + service.compterCandidatures(o.getId()) + " candidature(s)");
        candidatures.setStyle("-fx-text-fill: #534AB7; -fx-font-size: 12px;");

        card.getChildren().addAll(top, entreprise, date, candidatures);

        HBox actions = new HBox(8);
        actions.setPadding(new Insets(6, 0, 0, 0));

        Button btnEdit = buildBtn("Modifier", "#534AB7");
        btnEdit.setOnAction(e -> remplirFormulaire(o));

        Button btnDelete = buildBtn("Supprimer", "#c0392b");
        btnDelete.setOnAction(e -> {
            service.supprimer(o.getId());
            chargerOffres();
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().add(actions);
        return card;
    }

    private void remplirFormulaire(OffreEmploi o) {
        offreEnEdition = o;
        titreField.setText(o.getTitre());
        entrepriseField.setText(o.getEntreprise());
        descriptionArea.setText(o.getDescription() != null ? o.getDescription() : "");
        typeCombo.setValue(o.getTypeContrat());
        dateLimitePicker.setValue(o.getDateLimite());
        formTitle.setText("Modifier l'offre");
        ajouterBtn.setText("Enregistrer");
    }

    private void viderFormulaire() {
        titreField.clear(); entrepriseField.clear();
        descriptionArea.clear(); dateLimitePicker.setValue(null);
        typeCombo.setValue(OffreEmploi.TypeContrat.CDI);
    }

    private Label buildBadge(OffreEmploi.TypeContrat type) {
        Label b = new Label(type.name());
        String color = switch (type) {
            case CDI -> "#1D9E75"; case CDD -> "#534AB7"; case STAGE -> "#E49B0F";
        };
        b.setStyle("-fx-background-color: " + color + "22; -fx-text-fill: " + color + ";" +
                "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;");
        return b;
    }

    private Button buildBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + color + ";" +
                "-fx-text-fill: " + color + "; -fx-border-radius: 5; -fx-background-radius: 5;" +
                "-fx-padding: 5 12; -fx-font-size: 11px; -fx-cursor: hand;");
        return btn;
    }

    private void succes(String msg) {
        messageLabel.setStyle("-fx-text-fill: #1D9E75; -fx-font-size: 12px;");
        messageLabel.setText(msg);
    }
}