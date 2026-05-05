package Model;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class Project {
    private String id;
    private String name;
    private String createdBy;
    private LocalDate createdAt;


    public Project(String name, User currentUser) {
        this.name = name;
        this.createdBy = currentUser.getId();
        this.createdAt = LocalDate.now();
        this.id = generateNextId();
    }


    public Project(String id, String name, String createdBy, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    private String generateNextId() {
        String sql = "SELECT COUNT(*) FROM projects";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("P%02d", count + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("P%02d", System.currentTimeMillis() % 1000);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Project Id:" + id + "\n" +
                "Project Name:" + name + "\n" +
                "Created By:" + createdBy + "\n" +
                "Created At:" + createdAt;
    }
}
