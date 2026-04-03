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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OffresEtudiantController implements Initializable {

    @FXML private VBox      offresListContainer;
    @FXML private Label     messageLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;

    private final OffreEmploiService service = new OffreEmploiService();
    private Utilisateur utilisateur;
    private List<OffreEmploi> toutesLesOffres;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateur = SessionManager.getUtilisateurConnecte();
        filterType.setItems(FXCollections.observableArrayList("Tous", "CDI", "CDD", "STAGE"));
        filterType.setValue("Tous");
        chargerOffres();
    }

    @FXML
    private void handleRechercher() {
        String keyword = searchField.getText().trim().toLowerCase();
        String type    = filterType.getValue();

        List<OffreEmploi> filtrees = toutesLesOffres.stream()
                .filter(o -> {
                    boolean matchKeyword = keyword.isEmpty()
                            || o.getTitre().toLowerCase().contains(keyword)
                            || o.getEntreprise().toLowerCase().contains(keyword);
                    boolean matchType = "Tous".equals(type)
                            || o.getTypeContrat().name().equals(type);
                    return matchKeyword && matchType;
                })
                .collect(Collectors.toList());

        afficherOffres(filtrees);

        if (filtrees.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
            messageLabel.setText("Aucune offre trouvée pour cette recherche.");
        } else {
            messageLabel.setText(filtrees.size() + " offre(s) trouvée(s).");
            messageLabel.setStyle("-fx-text-fill: #534AB7; -fx-font-size: 12px;");
        }
    }

    @FXML
    private void handleToutAfficher() {
        searchField.clear();
        filterType.setValue("Tous");
        messageLabel.setText("");
        afficherOffres(toutesLesOffres);
    }

    private void chargerOffres() {
        // Load ALL offers from ALL recruiters
        toutesLesOffres = service.getAllOffres();
        afficherOffres(toutesLesOffres);
    }

    private void afficherOffres(List<OffreEmploi> offres) {
        offresListContainer.getChildren().clear();

        if (offres.isEmpty()) {
            Label empty = new Label("Aucune offre disponible pour le moment.");
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

        if (o.getDescription() != null && !o.getDescription().isBlank()) {
            String preview = o.getDescription().length() > 150
                    ? o.getDescription().substring(0, 150) + "..." : o.getDescription();
            Label desc = new Label(preview);
            desc.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
            desc.setWrapText(true);
            card.getChildren().addAll(top, entreprise, date, desc);
        } else {
            card.getChildren().addAll(top, entreprise, date);
        }

        // Postuler button
        boolean dejaPostule = service.aDejaPostule(o.getId(), utilisateur.getId());
        Button btnPostuler = dejaPostule
                ? buildBtn("✅  Candidature envoyée", "#1D9E75")
                : buildBtn("Postuler avec mon CV", "#534AB7");

        if (!dejaPostule) {
            btnPostuler.setOnAction(e -> {
                try {
                    service.associerCV(o.getId(), utilisateur.getId());
                    chargerOffres();
                    messageLabel.setStyle("-fx-text-fill: #1D9E75; -fx-font-size: 12px;");
                    messageLabel.setText("Candidature envoyée pour : " + o.getTitre());
                } catch (IllegalStateException ex) {
                    messageLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 12px;");
                    messageLabel.setText(ex.getMessage());
                }
            });
        } else {
            // Allow withdrawing application
            btnPostuler.setOnAction(e -> {
                service.dissocierCV(o.getId(), utilisateur.getId());
                chargerOffres();
                messageLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
                messageLabel.setText("Candidature retirée.");
            });
        }

        HBox actions = new HBox(8);
        actions.setPadding(new Insets(8, 0, 0, 0));
        actions.getChildren().add(btnPostuler);
        card.getChildren().add(actions);
        return card;
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
                "-fx-padding: 6 14; -fx-font-size: 12px; -fx-cursor: hand;");
        return btn;
    }
}