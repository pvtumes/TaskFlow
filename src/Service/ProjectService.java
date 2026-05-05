package Service;

import Model.Enums.UserRole;
import Model.Project;
import Model.ProjectMember;
import Model.User;
import Repository.ProjectMemberRepository;
import Repository.ProjectRepository;
import Repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }


    public boolean createProject(String name, User user) {


        if (user == null || userRepository.findById(user.getId()).isEmpty()) {
            return false;
        }


        Project newProject = new Project(name, user);
        projectRepository.save(newProject);


        ProjectMember member = new ProjectMember(
                newProject.getId(),
                user.getId(),
                UserRole.ADMIN
        );
        projectMemberRepository.save(member);

        return true;
    }


    public boolean addMember(String projectId, String userId, UserRole role) {


        if (projectRepository.findById(projectId).isEmpty()) {
            return false;
        }


        if (userRepository.findById(userId).isEmpty()) {
            return false;
        }


        if (projectMemberRepository.exists(projectId, userId)) {
            return false;
        }

        ProjectMember member = new ProjectMember(projectId, userId, role);
        projectMemberRepository.save(member);

        return true;
    }


    public List<Project> getProjectsByUser(String userId) {

        List<ProjectMember> memberships = projectMemberRepository.findByUserId(userId);
        List<Project> projects = new ArrayList<>();

        for (ProjectMember member : memberships) {
            Optional<Project> project = projectRepository.findById(member.getProjectId());
            project.ifPresent(projects::add);
        }

        return projects;
    }


    public boolean isUserPartOfProject(String projectId, String userId) {
        return projectMemberRepository.exists(projectId, userId);
    }


    public UserRole getUserRole(String projectId, String userId) {

        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        for (ProjectMember member : members) {
            if (member.getUserId().equals(userId)) {
                return member.getRole();
            }
        }

        return null;
    }


    public Project getProjectById(String projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }


    public boolean deleteProject(String projectId) {
        return projectRepository.deleteById(projectId);
    }
}