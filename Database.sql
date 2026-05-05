-- USERS TABLE
CREATE TABLE users (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);


-- PROJECTS TABLE
CREATE TABLE projects (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    created_by VARCHAR(20) NOT NULL,
    created_at DATE DEFAULT CURRENT_DATE,

    CONSTRAINT fk_project_creator
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE CASCADE
);


-- PROJECT MEMBERS TABLE (Many-to-Many)
CREATE TABLE project_members (
    project_id VARCHAR(20),
    user_id VARCHAR(20),
    role VARCHAR(20) NOT NULL,

    PRIMARY KEY (project_id, user_id),

    CONSTRAINT fk_pm_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pm_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);


-- TASKS TABLE
CREATE TABLE tasks (
    id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    due_date DATE,
    project_id VARCHAR(20) NOT NULL,
    assigned_to VARCHAR(20),
    created_by VARCHAR(20) NOT NULL,
    created_at DATE DEFAULT CURRENT_DATE,

    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_task_assigned
        FOREIGN KEY (assigned_to)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_task_creator
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE CASCADE
);

select * from users;