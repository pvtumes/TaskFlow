import CLI.TaskFlowCLI;
import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connected.");
        } catch (SQLException e) {
            System.out.println("Warning: could not connect to database — " + e.getMessage());
            System.out.println("Ensure PostgreSQL is running and taskflow DB exists.");
            return;
        }
        new TaskFlowCLI().start();
    }
}