package growmate.dao;

import growmate.db.DB;
import growmate.model.Plant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlantDAO {

    private Plant map(ResultSet rs) throws SQLException {
        Plant p = new Plant();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setName(rs.getString("name"));
        p.setSpecies(rs.getString("species"));
        p.setWaterIntervalDays(rs.getInt("water_interval_days"));
        p.setFertilizeIntervalDays(rs.getInt("fertilize_interval_days"));
        Date lw = rs.getDate("last_watered");
        Date lf = rs.getDate("last_fertilized");
        p.setLastWatered(lw==null?null:lw.toLocalDate());
        p.setLastFertilized(lf==null?null:lf.toLocalDate());
        p.setNotes(rs.getString("notes"));
        return p;
    }

    public List<Plant> listByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM plants WHERE user_id=? ORDER BY id DESC";
        List<Plant> list = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Plant create(Plant p) throws SQLException {
        String sql = "INSERT INTO plants (user_id, name, species, water_interval_days, fertilize_interval_days, last_watered, last_fertilized, notes) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getSpecies());
            ps.setInt(4, p.getWaterIntervalDays());
            ps.setInt(5, p.getFertilizeIntervalDays());
            ps.setDate(6, p.getLastWatered()==null?null:java.sql.Date.valueOf(p.getLastWatered()));
            ps.setDate(7, p.getLastFertilized()==null?null:java.sql.Date.valueOf(p.getLastFertilized()));
            ps.setString(8, p.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
        return p;
    }

    public void updateCareDates(int plantId, java.time.LocalDate lastWatered, java.time.LocalDate lastFertilized, int userId) throws SQLException {
        String sql = "UPDATE plants SET last_watered=?, last_fertilized=? WHERE id=? AND user_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, lastWatered==null?null:java.sql.Date.valueOf(lastWatered));
            ps.setDate(2, lastFertilized==null?null:java.sql.Date.valueOf(lastFertilized));
            ps.setInt(3, plantId);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id, int userId) throws SQLException {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM plants WHERE id=? AND user_id=?")) {
            ps.setInt(1, id); ps.setInt(2, userId); ps.executeUpdate();
        }
    }

    // NEW: update the custom reminder/notes
    public void updateNotes(int plantId, String notes, int userId) throws SQLException {
        String sql = "UPDATE plants SET notes=? WHERE id=? AND user_id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setInt(2, plantId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }
}
