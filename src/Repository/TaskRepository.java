package Repository;

import config.DatabaseConnection;
import Model.Task;
import Model.Enums.TaskPriority;
import Model.Enums.TaskStatus;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepository {

    public boolean save(Task task) {

        String sql = """
                INSERT INTO tasks (id, title, description, status, priority, due_date, project_id, assigned_to, created_by, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                  SET status      = EXCLUDED.status,
                      priority    = EXCLUDED.priority,
                      assigned_to = EXCLUDED.assigned_to
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus().name());
            ps.setString(5, task.getPriority().name());
            ps.setDate(6, task.getDueDate() != null ? Date.valueOf(task.getDueDate()) : null);
            ps.setString(7, task.getProjectId());
            ps.setString(8, task.getAssignedTo());
            ps.setString(9, task.getCreatedBy());
            ps.setDate(10, Date.valueOf(task.getCreatedAt()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<Task> findById(String taskId) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, taskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Task> findAll() {
        String sql = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tasks.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> findByProjectId(String projectId) {
        String sql = "SELECT * FROM tasks WHERE project_id = ?";
        List<Task> result = new ArrayList<>();
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

    public List<Task> findByAssignedUser(String userId) {
        String sql = "SELECT * FROM tasks WHERE assigned_to = ?";
        List<Task> result = new ArrayList<>();
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

    public boolean deleteById(String taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, taskId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Date due = rs.getDate("due_date");
        return new Task(
                rs.getString("id"),
                rs.getString("title"),
                TaskStatus.valueOf(rs.getString("status").toUpperCase()),
                TaskPriority.valueOf(rs.getString("priority").toUpperCase()),
                due != null ? due.toLocalDate() : null,
                rs.getString("project_id"),
                rs.getString("assigned_to"),
                rs.getString("created_by"),
                rs.getDate("created_at").toLocalDate()
        );
    }
}