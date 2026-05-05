package Model;

import Model.Enums.UserRole;

public class ProjectMember {
    private String projectId;
    private String userId;
    private UserRole role;


    public ProjectMember(String projectId, String userId, UserRole role) {
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    @Override
    public String toString(){
        return "Project Id:"+projectId+"\n"+
                "User Id:"+userId+"\n"+
                "Role:"+role+"\n";
    }
}
