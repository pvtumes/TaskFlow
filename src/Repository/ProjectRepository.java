package Repository;

import config.DatabaseConnection;
import Model.Project;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepository {

    public boolean save(Project project) {
        String sql = "INSERT INTO projects (id, name, created_by, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, project.getId());
            ps.setString(2, project.getName());
            ps.setString(3, project.getCreatedBy());
            ps.setDate(4, Date.valueOf(project.getCreatedAt()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Project> findAll() {
        String sql = "SELECT * FROM projects";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                projects.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public Optional<Project> findById(String id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean deleteById(String id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Project mapRow(ResultSet rs) throws SQLException {
        return new Project(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("created_by"),
                rs.getDate("created_at").toLocalDate()
        );
    }
}
