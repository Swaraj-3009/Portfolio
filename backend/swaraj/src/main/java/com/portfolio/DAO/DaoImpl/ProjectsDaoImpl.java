package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.ProjectsDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.MyProject;

public class ProjectsDaoImpl implements ProjectsDAO {

    @Override
    public boolean addProject(MyProject project) {
        String sql = "INSERT INTO my_projects (project_name, project_description, technologies_used, github_url, live_url, project_image, is_completed) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getProjectDescription());
            ps.setString(3, project.getTechnologiesUsed());
            ps.setString(4, project.getGithubURL());
            ps.setString(5, project.getLiveURL());
            ps.setString(6, project.getProjectImage());
            ps.setBoolean(7, Boolean.TRUE.equals(project.getIsCompleted()));

            int rows = ps.executeUpdate();

            if(rows > 0){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<MyProject> getProject() {
        List<MyProject> projects = new ArrayList<>();
        String sql = "SELECT * FROM my_projects ORDER BY id ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MyProject project = new MyProject();
                project.setId(rs.getInt("id"));
                project.setProjectName(rs.getString("project_name"));
                project.setProjectDescription(rs.getString("project_description"));
                project.setTechnologiesUsed(rs.getString("technologies_used"));
                project.setGithubURL(rs.getString("github_url"));
                project.setLiveURL(rs.getString("live_url"));
                project.setProjectImage(rs.getString("project_image"));
                project.setIsCompleted(rs.getBoolean("is_completed"));

                projects.add(project);
            }
            return projects;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateProject(MyProject project) {
        String sql = "UPDATE my_projects SET project_name = COALESCE(?, project_name), project_description = COALESCE(?, project_description), technologies_used = COALESCE(?, technologies_used), github_url = COALESCE(?, github_url), live_url = COALESCE(?, live_url), project_image = COALESCE(?, project_image), is_completed = COALESCE(?, is_completed) WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getProjectDescription());
            ps.setString(3, project.getTechnologiesUsed());
            ps.setString(4, project.getGithubURL());
            ps.setString(5, project.getLiveURL());
            ps.setString(6, project.getProjectImage());
            ps.setObject(7, project.getIsCompleted());
            ps.setInt(8, project.getId());

            int rows = ps.executeUpdate();
            if(rows > 0){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteProject(MyProject project) {
        String sql = "DELETE FROM my_projects WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, project.getId());

            int rows = ps.executeUpdate();
            if(rows > 0){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
