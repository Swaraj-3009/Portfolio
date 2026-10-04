package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.EducationDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.MyEducation;

public class EducationDaoImpl implements EducationDAO {

    @Override
    public boolean addEducation(MyEducation education) {
        String sql = "INSERT INTO my_education (degree, institution, year_of_passing, grade, description) VALUES (?, ?, ?, ?, ?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, education.getDegree());
                ps.setString(2, education.getInstitution());
                ps.setString(3, education.getYearofPassing());
                ps.setString(4, education.getGrade());
                ps.setString(5, education.getDescription());

                int rows = ps.executeUpdate();

                if(rows > 0){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<MyEducation> getEducation() {
        List<MyEducation> educations = new ArrayList<>();
        String sql = "SELECT * FROM my_education";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        MyEducation edu = new MyEducation();

                        edu.setId(rs.getInt("id"));
                        edu.setDegree(rs.getString("degree"));
                        edu.setInstitution(rs.getString("institution"));
                        edu.setYearofPassing(rs.getString("year_of_passing"));
                        edu.setGrade(rs.getString("grade"));
                        edu.setDescription(rs.getString("description"));

                        educations.add(edu);
                    }
                    return educations;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateEducation(MyEducation education) {
        String sql = "UPDATE my_education SET degree = ?, institution = ?, year_of_passing = ?, grade = ?, description = ? WHERE id = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, education.getDegree());
                ps.setString(2, education.getInstitution());
                ps.setString(3, education.getYearofPassing());
                ps.setString(4, education.getGrade());
                ps.setString(5, education.getDescription());
                ps.setInt(6, education.getId());

                int rows = ps.executeUpdate();

                if(rows > 0){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteEducation(MyEducation education) {
        String sql = "DELETE FROM my_education WHERE id = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1, education.getId());

                int rows = ps.executeUpdate();

                if(rows > 0){
                    return true;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
}
