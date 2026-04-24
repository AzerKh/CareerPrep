package com.example.test.controller;

import com.example.test.model.Candidature;
import com.example.test.model.OffreEmploi;
import com.example.test.model.Utilisateur;
import com.example.test.service.CandidatureService;
import com.example.test.service.OffreEmploiService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OffresRecruteurController implements Initializable {

    @FXML private VBox       offresListContainer;
    @FXML private Label      messageLabel;
    @FXML private Label      formTitle;
    @FXML private TextField  titreField, entrepriseField;
    @FXML private TextArea   descriptionArea;
    @FXML private ComboBox<OffreEmploi.TypeContrat> typeCombo;
    @FXML private DatePicker dateLimitePicker;
    @FXML private Button     ajouterBtn;

    @FXML private VBox  candidaturesPanel;
    @FXML private Label candidaturesTitleLabel;
    @FXML private VBox  candidaturesListContainer;

    private final OffreEmploiService offreService       = new OffreEmploiService();
    private final CandidatureService candidatureService = new CandidatureService();

    private Utilisateur utilisateur;
    private OffreEmploi offreEnEdition    = null;
    private OffreEmploi offreSelectionnee = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();
        typeCombo.setItems(FXCollections.observableArrayList(OffreEmploi.TypeContrat.values()));
        typeCombo.setValue(OffreEmploi.TypeContrat.CDI);
        candidaturesPanel.setVisible(false);
        candidaturesPanel.setManaged(false);
        chargerOffres();
    }

    @FXML private void handleSauvegarder() {
        messageLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
        try {
            String titre       = titreField.getText().trim();
            String entreprise  = entrepriseField.getText().trim();
            String description = descriptionArea.getText().trim();
            OffreEmploi.TypeContrat type = typeCombo.getValue();
            LocalDate dateLimite = dateLimitePicker.getValue();

            if (offreEnEdition == null) {
                offreService.ajouter(utilisateur, titre, entreprise, description, type, dateLimite);
                succes("Offre publiée !");
            } else {
                offreService.modifier(offreEnEdition, titre, entreprise, description, type, dateLimite);
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

    @FXML private void handleAnnuler() {
        offreEnEdition = null;
        viderFormulaire();
        formTitle.setText("Publier une offre");
        ajouterBtn.setText("Publier l'offre");
        messageLabel.setText("");
    }

    @FXML private void handleFermerCandidatures() {
        candidaturesPanel.setVisible(false);
        candidaturesPanel.setManaged(false);
        offreSelectionnee = null;
    }

    // ── Offers ────────────────────────────────────────────────────────────

    private void chargerOffres() {
        offresListContainer.getChildren().clear();
        List<OffreEmploi> offres = offreService.getOffresUtilisateur(utilisateur.getId());
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
        titre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a1840;");
        HBox.setHgrow(titre, Priority.ALWAYS);
        top.getChildren().addAll(titre, buildBadge(o.getTypeContrat()));

        Label entreprise = new Label("🏢  " + o.getEntreprise());
        entreprise.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        String dateTxt = o.getDateLimite() != null
                ? "📅  Limite : " + o.getDateLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "📅  Pas de date limite";
        Label date = new Label(dateTxt);
        date.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        int count = offreService.compterCandidatures(o.getId());
        Label countLabel = new Label("👥  " + count + " candidature" + (count > 1 ? "s" : ""));
        countLabel.setStyle("-fx-text-fill: #534AB7; -fx-font-size: 12px; -fx-font-weight: bold;");

        card.getChildren().addAll(top, entreprise, date, countLabel);

        HBox actions = new HBox(8);
        actions.setPadding(new Insets(6, 0, 0, 0));

        if (count > 0) {
            Button btnVoir = buildBtn("👁  Voir les candidats (" + count + ")", "#1D9E75");
            btnVoir.setOnAction(e -> afficherCandidatures(o));
            actions.getChildren().add(btnVoir);
        }

        Button btnEdit = buildBtn("Modifier", "#534AB7");
        btnEdit.setOnAction(e -> remplirFormulaire(o));

        Button btnDelete = buildBtn("Supprimer", "#c0392b");
        btnDelete.setOnAction(e -> {
            offreService.supprimer(o.getId());
            candidaturesPanel.setVisible(false);
            candidaturesPanel.setManaged(false);
            chargerOffres();
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().add(actions);
        return card;
    }

    // ── Candidatures panel ────────────────────────────────────────────────

    private void afficherCandidatures(OffreEmploi offre) {
        offreSelectionnee = offre;
        List<Candidature> candidatures = candidatureService.getCandidaturesParOffre(offre.getId());

        candidaturesTitleLabel.setText(
                "Candidats pour : " + offre.getTitre() + "  (" + candidatures.size() + ")");
        candidaturesListContainer.getChildren().clear();

        if (candidatures.isEmpty()) {
            Label empty = new Label("Aucun candidat pour cette offre.");
            empty.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");
            candidaturesListContainer.getChildren().add(empty);
        } else {
            for (Candidature c : candidatures)
                candidaturesListContainer.getChildren().add(buildCandidatureCard(c));
        }

        candidaturesPanel.setVisible(true);
        candidaturesPanel.setManaged(true);
    }

    private VBox buildCandidatureCard(Candidature c) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;" +
                "-fx-border-color: #e0ddf8; -fx-border-radius: 10; -fx-border-width: 1;");

        // Header: avatar + name + email + date
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        // Avatar circle
        StackPane avatar = new StackPane();
        avatar.setPrefSize(42, 42);
        avatar.setStyle("-fx-background-color: #534AB7; -fx-background-radius: 21;");
        String initials = String.valueOf(c.getEtudiantPrenom().charAt(0)).toUpperCase()
                + String.valueOf(c.getEtudiantNom().charAt(0)).toUpperCase();
        Label avatarLbl = new Label(initials);
        avatarLbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        avatar.getChildren().add(avatarLbl);

        VBox nameBlock = new VBox(4);
        HBox.setHgrow(nameBlock, Priority.ALWAYS);

        // ✅ Nom en noir foncé bien visible
        Label name = new Label(c.getNomComplet());
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1840;");

        // ✅ Email en gris foncé lisible
        Label email = new Label("✉  " + c.getEtudiantEmail());
        email.setStyle("-fx-font-size: 12px; -fx-text-fill: #444444;");

        nameBlock.getChildren().addAll(name, email);

        Label dateLabel = new Label("📅 " + c.getDateCandidature()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        header.getChildren().addAll(avatar, nameBlock, dateLabel);

        // CV mini preview
        VBox cvPreview = new VBox(6);
        cvPreview.setStyle("-fx-background-color: #f9f8fe; -fx-background-radius: 8;" +
                "-fx-padding: 10 12; -fx-border-color: #e8e6f8;" +
                "-fx-border-radius: 8; -fx-border-width: 1;");

        Label cvTitre = new Label("📄  " + (c.getCvTitre() != null ? c.getCvTitre() : "CV sans titre"));
        cvTitre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #534AB7;");
        cvPreview.getChildren().add(cvTitre);

        if (c.getCvFormation() != null && !c.getCvFormation().isBlank()) {
            Label formation = new Label("🎓  " + truncate(c.getCvFormation(), 70));
            formation.setStyle("-fx-text-fill: #444444; -fx-font-size: 12px;");
            cvPreview.getChildren().add(formation);
        }

        if (c.getCvCompetences() != null && !c.getCvCompetences().isBlank()) {
            HBox skills = new HBox(6);
            skills.setAlignment(Pos.CENTER_LEFT);
            int cnt = 0;
            for (String skill : c.getCvCompetences().split(",")) {
                String s = skill.trim();
                if (!s.isEmpty() && cnt < 4) {
                    Label badge = new Label(s);
                    badge.setStyle("-fx-background-color: #EEEDFE; -fx-text-fill: #3C3489;" +
                            "-fx-background-radius: 20; -fx-padding: 2 8;" +
                            "-fx-font-size: 10px; -fx-font-weight: bold;");
                    skills.getChildren().add(badge);
                    cnt++;
                }
            }
            cvPreview.getChildren().add(skills);
        }

        // Action buttons
        HBox actions = new HBox(8);
        actions.setPadding(new Insets(4, 0, 0, 0));

        Button btnVoirCV = buildBtn("📄  Voir le CV complet", "#534AB7");
        btnVoirCV.setOnAction(e -> ouvrirCVDetail(c));

        Button btnRefuser = buildBtn("✕  Retirer", "#c0392b");
        btnRefuser.setOnAction(e -> {
            candidatureService.supprimerCandidature(c.getId());
            chargerOffres();
            if (offreSelectionnee != null) afficherCandidatures(offreSelectionnee);
        });

        actions.getChildren().addAll(btnVoirCV, btnRefuser);
        card.getChildren().addAll(header, cvPreview, actions);
        return card;
    }

    // ── CV popup ──────────────────────────────────────────────────────────

    private void ouvrirCVDetail(Candidature c) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/test/CVDetail.fxml"));
            Parent root = loader.load();

            CVDetailController ctrl = loader.getController();
            ctrl.setCandidature(c);

            Stage popup = new Stage();
            popup.setTitle("CV de " + c.getNomComplet());
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(new Scene(root, 620, 680));
            popup.setResizable(false);
            popup.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

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
        titreField.clear();
        entrepriseField.clear();
        descriptionArea.clear();
        dateLimitePicker.setValue(null);
        typeCombo.setValue(OffreEmploi.TypeContrat.CDI);
    }

    private Label buildBadge(OffreEmploi.TypeContrat type) {
        Label b = new Label(type.name());
        String color = switch (type) {
            case CDI -> "#1D9E75"; case CDD -> "#534AB7"; case STAGE -> "#E49B0F";
        };
        b.setStyle("-fx-background-color: " + color + "22; -fx-text-fill: " + color + ";" +
                "-fx-background-radius: 20; -fx-padding: 3 10;" +
                "-fx-font-size: 11px; -fx-font-weight: bold;");
        return b;
    }

    private Button buildBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + color + ";" +
                "-fx-text-fill: " + color + "; -fx-border-radius: 5;" +
                "-fx-background-radius: 5; -fx-padding: 5 12;" +
                "-fx-font-size: 11px; -fx-cursor: hand;");
        return btn;
    }

    private void succes(String msg) {
        messageLabel.setStyle("-fx-text-fill: #1D9E75; -fx-font-size: 12px;");
        messageLabel.setText(msg);
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}