package Repository;

import config.DatabaseConnection;
import Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {


    public boolean save(User user) {

        String sql = "INSERT INTO users (id, name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Optional<User> findById(String id) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public Optional<User> findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public List<User> findAll() {

        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    public boolean deleteById(String id) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}