package com.example.test.service;

import com.example.test.model.CV;
import com.example.test.model.Utilisateur;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GeminiService {

    private static final String API_KEY  = "AIzaSyDAMEHz5ixJIEtIIFggUz8IgUymjEc7U3Y"; // ← remplacez ici
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public String ameliorerCV(CV cv, Utilisateur utilisateur) {
        String prompt = """
            Tu es un expert en rédaction de CV professionnels.
            Voici le CV de %s %s :
            Titre : %s
            Formation : %s
            Expériences : %s
            Compétences : %s
            Langues : %s
            
            Améliore ce CV en :
            1. Rendant les descriptions plus percutantes et professionnelles
            2. Utilisant des verbes d'action forts
            3. Quantifiant les réalisations si possible
            4. Structurant mieux le contenu
            
            Réponds UNIQUEMENT avec le contenu amélioré du CV, sans commentaires ni explications.
            Réponds en français.
            """.formatted(
                utilisateur.getPrenom(), utilisateur.getNom(),
                orEmpty(cv.getTitre()), orEmpty(cv.getFormation()),
                orEmpty(cv.getExperiences()), orEmpty(cv.getCompetences()),
                orEmpty(cv.getLangues())
        );
        return callGemini(prompt);
    }

    public String suggererCompetences(CV cv) {
        String prompt = """
            Tu es un expert en recrutement tech et digital.
            Formation : %s
            Expériences : %s
            Compétences actuelles : %s
            
            Suggère 8 à 10 compétences manquantes pertinentes pour ce profil.
            Pour chaque compétence, donne une courte explication (1 ligne).
            Réponds UNIQUEMENT avec la liste numérotée, en français.
            """.formatted(
                orEmpty(cv.getFormation()),
                orEmpty(cv.getExperiences()),
                orEmpty(cv.getCompetences())
        );
        return callGemini(prompt);
    }

    public String simulerEntretien(CV cv, String poste) {
        String prompt = """
            Tu es un recruteur expérimenté.
            Poste visé : %s
            Formation : %s
            Expériences : %s
            Compétences : %s
            
            Génère 8 questions d'entretien variées :
            - 2 sur la motivation
            - 3 techniques
            - 2 comportementales (méthode STAR)
            - 1 sur les objectifs de carrière
            
            Pour chaque question, ajoute un conseil de réponse.
            Réponds UNIQUEMENT avec les questions, en français.
            """.formatted(
                poste.isBlank() ? "un poste adapté au profil" : poste,
                orEmpty(cv.getFormation()),
                orEmpty(cv.getExperiences()),
                orEmpty(cv.getCompetences())
        );
        return callGemini(prompt);
    }

    // ── Core API call ─────────────────────────────────────────────────────

    private String callGemini(String prompt) {
        try {
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);

            String body = "{\"contents\":[{\"parts\":[{\"text\":" + toJsonString(prompt) + "}]}]," +
                    "\"generationConfig\":{\"temperature\":1,\"maxOutputTokens\":2048}}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                Scanner err = new Scanner(conn.getErrorStream(), StandardCharsets.UTF_8);
                return "Erreur API (" + status + ") : " + err.useDelimiter("\\A").next();
            }

            Scanner sc = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            return extractText(sc.useDelimiter("\\A").next());

        } catch (Exception e) {
            return "Erreur de connexion : " + e.getMessage();
        }
    }

    /**
     * Extracts ONLY the text content from Gemini's JSON response.
     * Looks for "parts":[{"text":"..."  and grabs just that value.
     */
    private String extractText(String json) {
        try {
            // Find "parts" array then the first "text" field inside it
            int partsIdx = json.indexOf("\"parts\"");
            if (partsIdx == -1) return "Réponse vide.";

            int textIdx = json.indexOf("\"text\"", partsIdx);
            if (textIdx == -1) return "Réponse vide.";

            // Skip: "text":  then opening quote
            int start = json.indexOf("\"", textIdx + 7) + 1;

            // Find closing quote (not escaped)
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }

            String raw = json.substring(start, end);
            return raw.replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\/", "/")
                    .replace("\\\\", "\\");

        } catch (Exception e) {
            return "Impossible de lire la réponse.";
        }
    }

    private String toJsonString(String text) {
        return "\"" + text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t") + "\"";
    }

    private String orEmpty(String s) {
        return s != null ? s : "Non renseigné";
    }
}