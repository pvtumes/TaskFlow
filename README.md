# TaskFlow

TaskFlow is a robust, command-line-based Task Management System built in Java. It allows users to register, create projects, assign members to projects, and efficiently manage tasks within those projects. The application persists data using a PostgreSQL database, ensuring data integrity and durability across sessions.

## Features

- **User Authentication:** Register and login via a straightforward command-line interface.
- **Project Management:** Create new projects and oversee existing ones.
- **Role-Based Access Control:** Manage project members and their roles.
- **Task Management:**
  - Create tasks within specific projects.
  - Assign tasks to project members.
  - Update task status (e.g., TODO, IN_PROGRESS, DONE).
  - Set and update task priorities and due dates.
- **Database Persistence:** Utilizes PostgreSQL for robust data storage.
- **Clean Architecture:** Separated into distinct layers: Models, Repositories, Services, and a CLI Presentation layer.

## Technology Stack

- **Language:** Java
- **Database:** PostgreSQL
- **Database Connectivity:** JDBC (`postgresql-42.7.11.jar`)

## Project Structure

- `src/Model/` - Contains domain models (`User`, `Project`, `Task`, `ProjectMember`).
- `src/Repository/` - Handles data access and database operations using JDBC (`UserRepository`, `ProjectRepository`, `TaskRepository`, `ProjectMemberRepository`).
- `src/Service/` - Encapsulates core business logic and rules (`UserService`, `ProjectService`, `TaskService`).
- `src/CLI/` - Contains the command-line interface logic to interact with the user.
- `src/config/` - Manages database configuration and connections (`DatabaseConnection`).
- `Database.sql` - Contains the SQL schema definitions for the system tables.

## Setup and Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher.
- PostgreSQL Server installed and running.
- The PostgreSQL JDBC Driver (`postgresql-42.7.11.jar`) included in the project dependencies.

### Database Setup
1. Create a new PostgreSQL database.
2. Execute the SQL script provided in `Database.sql` to create the required tables:
   ```bash
   psql -U your_username -d your_database_name -f Database.sql
   ```
3. Update the database credentials (URL, username, password) in `src/config/DatabaseConnection.java` to match your local PostgreSQL setup.

### Running the Application
Compile the Java source files and run the `Main` class. Ensure the JDBC driver is included in the classpath during compilation and execution.

## Usage

Upon starting the application, you will be greeted by the CLI. Follow the on-screen prompts to register, log in, create projects, and start managing your tasks!
