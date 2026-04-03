package com.example.test.dao;

import com.example.test.config.DatabaseConnection;
import com.example.test.model.Utilisateur;

import java.sql.*;
import java.util.Optional;

public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Override
    public void create(Utilisateur u) {
        String sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe_hash, formation, competences, experiences, date_inscription, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasseHash());
            ps.setString(5, u.getFormation());
            ps.setString(6, u.getCompetences());
            ps.setString(7, u.getExperiences());
            ps.setDate(8, Date.valueOf(u.getDateInscription()));
            ps.setString(9, u.getRole().name());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) u.setId(keys.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilisateur> findById(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Utilisateur u) {
        String sql = "UPDATE utilisateurs SET nom=?, prenom=?, email=?, formation=?, competences=?, experiences=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getFormation());
            ps.setString(5, u.getCompetences());
            ps.setString(6, u.getExperiences());
            ps.setInt(7, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur emailExists : " + e.getMessage(), e);
        }
        return false;
    }

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasseHash(rs.getString("mot_de_passe_hash"));
        u.setFormation(rs.getString("formation"));
        u.setCompetences(rs.getString("competences"));
        u.setExperiences(rs.getString("experiences"));
        u.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        u.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
        return u;
    }
}