package com.example.test.dao;

import com.example.test.config.DatabaseConnection;
import com.example.test.model.OffreEmploi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OffreEmploiDAOImpl implements OffreEmploiDAO {

    @Override
    public void create(OffreEmploi o) {
        String sql = "INSERT INTO offres_emploi (utilisateur_id, titre, entreprise, description, type_contrat, date_limite, date_ajout) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getUtilisateurId());
            ps.setString(2, o.getTitre());
            ps.setString(3, o.getEntreprise());
            ps.setString(4, o.getDescription());
            ps.setString(5, o.getTypeContrat().name());
            ps.setDate(6, o.getDateLimite() != null ? Date.valueOf(o.getDateLimite()) : null);
            ps.setDate(7, Date.valueOf(o.getDateAjout()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) o.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création offre : " + e.getMessage(), e);
        }
    }

    @Override
    public List<OffreEmploi> findByUtilisateurId(int utilisateurId) {
        String sql = "SELECT * FROM offres_emploi WHERE utilisateur_id = ? ORDER BY date_ajout DESC";
        List<OffreEmploi> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByUtilisateurId : " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<OffreEmploi> findAll() {
        String sql = "SELECT * FROM offres_emploi ORDER BY date_ajout DESC";
        List<OffreEmploi> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll : " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Optional<OffreEmploi> findById(int id) {
        String sql = "SELECT * FROM offres_emploi WHERE id = ?";
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
    public void update(OffreEmploi o) {
        String sql = "UPDATE offres_emploi SET titre=?, entreprise=?, description=?, type_contrat=?, date_limite=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, o.getTitre());
            ps.setString(2, o.getEntreprise());
            ps.setString(3, o.getDescription());
            ps.setString(4, o.getTypeContrat().name());
            ps.setDate(5, o.getDateLimite() != null ? Date.valueOf(o.getDateLimite()) : null);
            ps.setInt(6, o.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM offres_emploi WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete : " + e.getMessage(), e);
        }
    }

    @Override
    public void associerCV(int offreId, int cvId, int etudiantId) {
        String sql = "INSERT IGNORE INTO candidatures (offre_id, cv_id, etudiant_id, date_candidature) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offreId);
            ps.setInt(2, cvId);
            ps.setInt(3, etudiantId);
            ps.setDate(4, Date.valueOf(java.time.LocalDate.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur associerCV : " + e.getMessage(), e);
        }
    }

    @Override
    public void dissocierCV(int offreId, int etudiantId) {
        String sql = "DELETE FROM candidatures WHERE offre_id = ? AND etudiant_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offreId);
            ps.setInt(2, etudiantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur dissocierCV : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean aDejaPostule(int offreId, int etudiantId) {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE offre_id = ? AND etudiant_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offreId);
            ps.setInt(2, etudiantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur aDejaPostule : " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public int compterCandidatures(int offreId) {
        String sql = "SELECT COUNT(*) FROM candidatures WHERE offre_id = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offreId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur compterCandidatures : " + e.getMessage(), e);
        }
        return 0;
    }

    private OffreEmploi map(ResultSet rs) throws SQLException {
        OffreEmploi o = new OffreEmploi();
        o.setId(rs.getInt("id"));
        o.setUtilisateurId(rs.getInt("utilisateur_id"));
        o.setTitre(rs.getString("titre"));
        o.setEntreprise(rs.getString("entreprise"));
        o.setDescription(rs.getString("description"));
        o.setTypeContrat(OffreEmploi.TypeContrat.valueOf(rs.getString("type_contrat")));
        Date dl = rs.getDate("date_limite");
        if (dl != null) o.setDateLimite(dl.toLocalDate());
        o.setDateAjout(rs.getDate("date_ajout").toLocalDate());
        return o;
    }
}