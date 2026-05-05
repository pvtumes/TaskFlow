package Model;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class User {
    private String id;
    private String name;
    private String email;
    private String password;


    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = generateNextId();
    }


    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    private String generateNextId() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return String.format("U%02d", count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("U%02d", System.currentTimeMillis() % 1000);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User Id:" + id + "\n" +
                "Name:" + name + "\n" +
                "Email Id:" + email + "\n";
    }
}
