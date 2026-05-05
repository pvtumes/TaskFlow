package Repository;

import config.DatabaseConnection;
import Model.ProjectMember;
import Model.Enums.UserRole;

import java.sql.*;
import java.util.*;

public class ProjectMemberRepository {

    public boolean save(ProjectMember projectMember) {
        String sql = "INSERT INTO project_members (project_id, user_id, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectMember.getProjectId());
            ps.setString(2, projectMember.getUserId());
            ps.setString(3, projectMember.getRole().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<ProjectMember> findByProjectId(String projectId) {
        String sql = "SELECT * FROM project_members WHERE project_id = ?";
        List<ProjectMember> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public List<ProjectMember> findByUserId(String userId) {
        String sql = "SELECT * FROM project_members WHERE user_id = ?";
        List<ProjectMember> result = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public boolean exists(String projectId, String userId) {
        String sql = "SELECT 1 FROM project_members WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ProjectMember mapRow(ResultSet rs) throws SQLException {
        return new ProjectMember(
                rs.getString("project_id"),
                rs.getString("user_id"),
                UserRole.valueOf(rs.getString("role").toUpperCase())
        );
    }
}
