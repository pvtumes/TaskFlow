package Service;

import Model.Enums.TaskPriority;
import Model.Enums.TaskStatus;
import Model.Enums.UserRole;
import Model.Task;
import Repository.ProjectMemberRepository;
import Repository.ProjectRepository;
import Repository.TaskRepository;
import Repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       ProjectMemberRepository projectMemberRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public boolean createTask(String title, LocalDate dueDate, String projectId, String assignedTo, String createdBy) {
        if (title == null || title.isBlank()) {
            System.out.println("Error: task title cannot be empty.");
            return false;
        }
        if (projectRepository.findById(projectId).isEmpty()) {
            System.out.println("Error: project " + projectId + " does not exist.");
            return false;
        }
        if (userRepository.findById(createdBy).isEmpty()) {
            System.out.println("Error: user " + createdBy + " does not exist.");
            return false;
        }
        if (!projectMemberRepository.exists(projectId, createdBy)) {
            System.out.println("Error: user " + createdBy + " is not a member of project " + projectId + ".");
            return false;
        }
        if (assignedTo != null && !assignedTo.isBlank()) {
            if (userRepository.findById(assignedTo).isEmpty()) {
                System.out.println("Error: assignee " + assignedTo + " does not exist.");
                return false;
            }
            if (!projectMemberRepository.exists(projectId, assignedTo)) {
                System.out.println("Error: assignee " + assignedTo + " is not a member of project " + projectId + ".");
                return false;
            }
        }

        Task task = new Task(
                title,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                dueDate,
                projectId,
                (assignedTo != null && !assignedTo.isBlank()) ? assignedTo : null,
                createdBy
        );
        taskRepository.save(task);
        System.out.println("Task " + task.getId() + " created.");
        return true;
    }

    public boolean assignTask(String taskId, String assignedTo, String assignedBy) {
        Optional<Task> optTask = taskRepository.findById(taskId);
        if (optTask.isEmpty()) {
            System.out.println("Error: task " + taskId + " does not exist.");
            return false;
        }
        Task task = optTask.get();

        if (task.getStatus() == TaskStatus.DONE) {
            System.out.println("Error: cannot reassign a completed task.");
            return false;
        }

        String projectId = task.getProjectId();

        if (userRepository.findById(assignedBy).isEmpty()) {
            System.out.println("Error: user " + assignedBy + " does not exist.");
            return false;
        }
        if (getMemberRole(projectId, assignedBy) != UserRole.ADMIN) {
            System.out.println("Error: user " + assignedBy + " is not an ADMIN of project " + projectId + ".");
            return false;
        }
        if (userRepository.findById(assignedTo).isEmpty()) {
            System.out.println("Error: user " + assignedTo + " does not exist.");
            return false;
        }
        if (!projectMemberRepository.exists(projectId, assignedTo)) {
            System.out.println("Error: user " + assignedTo + " is not a member of project " + projectId + ".");
            return false;
        }

        task.setAssignedTo(assignedTo);
        taskRepository.save(task);
        System.out.println("Task " + taskId + " assigned to " + assignedTo + ".");
        return true;
    }

    public boolean updateStatus(String taskId, TaskStatus newStatus, String requestedBy) {
        Optional<Task> optTask = taskRepository.findById(taskId);
        if (optTask.isEmpty()) {
            System.out.println("Error: task " + taskId + " does not exist.");
            return false;
        }
        Task task = optTask.get();

        if (userRepository.findById(requestedBy).isEmpty()) {
            System.out.println("Error: user " + requestedBy + " does not exist.");
            return false;
        }
        if (task.getAssignedTo() == null || task.getAssignedTo().isBlank()) {
            System.out.println("Error: task " + taskId + " has no assignee.");
            return false;
        }
        if (!task.getAssignedTo().equals(requestedBy)) {
            System.out.println("Error: only the assigned user can update this task's status.");
            return false;
        }

        TaskStatus current = task.getStatus();
        if (!isValidTransition(current, newStatus)) {
            System.out.println("Error: invalid transition " + current + " -> " + newStatus + ". Allowed: TODO -> IN_PROGRESS -> DONE.");
            return false;
        }

        task.setStatus(newStatus);
        taskRepository.save(task);
        System.out.println("Task " + taskId + " status changed from " + current + " to " + newStatus + ".");
        return true;
    }

    public boolean updatePriority(String taskId, TaskPriority newPriority, String requestedBy) {
        Optional<Task> optTask = taskRepository.findById(taskId);
        if (optTask.isEmpty()) {
            System.out.println("Error: task " + taskId + " does not exist.");
            return false;
        }
        Task task = optTask.get();

        if (task.getStatus() == TaskStatus.DONE) {
            System.out.println("Error: cannot modify a completed task.");
            return false;
        }
        if (userRepository.findById(requestedBy).isEmpty()) {
            System.out.println("Error: user " + requestedBy + " does not exist.");
            return false;
        }

        UserRole role = getMemberRole(task.getProjectId(), requestedBy);
        boolean isAdmin   = role == UserRole.ADMIN;
        boolean isCreator = task.getCreatedBy().equals(requestedBy);

        if (!isAdmin && !isCreator) {
            System.out.println("Error: only an ADMIN or the task creator can change priority.");
            return false;
        }

        TaskPriority old = task.getPriority();
        task.setPriority(newPriority);
        taskRepository.save(task);
        System.out.println("Task " + taskId + " priority changed from " + old + " to " + newPriority + ".");
        return true;
    }

    public List<Task> getTasksByProject(String projectId, String requestedBy) {
        if (projectRepository.findById(projectId).isEmpty()) {
            System.out.println("Error: project " + projectId + " does not exist.");
            return List.of();
        }
        if (userRepository.findById(requestedBy).isEmpty()) {
            System.out.println("Error: user " + requestedBy + " does not exist.");
            return List.of();
        }
        if (!projectMemberRepository.exists(projectId, requestedBy)) {
            System.out.println("Error: access denied. User " + requestedBy + " is not a member of project " + projectId + ".");
            return List.of();
        }
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getTasksByUser(String userId, String requestedBy) {
        if (userRepository.findById(userId).isEmpty()) {
            System.out.println("Error: user " + userId + " does not exist.");
            return List.of();
        }
        if (userRepository.findById(requestedBy).isEmpty()) {
            System.out.println("Error: user " + requestedBy + " does not exist.");
            return List.of();
        }
        if (!userId.equals(requestedBy)) {
            System.out.println("Error: access denied.");
            return List.of();
        }
        return taskRepository.findByAssignedUser(userId);
    }

    public boolean deleteTask(String taskId, String requestedBy) {
        Optional<Task> optTask = taskRepository.findById(taskId);
        if (optTask.isEmpty()) {
            System.out.println("Error: task " + taskId + " does not exist.");
            return false;
        }
        Task task = optTask.get();

        if (userRepository.findById(requestedBy).isEmpty()) {
            System.out.println("Error: user " + requestedBy + " does not exist.");
            return false;
        }
        if (getMemberRole(task.getProjectId(), requestedBy) != UserRole.ADMIN) {
            System.out.println("Error: only an ADMIN can delete tasks.");
            return false;
        }
        if (task.getStatus() == TaskStatus.IN_PROGRESS || task.getStatus() == TaskStatus.DONE) {
            System.out.println("Error: cannot delete a task with status " + task.getStatus() + ".");
            return false;
        }

        taskRepository.deleteById(taskId);
        System.out.println("Task " + taskId + " deleted.");
        return true;
    }

    private UserRole getMemberRole(String projectId, String userId) {
        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .filter(m -> m.getUserId().equals(userId))
                .map(m -> m.getRole())
                .findFirst()
                .orElse(null);
    }

    private boolean isValidTransition(TaskStatus current, TaskStatus next) {
        switch (current) {
            case TODO:        return next == TaskStatus.IN_PROGRESS;
            case IN_PROGRESS: return next == TaskStatus.DONE;
            case DONE:        return false;
            default:          return false;
        }
    }
}
