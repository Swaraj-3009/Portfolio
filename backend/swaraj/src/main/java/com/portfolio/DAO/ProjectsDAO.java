package com.portfolio.DAO;

import java.util.List;

import com.portfolio.model.MyProject;

public interface ProjectsDAO {
    public boolean addProject(MyProject project);
    public List<MyProject> getProject();
    public boolean updateProject(MyProject project);
    public boolean deleteProject(MyProject project);
}
