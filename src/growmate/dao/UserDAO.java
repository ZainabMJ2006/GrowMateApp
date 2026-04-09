
package growmate.dao;

import growmate.db.DB;
import growmate.model.User;

import java.security.MessageDigest;
import java.sql.*;

public class UserDAO {

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b: h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return s; }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"),
                                    rs.getString("username"),
                                    rs.getString("password_hash"));
                }
                return null;
            }
        }
    }

    public boolean exists(String username) throws SQLException {
        return findByUsername(username) != null;
    }

    public User create(String username, String rawPassword) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?,?)";
        String hash = sha256(rawPassword);
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username.toLowerCase());
            ps.setString(2, hash);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return new User(rs.getInt(1), username.toLowerCase(), hash);
            }
        }
        return null;
    }

    public boolean authenticate(String username, String rawPassword) throws SQLException {
        User u = findByUsername(username);
        return u != null && u.getPasswordHash().equals(sha256(rawPassword));
    }
}
