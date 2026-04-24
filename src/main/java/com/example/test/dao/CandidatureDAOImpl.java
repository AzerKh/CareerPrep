package com.example.test.dao;

import com.example.test.config.DatabaseConnection;
import com.example.test.model.Candidature;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatureDAOImpl implements CandidatureDAO {

    @Override
    public List<Candidature> findByOffreId(int offreId) {
        String sql = """
            SELECT
                c.id,
                c.offre_id,
                c.cv_id,
                c.etudiant_id,
                c.date_candidature,
                u.nom           AS etudiant_nom,
                u.prenom        AS etudiant_prenom,
                u.email         AS etudiant_email,
                cv.titre        AS cv_titre,
                cv.competences  AS cv_competences,
                cv.formation    AS cv_formation,
                cv.experiences  AS cv_experiences,
                cv.langues      AS cv_langues,
                cv.photo_path   AS cv_photo_path
            FROM candidatures c
            JOIN utilisateurs u ON c.etudiant_id = u.id
            JOIN cvs cv         ON c.cv_id       = cv.id
            WHERE c.offre_id = ?
            ORDER BY c.date_candidature DESC
            """;

        List<Candidature> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offreId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Candidature cand = new Candidature();
                cand.setId(rs.getInt("id"));
                cand.setOffreId(rs.getInt("offre_id"));
                cand.setCvId(rs.getInt("cv_id"));
                cand.setEtudiantId(rs.getInt("etudiant_id"));
                cand.setDateCandidature(rs.getDate("date_candidature").toLocalDate());
                cand.setEtudiantNom(rs.getString("etudiant_nom"));
                cand.setEtudiantPrenom(rs.getString("etudiant_prenom"));
                cand.setEtudiantEmail(rs.getString("etudiant_email"));
                cand.setCvTitre(rs.getString("cv_titre"));
                cand.setCvCompetences(rs.getString("cv_competences"));
                cand.setCvFormation(rs.getString("cv_formation"));
                cand.setCvExperiences(rs.getString("cv_experiences"));
                cand.setCvLangues(rs.getString("cv_langues"));
                cand.setCvPhotoPath(rs.getString("cv_photo_path"));
                list.add(cand);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByOffreId : " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void delete(int candidatureId) {
        String sql = "DELETE FROM candidatures WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidatureId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete candidature : " + e.getMessage(), e);
        }
    }
}