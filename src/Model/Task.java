package Model;

import config.DatabaseConnection;
import Model.Enums.TaskPriority;
import Model.Enums.TaskStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class Task {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private String projectId;
    private String assignedTo;
    private String createdBy;
    private LocalDate createdAt;


    public Task(String title, TaskStatus status, TaskPriority priority, LocalDate dueDate,
                String projectId, String assignedTo, String createdBy) {
        this.title = title;
        this.description = null;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.createdAt = LocalDate.now();
        this.id = generateNextId();
    }


    public Task(String id, String title, TaskStatus status, TaskPriority priority, LocalDate dueDate,
                String projectId, String assignedTo, String createdBy, LocalDate createdAt) {
        this.id = id;
        this.title = title;
        this.description = null;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    private String generateNextId() {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("T%02d", count + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("T%02d", System.currentTimeMillis() % 1000);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public String getProjectId() { return projectId; }
    public String getAssignedTo() { return assignedTo; }
    public String getCreatedBy() { return createdBy; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setStatus(TaskStatus status) { this.status = status; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    @Override
    public String toString() {
        return "Task [" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", dueDate=" + dueDate +
                ", projectId='" + projectId + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                "]\n";
    }
}
