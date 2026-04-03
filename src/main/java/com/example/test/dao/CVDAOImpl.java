package com.example.test.dao;

import com.example.test.config.DatabaseConnection;
import com.example.test.model.CV;

import java.sql.*;
import java.util.Optional;

public class CVDAOImpl implements CVDAO {

    @Override
    public void create(CV cv) {
        String sql = "INSERT INTO cvs (utilisateur_id, titre, formation, competences, experiences, langues, photo_path, date_creation, date_modification) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cv.getUtilisateurId());
            ps.setString(2, cv.getTitre());
            ps.setString(3, cv.getFormation());
            ps.setString(4, cv.getCompetences());
            ps.setString(5, cv.getExperiences());
            ps.setString(6, cv.getLangues());
            ps.setString(7, cv.getPhotoPath());
            ps.setDate(8, Date.valueOf(cv.getDateCreation()));
            ps.setDate(9, Date.valueOf(cv.getDateModification()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) cv.setId(keys.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création CV : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CV> findByUtilisateurId(int utilisateurId) {
        String sql = "SELECT * FROM cvs WHERE utilisateur_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByUtilisateurId : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void update(CV cv) {
        String sql = "UPDATE cvs SET titre=?, formation=?, competences=?, experiences=?, langues=?, photo_path=?, date_modification=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cv.getTitre());
            ps.setString(2, cv.getFormation());
            ps.setString(3, cv.getCompetences());
            ps.setString(4, cv.getExperiences());
            ps.setString(5, cv.getLangues());
            ps.setString(6, cv.getPhotoPath());
            ps.setDate(7, Date.valueOf(java.time.LocalDate.now()));
            ps.setInt(8, cv.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update CV : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM cvs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete CV : " + e.getMessage(), e);
        }
    }

    private CV map(ResultSet rs) throws SQLException {
        CV cv = new CV();
        cv.setId(rs.getInt("id"));
        cv.setUtilisateurId(rs.getInt("utilisateur_id"));
        cv.setTitre(rs.getString("titre"));
        cv.setFormation(rs.getString("formation"));
        cv.setCompetences(rs.getString("competences"));
        cv.setExperiences(rs.getString("experiences"));
        cv.setLangues(rs.getString("langues"));
        cv.setPhotoPath(rs.getString("photo_path"));
        cv.setDateCreation(rs.getDate("date_creation").toLocalDate());
        cv.setDateModification(rs.getDate("date_modification").toLocalDate());
        return cv;
    }
}