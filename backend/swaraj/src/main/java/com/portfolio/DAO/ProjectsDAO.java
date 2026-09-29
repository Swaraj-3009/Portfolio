package com.portfolio.DAO;

import com.portfolio.model.MyProject;

public interface ProjectsDAO {
    public void addProject(MyProject project);
    public MyProject getProject(String projectName);
    public void updateProject(MyProject project);
    public void deleteProject(MyProject project);
}
