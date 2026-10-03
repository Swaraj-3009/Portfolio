package com.portfolio.DAO.DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.portfolio.DAO.SkillsDAO;
import com.portfolio.config.DatabaseConnection;
import com.portfolio.model.MySkills;

public class SkillsDaoImpl implements SkillsDAO {

    @Override
    public boolean addSkills(MySkills skills) {
        String sql = "INSERT INTO my_skills (skill_name, is_completed) VALUES(?, ?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, skills.getSkillName());
                ps.setBoolean(2, skills.getIsCompleted());

                int rows = ps.executeUpdate();

                if(rows > 0){
                    return true;
                }
                else{
                    return false;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<MySkills> getSkills() {
        List<MySkills> skill = new ArrayList<>();

        String sql = "SELECT * FROM my_skills";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        MySkills mySkill = new MySkills();

                        mySkill.setId(rs.getInt("id"));
                        mySkill.setSkillName(rs.getString("skill_name"));
                        mySkill.setIsCompleted(rs.getBoolean("is_completed"));

                        skill.add(mySkill);
                    }

                    return skill;
                }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateSkills(MySkills skills) {
        String sql = "UPDATE my_skills SET is_completed = ? WHERE id = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setBoolean(1, skills.getIsCompleted());
                ps.setInt(2, skills.getId());

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
    public boolean deleteSkills(MySkills skills) {
        String sql = "DELETE FROM my_skills WHERE id = ?";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1, skills.getId());

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
