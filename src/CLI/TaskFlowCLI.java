package CLI;

import Model.Enums.AuthStatus;
import Model.Enums.TaskPriority;
import Model.Enums.TaskStatus;
import Model.Enums.UserRole;
import Model.Task;
import Model.User;
import Repository.ProjectMemberRepository;
import Repository.ProjectRepository;
import Repository.TaskRepository;
import Repository.UserRepository;
import Service.ProjectService;
import Service.TaskService;
import Service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TaskFlowCLI {

    private final UserRepository userRepository             = new UserRepository();
    private final ProjectRepository projectRepository       = new ProjectRepository();
    private final ProjectMemberRepository memberRepository  = new ProjectMemberRepository();
    private final TaskRepository taskRepository             = new TaskRepository();

    private final UserService    userService    = new UserService(userRepository);
    private final ProjectService projectService = new ProjectService(projectRepository, memberRepository, userRepository);
    private final TaskService    taskService    = new TaskService(taskRepository, projectRepository, memberRepository, userRepository);

    private User currentUser = null;
    private final Scanner sc = new Scanner(System.in);

    public void start() {
        System.out.println("TaskFlow v1.0");
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = guestMenu();
            } else {
                running = mainMenu();
            }
        }
        System.out.println("Exiting.");
        sc.close();
    }

    private boolean guestMenu() {
        System.out.println("\n--- TaskFlow ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        System.out.print("> ");
        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1" -> handleRegister();
            case "2" -> handleLogin();
            case "0" -> { return false; }
            default  -> System.out.println("Invalid option.");
        }
        return true;
    }

    private boolean mainMenu() {
        System.out.println("\n--- Main Menu  [" + currentUser.getName() + " / " + currentUser.getId() + "] ---");
        System.out.println("  Project");
        System.out.println("    1.  Create Project");
        System.out.println("    2.  Add Member");
        System.out.println("    3.  My Projects");
        System.out.println("    4.  Delete Project");
        System.out.println("  Task");
        System.out.println("    5.  Create Task");
        System.out.println("    6.  Assign Task");
        System.out.println("    7.  Update Status");
        System.out.println("    8.  Update Priority");
        System.out.println("    9.  Tasks by Project");
        System.out.println("    10. My Tasks");
        System.out.println("    11. Delete Task");
        System.out.println("  Session");
        System.out.println("    12. Logout");
        System.out.println("    0.  Exit");
        System.out.print("> ");
        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1"  -> handleCreateProject();
            case "2"  -> handleAddMember();
            case "3"  -> handleViewMyProjects();
            case "4"  -> handleDeleteProject();
            case "5"  -> handleCreateTask();
            case "6"  -> handleAssignTask();
            case "7"  -> handleUpdateStatus();
            case "8"  -> handleUpdatePriority();
            case "9"  -> handleViewTasksByProject();
            case "10" -> handleViewMyTasks();
            case "11" -> handleDeleteTask();
            case "12" -> handleLogout();
            case "0"  -> { return false; }
            default   -> System.out.println("Invalid option.");
        }
        return true;
    }



    private void handleRegister() {
        System.out.println("\n[Register]");
        System.out.print("Name     : "); String name  = sc.nextLine().trim();
        System.out.print("Email    : "); String email = sc.nextLine().trim();
        System.out.print("Password : "); String pass  = sc.nextLine().trim();

        AuthStatus status = userService.registerUser(name, email, pass);
        switch (status) {
            case SUCCESS       -> System.out.println("Registered. You can now login.");
            case EMAIL_EXIST   -> System.out.println("Error: email already in use.");
            case INVALID_INPUT -> System.out.println("Error: all fields are required.");
            default            -> System.out.println("Registration failed.");
        }
    }

    private void handleLogin() {
        System.out.println("\n[Login]");
        System.out.print("Email    : "); String email = sc.nextLine().trim();
        System.out.print("Password : "); String pass  = sc.nextLine().trim();

        AuthStatus status = userService.loginUser(email, pass);
        switch (status) {
            case SUCCESS -> {
                Optional<User> opt = userRepository.findByEmail(email);
                opt.ifPresent(u -> {
                    currentUser = u;
                    System.out.println("Logged in as " + u.getName() + " (" + u.getId() + ").");
                });
            }
            case USER_NOT_FOUND   -> System.out.println("Error: no account found for that email.");
            case INVALID_PASSWORD -> System.out.println("Error: incorrect password.");
            case INVALID_INPUT    -> System.out.println("Error: email and password are required.");
            default               -> System.out.println("Login failed.");
        }
    }

    private void handleLogout() {
        System.out.println("Logged out.");
        currentUser = null;
    }



    private void handleCreateProject() {
        System.out.println("\n[Create Project]");
        System.out.print("Name: "); String name = sc.nextLine().trim();

        boolean ok = projectService.createProject(name, currentUser);
        System.out.println(ok ? "Project created. You are the ADMIN." : "Failed to create project.");
    }

    private void handleAddMember() {
        System.out.println("\n[Add Member]");
        System.out.print("Project ID : "); String projectId = sc.nextLine().trim();
        System.out.print("User ID    : "); String userId    = sc.nextLine().trim();
        System.out.print("Role (ADMIN / MEMEBR): "); String roleInput = sc.nextLine().trim();

        UserRole role;
        try {
            role = UserRole.valueOf(roleInput.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: invalid role. Use ADMIN or MEMEBR.");
            return;
        }

        if (projectService.getUserRole(projectId, currentUser.getId()) != UserRole.ADMIN) {
            System.out.println("Error: only an ADMIN of project " + projectId + " can add members.");
            return;
        }

        boolean ok = projectService.addMember(projectId, userId, role);
        System.out.println(ok ? "Member added." : "Failed. User or project may not exist, or already a member.");
    }

    private void handleViewMyProjects() {
        System.out.println("\n[My Projects]");
        List<Model.Project> projects = projectService.getProjectsByUser(currentUser.getId());
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
        } else {
            projects.forEach(System.out::println);
        }
    }

    private void handleDeleteProject() {
        System.out.println("\n[Delete Project]");
        System.out.print("Project ID: "); String projectId = sc.nextLine().trim();

        if (projectService.getUserRole(projectId, currentUser.getId()) != UserRole.ADMIN) {
            System.out.println("Error: only an ADMIN can delete this project.");
            return;
        }

        System.out.print("Confirm delete " + projectId + " (yes/no): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean ok = projectService.deleteProject(projectId);
        System.out.println(ok ? "Project deleted." : "Error: project not found.");
    }



    private void handleCreateTask() {
        System.out.println("\n[Create Task]");
        System.out.print("Title      : "); String title     = sc.nextLine().trim();
        System.out.print("Project ID : "); String projectId = sc.nextLine().trim();
        System.out.print("Assign To (User ID, blank to skip): "); String assignTo = sc.nextLine().trim();
        System.out.print("Due Date (YYYY-MM-DD, blank to skip): "); String dateStr  = sc.nextLine().trim();

        LocalDate dueDate = null;
        if (!dateStr.isBlank()) {
            try {
                dueDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Error: invalid date. Use YYYY-MM-DD.");
                return;
            }
        }

        taskService.createTask(title, dueDate, projectId, assignTo.isBlank() ? null : assignTo, currentUser.getId());
    }

    private void handleAssignTask() {
        System.out.println("\n[Assign Task]");
        System.out.print("Task ID    : "); String taskId   = sc.nextLine().trim();
        System.out.print("Assign To  : "); String assignTo = sc.nextLine().trim();

        taskService.assignTask(taskId, assignTo, currentUser.getId());
    }

    private void handleUpdateStatus() {
        System.out.println("\n[Update Status]");
        System.out.print("Task ID    : "); String taskId = sc.nextLine().trim();
        System.out.print("New Status (TODO / IN_PROGRESS / DONE): ");

        TaskStatus newStatus;
        try {
            newStatus = TaskStatus.valueOf(sc.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: invalid status.");
            return;
        }

        taskService.updateStatus(taskId, newStatus, currentUser.getId());
    }

    private void handleUpdatePriority() {
        System.out.println("\n[Update Priority]");
        System.out.print("Task ID      : "); String taskId = sc.nextLine().trim();
        System.out.print("New Priority (LOW / MEDIUM / HIGH): ");

        TaskPriority newPriority;
        try {
            newPriority = TaskPriority.valueOf(sc.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: invalid priority.");
            return;
        }

        taskService.updatePriority(taskId, newPriority, currentUser.getId());
    }

    private void handleViewTasksByProject() {
        System.out.println("\n[Tasks by Project]");
        System.out.print("Project ID: "); String projectId = sc.nextLine().trim();

        List<Task> tasks = taskService.getTasksByProject(projectId, currentUser.getId());
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            tasks.forEach(System.out::println);
        }
    }

    private void handleViewMyTasks() {
        System.out.println("\n[My Tasks]");
        List<Task> tasks = taskService.getTasksByUser(currentUser.getId(), currentUser.getId());
        if (tasks.isEmpty()) {
            System.out.println("No tasks assigned to you.");
        } else {
            tasks.forEach(System.out::println);
        }
    }

    private void handleDeleteTask() {
        System.out.println("\n[Delete Task]");
        System.out.print("Task ID: "); String taskId = sc.nextLine().trim();

        System.out.print("Confirm delete " + taskId + " (yes/no): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Cancelled.");
            return;
        }

        taskService.deleteTask(taskId, currentUser.getId());
    }
}
